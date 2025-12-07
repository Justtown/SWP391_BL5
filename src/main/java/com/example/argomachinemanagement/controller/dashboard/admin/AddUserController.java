package com.example.argomachinemanagement.controller.dashboard.admin;

import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        String phone = request.getParameter("phone");
        String dob = request.getParameter("dob");
        String password = request.getParameter("password");
        String roleName = request.getParameter("role");
        String statusStr = request.getParameter("status");
        
        // Validate
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Full name is required!");
            showAddFormWithData(request, response, fullName, email, phone, dob, roleName, statusStr);
            return;
        }
        
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email is required!");
            showAddFormWithData(request, response, fullName, email, phone, dob, roleName, statusStr);
            return;
        }
        
        // Validate email format
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailRegex)) {
            request.setAttribute("errorMessage", "Invalid email format!");
            showAddFormWithData(request, response, fullName, email, phone, dob, roleName, statusStr);
            return;
        }
        
        if (password == null || password.trim().isEmpty() || password.length() < 6) {
            request.setAttribute("errorMessage", "Password must be at least 6 characters!");
            showAddFormWithData(request, response, fullName, email, phone, dob, roleName, statusStr);
            return;
        }
        
        if (roleName == null || roleName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please select a role!");
            showAddFormWithData(request, response, fullName, email, phone, dob, roleName, statusStr);
            return;
        }
        
        // Parse status
        int status = 1; // default active
        if (statusStr != null && statusStr.equals("0")) {
            status = 0;
        }
        
        // Tạo User object
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPassword(password);
        user.setStatus(status);
        user.setRoleName(roleName);
        
        // Insert vào database
        int userId = userDAO.insert(user);
        
        if (userId > 0) {
            // Success - redirect to user management page
            response.sendRedirect(request.getContextPath() + "/manage-account?success=User added successfully");
        } else {
            // Error
            request.setAttribute("errorMessage", "Failed to add user. Please try again!");
            showAddFormWithData(request, response, fullName, email, phone, dob, roleName, statusStr);
        }
    }
    
    private void showAddFormWithData(HttpServletRequest request, HttpServletResponse response,
                                    String fullName, String email, String phone, String dob,
                                    String roleName, String status) throws ServletException, IOException {
        List<String> roles = userDAO.getAllRoles();
        request.setAttribute("roles", roles);
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);
        request.setAttribute("dob", dob);
        request.setAttribute("selectedRole", roleName);
        request.setAttribute("status", status);
        request.getRequestDispatcher("/view/dashboard/admin/add-user.jsp").forward(request, response);
    }
}

