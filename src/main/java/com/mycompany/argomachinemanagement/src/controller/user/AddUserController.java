package com.mycompany.argomachinemanagement.src.controller.user;

import com.mycompany.argomachinemanagement.src.dal.AccountDAO;
import com.mycompany.argomachinemanagement.src.entity.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller for adding new user
 */
@WebServlet(name = "AddUserController", urlPatterns = {"/add-user"})
public class AddUserController extends HttpServlet {

    private AccountDAO accountDAO;

    @Override
    public void init() throws ServletException {
        accountDAO = new AccountDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get all roles for dropdown
        List<String> roles = accountDAO.getAllRoleNames();
        request.setAttribute("roles", roles);
        
        request.getRequestDispatcher("/view/user/add-user.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form parameters
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        // String dob = request.getParameter("dob"); // Not stored in database yet
        String password = request.getParameter("password");
        String roleName = request.getParameter("role");
        String statusStr = request.getParameter("status");
        
        // Validate required fields
        if (fullName == null || fullName.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            roleName == null || roleName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please fill in all required fields");
            List<String> roles = accountDAO.getAllRoleNames();
            request.setAttribute("roles", roles);
            request.getRequestDispatcher("/view/user/add-user.jsp").forward(request, response);
            return;
        }
        
        // Get role ID from role name
        Integer roleId = accountDAO.getRoleIdByName(roleName);
        if (roleId == null) {
            request.setAttribute("errorMessage", "Invalid role selected");
            List<String> roles = accountDAO.getAllRoleNames();
            request.setAttribute("roles", roles);
            request.getRequestDispatcher("/view/user/add-user.jsp").forward(request, response);
            return;
        }
        
        // Create username from email (before @)
        String username = email.substring(0, email.indexOf("@"));
        
        // Check if username already exists
        if (accountDAO.findByUsername(username) != null) {
            // If username exists, append number
            int counter = 1;
            String originalUsername = username;
            while (accountDAO.findByUsername(username) != null) {
                username = originalUsername + counter;
                counter++;
            }
        }
        
        // Parse status
        boolean isActive = statusStr != null && statusStr.equals("1");
        
        // Create Account object
        Account account = Account.builder()
            .username(username)
            .password(password) // In production, should hash password
            .fullName(fullName.trim())
            .email(email.trim())
            .phone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null)
            .isActive(isActive)
            .roleId(roleId)
            .build();
        
        // Insert account
        int result = accountDAO.insert(account);
        
        if (result > 0) {
            // Success - redirect to list user page
            response.sendRedirect(request.getContextPath() + "/list-user");
        } else {
            // Error
            request.setAttribute("errorMessage", "Failed to add user. Please try again.");
            List<String> roles = accountDAO.getAllRoleNames();
            request.setAttribute("roles", roles);
            request.getRequestDispatcher("/view/user/add-user.jsp").forward(request, response);
        }
    }
}

