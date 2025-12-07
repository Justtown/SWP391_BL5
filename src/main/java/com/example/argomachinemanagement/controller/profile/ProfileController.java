package com.example.argomachinemanagement.controller.profile;

import com.example.argomachinemanagement.dal.ProfileDAO;
import com.example.argomachinemanagement.entity.Profile;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;

@WebServlet(name = "ProfileController", urlPatterns = {"/my-profile"})
public class ProfileController extends HttpServlet {
    
    private ProfileDAO profileDAO;
    
    @Override
    public void init() throws ServletException {
        profileDAO = new ProfileDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        
        // Lấy user ID từ session (nếu không có sẽ dùng user ID = 1 mặc định)
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            userId = 1; // User mặc định
        }
        
        // Lấy thông tin profile
        Profile profile = profileDAO.getProfileByUserId(userId);
        
        // Nếu không có profile, tạo profile mặc định
        if (profile == null) {
            profile = new Profile();
            profile.setUserId(userId);
            profile.setName("Guest User");
            profile.setEmail("guest@example.com");
            profile.setPhone("");
            profile.setAddress("");
            profile.setAvatar("");
            profile.setRoleName("Customer");
        }
        
        // Set default values nếu null
        if (profile.getPhone() == null) profile.setPhone("");
        if (profile.getAddress() == null) profile.setAddress("");
        if (profile.getAvatar() == null) profile.setAvatar("");
        if (profile.getRoleName() == null) profile.setRoleName("Customer");
        
        request.setAttribute("profile", profile);
        request.getRequestDispatcher("/view/profile/my-profile.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        
        // Lấy user ID từ session (nếu không có sẽ dùng user ID = 1 mặc định)
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            userId = 1; // User mặc định
        }
        
        String action = request.getParameter("action");
        
        if ("update".equals(action)) {
            // Lấy dữ liệu từ form
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String avatar = request.getParameter("avatar");
            String birthdateStr = request.getParameter("birthdate");
            
            // Validate
            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("error", "Tên không được để trống!");
                doGet(request, response);
                return;
            }
            
            name = name.trim();
            if (name.length() < 2 || name.length() > 100) {
                request.setAttribute("error", "Tên phải có từ 2-100 ký tự!");
                doGet(request, response);
                return;
            }
            
            if (email == null || email.trim().isEmpty()) {
                request.setAttribute("error", "Email không được để trống!");
                doGet(request, response);
                return;
            }
            
            email = email.trim();
            if (email.length() > 255) {
                request.setAttribute("error", "Email không được vượt quá 255 ký tự!");
                doGet(request, response);
                return;
            }
            
            // Validate email format
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            if (!email.matches(emailRegex)) {
                request.setAttribute("error", "Email không đúng định dạng! Ví dụ: example@gmail.com");
                doGet(request, response);
                return;
            }
            
            // Validate Phone
            if (phone != null && !phone.trim().isEmpty()) {
                phone = phone.trim();
                String phoneDigits = phone.replaceAll("[^0-9]", "");
                if (phoneDigits.length() < 10 || phoneDigits.length() > 11) {
                    request.setAttribute("error", "Số điện thoại phải có từ 10-11 chữ số!");
                    doGet(request, response);
                    return;
                }
            }
            
            // Validate Address
            if (address != null && address.length() > 500) {
                request.setAttribute("error", "Địa chỉ không được vượt quá 500 ký tự!");
                doGet(request, response);
                return;
            }
            
            // Validate Birthdate
            Date birthdate = null;
            if (birthdateStr != null && !birthdateStr.trim().isEmpty()) {
                try {
                    birthdate = Date.valueOf(birthdateStr);
                    Date today = new Date(System.currentTimeMillis());
                    
                    if (birthdate.after(today)) {
                        request.setAttribute("error", "Ngày sinh không được là tương lai!");
                        doGet(request, response);
                        return;
                    }
                    
                    long diffInMillies = today.getTime() - birthdate.getTime();
                    long diffInYears = diffInMillies / (1000L * 60 * 60 * 24 * 365);
                    
                    if (diffInYears < 13) {
                        request.setAttribute("error", "Bạn phải ít nhất 13 tuổi!");
                        doGet(request, response);
                        return;
                    }
                    
                    if (diffInYears > 120) {
                        request.setAttribute("error", "Ngày sinh không hợp lệ!");
                        doGet(request, response);
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    request.setAttribute("error", "Ngày sinh không hợp lệ!");
                    doGet(request, response);
                    return;
                }
            }
            
            // Tạo Profile object để update
            Profile profile = new Profile();
            profile.setUserId(userId);
            profile.setName(name);
            profile.setEmail(email);
            profile.setPhone(phone != null ? phone.trim() : "");
            profile.setAddress(address != null ? address.trim() : "");
            profile.setAvatar(avatar != null ? avatar.trim() : "");
            profile.setBirthdate(birthdate);
            
            // Lưu profile (tạo mới hoặc cập nhật)
            boolean success = profileDAO.saveProfile(profile);
            
            if (success) {
                request.setAttribute("success", "Cập nhật thông tin thành công!");
            } else {
                request.setAttribute("error", "Cập nhật thông tin thất bại!");
            }
        }
        
        // Load lại trang
        doGet(request, response);
    }
}

