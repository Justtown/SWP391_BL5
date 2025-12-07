package com.example.argomachinemanagement.controller.authen;

import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.dal.ProfileDAO;
import com.example.argomachinemanagement.entity.User;
import com.example.argomachinemanagement.entity.Profile;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private ProfileDAO profileDAO;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{9,15}$");
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        profileDAO = new ProfileDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        request.getRequestDispatcher("/view/authen/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // Get form parameters
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String phoneCode = request.getParameter("phoneCode");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String birthdateStr = request.getParameter("birthdate");
        
        // Store form data for re-display on error
        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.setAttribute("fullName", fullName);
        request.setAttribute("phoneCode", phoneCode);
        request.setAttribute("phoneNumber", phoneNumber);
        request.setAttribute("address", address);
        request.setAttribute("birthdate", birthdateStr);
        
        // Validation
        StringBuilder errors = new StringBuilder();
        
        // Required fields validation
        if (username == null || username.trim().isEmpty()) {
            errors.append("Username là bắt buộc. ");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            errors.append("Họ tên là bắt buộc. ");
        }
        if (email == null || email.trim().isEmpty()) {
            errors.append("Email là bắt buộc. ");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.append("Email không hợp lệ. ");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.append("Mật khẩu là bắt buộc. ");
        } else if (password.length() < 6) {
            errors.append("Mật khẩu phải có ít nhất 6 ký tự. ");
        }
        if (confirmPassword == null || !confirmPassword.equals(password)) {
            errors.append("Mật khẩu xác nhận không khớp. ");
        }
        
        // Phone validation (optional but if provided must be valid)
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(phoneNumber.trim()).matches()) {
                errors.append("Số điện thoại không hợp lệ. ");
            }
        }
        
        // Birthdate validation
        Date birthdate = null;
        if (birthdateStr != null && !birthdateStr.trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                java.util.Date parsed = sdf.parse(birthdateStr);
                birthdate = new Date(parsed.getTime());
                
                // Check if birthdate is in the future
                if (birthdate.after(new java.util.Date())) {
                    errors.append("Ngày sinh không thể là ngày trong tương lai. ");
                }
            } catch (ParseException e) {
                errors.append("Ngày sinh không hợp lệ. ");
            }
        }
        
        // Check if username already exists
        if (username != null && !username.trim().isEmpty()) {
            User existingUser = userDAO.findByUsername(username.trim());
            if (existingUser != null) {
                errors.append("Username đã tồn tại. ");
            }
        }
        
        // Check if email already exists
        if (email != null && !email.trim().isEmpty()) {
            User existingEmail = userDAO.findByEmail(email.trim());
            if (existingEmail != null) {
                errors.append("Email đã được sử dụng. ");
            }
        }
        
        // If there are errors, return to register page
        if (errors.length() > 0) {
            request.setAttribute("error", errors.toString());
            request.getRequestDispatcher("/view/authen/register.jsp").forward(request, response);
            return;
        }
        
        // Build full phone number with country code
        String fullPhoneNumber = null;
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            fullPhoneNumber = (phoneCode != null ? phoneCode : "+84") + phoneNumber.trim();
        }
        
        // Create new user
        User newUser = User.builder()
                .username(username.trim())
                .password(password) // Will be encoded in DAO
                .fullName(fullName.trim())
                .email(email.trim())
                .phoneNumber(fullPhoneNumber)
                .address(address != null ? address.trim() : null)
                .birthdate(birthdate)
                .status(1) // Active by default
                .build();
        
        // Insert user into database
        int userId = userDAO.insert(newUser);
        
        if (userId > 0) {
            // Tạo profile cho user mới
            Profile newProfile = new Profile();
            newProfile.setUserId(userId);
            newProfile.setName(fullName.trim());
            newProfile.setEmail(email.trim());
            newProfile.setPhone(fullPhoneNumber);
            newProfile.setAddress(address != null ? address.trim() : null);
            newProfile.setBirthdate(birthdate);
            newProfile.setAvatar(null);
            
            // Tạo profile trong database
            boolean profileCreated = profileDAO.createProfile(newProfile);
            
            if (!profileCreated) {
                // Log warning nhưng vẫn cho phép đăng ký thành công
                System.out.println("Warning: Failed to create profile for user ID: " + userId);
            }
            
            // Success - redirect to login with success message
            HttpSession session = request.getSession(true);
            session.setAttribute("registerSuccess", "Đăng ký thành công! Vui lòng đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            // Failed to insert
            request.setAttribute("error", "Đã có lỗi xảy ra. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/view/authen/register.jsp").forward(request, response);
        }
    }
    
    @Override
    public String getServletInfo() {
        return "Register Servlet";
    }
}

