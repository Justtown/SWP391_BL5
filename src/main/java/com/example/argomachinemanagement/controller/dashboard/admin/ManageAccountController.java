/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.example.argomachinemanagement.controller.dashboard.admin;

import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@WebServlet(name = "ManageAccountController", urlPatterns = { "/manage-account" })
public class ManageAccountController extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.equals("list")) {
            handleListWithFilters(request, response);
        } else {
            switch (action) {
                case "add":
                    showAddForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "deactivate":
                    deactivateAccount(request, response);
                    break;
                default:
                    handleListWithFilters(request, response);
                    break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default action
        }

        switch (action) {
            case "add":
                addUser(request, response);
                break;
            case "update":
                updateAccount(request, response);
                break;
            default:
                handleListWithFilters(request, response);
                break;
        }
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy danh sách roles
        List<String> roles = userDAO.getAllRoles();
        request.setAttribute("roles", roles);
        
        // Forward to add user form
        request.getRequestDispatcher("/view/dashboard/admin/add-user.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
     
        response.sendRedirect(request.getContextPath() + "/manage-account");
    }

    private void handleListWithFilters(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get filter parameters
        String roleFilter = request.getParameter("role");
        String statusFilter = request.getParameter("status");
        String keyword = request.getParameter("keyword");
        
        // Get all users from database
        List<User> allUsers = userDAO.findAll();
        
        // Apply filters
        List<User> filteredUsers = new ArrayList<>();
        
        for (User user : allUsers) {
            boolean match = true;
            
            // Filter by role
            if (roleFilter != null && !roleFilter.isEmpty() && !roleFilter.equals("All Role")) {
                if (user.getRoleName() == null || !roleFilter.equals(user.getRoleName())) {
                    match = false;
                }
            }
            
            // Filter by status
            if (match && statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("All Status")) {
                int status = statusFilter.equals("Active") ? 1 : 0;
                if (user.getStatus() == null || user.getStatus() != status) {
                    match = false;
                }
            }
            
            // Filter by keyword
            if (match && keyword != null && !keyword.trim().isEmpty()) {
                String searchKeyword = keyword.toLowerCase().trim();
                boolean found = false;
                
                if (user.getFullName() != null && user.getFullName().toLowerCase().contains(searchKeyword)) {
                    found = true;
                } else if (user.getUsername() != null && user.getUsername().toLowerCase().contains(searchKeyword)) {
                    found = true;
                } else if (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchKeyword)) {
                    found = true;
                } else if (user.getRoleName() != null && user.getRoleName().toLowerCase().contains(searchKeyword)) {
                    found = true;
                }
                
                if (!found) {
                    match = false;
                }
            }
            
            if (match) {
                filteredUsers.add(user);
            }
        }
        
        // Set attributes for JSP
        request.setAttribute("users", filteredUsers);
        request.setAttribute("roleFilter", roleFilter != null ? roleFilter : "All Role");
        request.setAttribute("statusFilter", statusFilter != null ? statusFilter : "All Status");
        request.setAttribute("keyword", keyword != null ? keyword : "");
        
        // Forward to JSP
        request.getRequestDispatcher("/view/dashboard/admin/user-management.jsp").forward(request, response);
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response)
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
    
    private void updateAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
    }

    private void deactivateAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    }


}
