package com.example.argomachinemanagement.controller.admindashboard;

import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller xử lý việc thêm user mới
 * URL: /add-user
 */
@WebServlet(name = "AddUserController", urlPatterns = { "/add-user", "/admin/add-user" })
public class AddUserController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AddUserController.class.getName());
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy danh sách roles để hiển thị trong dropdown
        List<String> roles = userDAO.getAllRoles();
        request.setAttribute("roles", roles);
        
        // Forward to add user form
        request.getRequestDispatcher("/view/dashboard/admin/add-user.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Lấy dữ liệu từ form
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String dob = request.getParameter("dob");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String roleName = request.getParameter("role");
        String statusStr = request.getParameter("status");

        // Normalize basic inputs
        fullName = fullName != null ? fullName.trim() : null;
        // normalize email to lower-case to enforce uniqueness regardless of case
        email = email != null ? email.trim().toLowerCase() : null;
        username = username != null ? username.trim() : null;
        phone = phone != null ? phone.trim() : null;
        
        // Validate
        if (fullName == null || fullName.isEmpty()) {
            request.setAttribute("errorMessage", "Full name is required!");
            showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
            return;
        }
        
        if (email == null || email.isEmpty()) {
            request.setAttribute("errorMessage", "Email is required!");
            showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
            return;
        }
        
        // Validate email format
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailRegex)) {
            request.setAttribute("errorMessage", "Invalid email format!");
            showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
            return;
        }

        // Check if email already exists (use COUNT to avoid ResultSet mapping issues)
        if (userDAO.isEmailExists(email)) {
            LOGGER.warning("[AddUser][DUPLICATE] Email already exists: " + email);
            request.setAttribute("errorMessage", "Email already exists! Please use another one.");
            showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
            return;
        }
        
        // Validate username if provided
        if (username != null && !username.isEmpty()) {
            if (username.length() > 100) {
                request.setAttribute("errorMessage", "Username must not exceed 100 characters!");
                showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
                return;
            }
            // Validate username format: 3-100 chars, only letters/numbers/._-
            String usernameRegex = "^[a-zA-Z0-9._-]{3,100}$";
            if (!username.matches(usernameRegex)) {
                request.setAttribute("errorMessage", "Invalid username! Use 3-100 characters and only letters, numbers, dot (.), underscore (_), hyphen (-).");
                showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
                return;
            }
            // Check if username already exists
            if (userDAO.isUsernameExistsPublic(username)) {
                LOGGER.warning("[AddUser][DUPLICATE] Username already exists: " + username);
                request.setAttribute("errorMessage", "Username already exists! Please choose another one.");
                showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
                return;
            }
        }

        // Validate phone if provided + check duplicate
        if (phone != null && !phone.isEmpty()) {
            // Basic length guard (after removing separators)
            String phoneNormalized = phone.replaceAll("[\\s\\-().+]", "");
            if (phoneNormalized.length() < 9 || phoneNormalized.length() > 15) {
                request.setAttribute("errorMessage", "Invalid phone number! Please enter 9-15 digits.");
                showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
                return;
            }
            if (userDAO.isPhoneExists(phone)) {
                LOGGER.warning("[AddUser][DUPLICATE] Phone already exists: " + phone);
                request.setAttribute("errorMessage", "Phone already exists! Please use another one.");
                showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
                return;
            }
        }
        
        // Validate address length
        if (address != null && address.length() > 500) {
            request.setAttribute("errorMessage", "Address must not exceed 500 characters!");
            showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
            return;
        }
        
        if (password == null || password.trim().isEmpty() || password.length() < 6) {
            request.setAttribute("errorMessage", "Password must be at least 6 characters!");
            showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
            return;
        }
        
        // Validate confirm password
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please confirm your password!");
            showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match! Please check and try again.");
            showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
            return;
        }
        
        if (roleName == null || roleName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please select a role!");
            showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
            return;
        }
        
        // Parse status
        int status = 1; // default active
        if (statusStr != null && statusStr.equals("0")) {
            status = 0;
        }
        
        // Parse DOB
        Date birthdate = null;
        if (dob != null && !dob.trim().isEmpty()) {
            try {
                birthdate = Date.valueOf(dob);
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", "Invalid date format!");
                showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
                return;
            }
        }

        // Validate DOB must be before today (no future or today)
        if (birthdate != null) {
            LocalDate birthLocalDate = birthdate.toLocalDate();
            LocalDate today = LocalDate.now();
            if (!birthLocalDate.isBefore(today)) {
                request.setAttribute("errorMessage", "Date of birth must be before today!");
                showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
                return;
            }
        }
        
        // Tạo User object
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setUsername(username != null && !username.isEmpty() ? username : null);
        user.setPassword(password);
        user.setStatus(status);
        user.setRoleName(roleName);
        user.setPhoneNumber(phone != null && !phone.isEmpty() ? phone : null);
        user.setAddress(address != null && !address.trim().isEmpty() ? address.trim() : null);
        user.setBirthdate(birthdate);
        
        // Insert vào database
        int userId = userDAO.insert(user);
        
        if (userId > 0) {
            // Success - redirect to user management page
            response.sendRedirect(request.getContextPath() + "/admin/manage-account?success=User added successfully");
        } else {
            // Error
            LOGGER.warning("[AddUser][FAILED] Insert failed. email=" + email + ", username=" + username);
            request.setAttribute("errorMessage", "Failed to add user. Please try again!");
            showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
        }
    }
    
    private void showAddFormWithData(HttpServletRequest request, HttpServletResponse response,
                                    String fullName, String email, String username, String phone, 
                                    String address, String dob, String roleName, String status) 
            throws ServletException, IOException {
        List<String> roles = userDAO.getAllRoles();
        request.setAttribute("roles", roles);
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("username", username);
        request.setAttribute("phone", phone);
        request.setAttribute("address", address);
        request.setAttribute("dob", dob);
        request.setAttribute("selectedRole", roleName);
        request.setAttribute("status", status);
        request.getRequestDispatcher("/view/dashboard/admin/add-user.jsp").forward(request, response);
    }
}

