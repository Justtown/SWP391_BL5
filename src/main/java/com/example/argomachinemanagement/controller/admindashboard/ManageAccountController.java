/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.example.argomachinemanagement.controller.admindashboard;

import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@WebServlet(name = "ManageAccountController", urlPatterns = { "/manage-account" })
public class ManageAccountController extends HttpServlet {
    
    private static final int PAGE_SIZE = 5;
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra authentication và authorization
        if (!isAdmin(request, response)) {
            return; // Đã redirect trong isAdmin()
        }
        
        String action = request.getParameter("action");
        if (action == null || action.equals("list")) {
            handleListWithFilters(request, response);
        } else {
            switch (action) {
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
        // Kiểm tra authentication và authorization
        if (!isAdmin(request, response)) {
            return; // Đã redirect trong isAdmin()
        }
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default action
        }

        switch (action) {
            case "update":
                updateAccount(request, response);
                break;
            default:
                handleListWithFilters(request, response);
                break;
        }
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
        
        // Get pagination parameter
        String pageStr = request.getParameter("page");
        int currentPage = 1;
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageStr);
                if (currentPage < 1) {
                    currentPage = 1;
                }
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        
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
        
        // Pagination logic
        int totalUsers = filteredUsers.size();
        int totalPages = (int) Math.ceil((double) totalUsers / PAGE_SIZE);
        
        // Ensure currentPage is within valid range
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        
        // Calculate start and end index for current page
        int startIndex = (currentPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalUsers);
        
        // Get users for current page
        List<User> paginatedUsers = new ArrayList<>();
        if (startIndex < totalUsers) {
            paginatedUsers = filteredUsers.subList(startIndex, endIndex);
        }
        
        // Set attributes for JSP
        request.setAttribute("users", paginatedUsers);
        request.setAttribute("roleFilter", roleFilter != null ? roleFilter : "All Role");
        request.setAttribute("statusFilter", statusFilter != null ? statusFilter : "All Status");
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("pageSize", PAGE_SIZE);
        request.setAttribute("startIndex", startIndex); // Để tính STT từ đầu danh sách
        
        // Forward to JSP
        request.getRequestDispatcher("/view/dashboard/admin/user-management.jsp").forward(request, response);
    }

    private void updateAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
    }

    private void deactivateAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    }

    /**
     * Kiểm tra user có phải admin không
     * @return true nếu là admin, false nếu không (đã redirect về login)
     */
    private boolean isAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        
        // Kiểm tra đã đăng nhập chưa
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        
        // Kiểm tra userId
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        
        // Kiểm tra role từ session (nhanh hơn)
        String roleName = (String) session.getAttribute("roleName");
        if (roleName == null || !"admin".equalsIgnoreCase(roleName)) {
            // Nếu không có trong session, kiểm tra từ database
            User user = userDAO.findById(userId);
            if (user == null || !"admin".equalsIgnoreCase(user.getRoleName())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin only.");
                return false;
            }
        }
        
        return true;
    }

}
