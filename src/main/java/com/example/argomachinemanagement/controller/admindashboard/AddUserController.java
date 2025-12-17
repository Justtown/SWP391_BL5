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
import java.util.List;

/**
 * Controller xử lý việc thêm user mới
 * URL: /add-user
 */
@WebServlet(name = "AddUserController", urlPatterns = { "/add-user" })
public class AddUserController extends HttpServlet {
    
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
        
        // Validate
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Full name is required!");
            showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
            return;
        }
        
        if (email == null || email.trim().isEmpty()) {
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
        
        // Validate username if provided
        if (username != null && !username.trim().isEmpty()) {
            username = username.trim();
            if (username.length() > 100) {
                request.setAttribute("errorMessage", "Username must not exceed 100 characters!");
                showAddFormWithData(request, response, fullName, email, username, phone, address, dob, roleName, statusStr);
                return;
            }
            // Check if username already exists
            if (userDAO.findByUsername(username) != null) {
                request.setAttribute("errorMessage", "Username already exists! Please choose another one.");
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
        if (confirmPassword == null || !confirmPassword.equals(password)) {
            request.setAttribute("errorMessage", "Confirm password must match password!");
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
        
        // Tạo User object
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setUsername(username != null && !username.trim().isEmpty() ? username.trim() : null);
        user.setPassword(password);
        user.setStatus(status);
        user.setRoleName(roleName);
        user.setPhoneNumber(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
        user.setAddress(address != null && !address.trim().isEmpty() ? address.trim() : null);
        user.setBirthdate(birthdate);
        
        // Insert vào database
        int userId = userDAO.insert(user);
        
        if (userId > 0) {
            // Success - redirect to user management page
            response.sendRedirect(request.getContextPath() + "/admin/manage-account?success=User added successfully");
        } else {
            // Error
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

