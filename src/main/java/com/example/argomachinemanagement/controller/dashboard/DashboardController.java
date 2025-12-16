package com.example.argomachinemanagement.controller.dashboard;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller xử lý dashboard chung cho các role (trừ admin - admin có controller riêng)
 * URL patterns: /manager/dashboard, /sale/dashboard, /customer/dashboard
 */
@WebServlet(name = "DashboardController", urlPatterns = {
    "/admin/dashboard",
    "/manager/dashboard", 
    "/sale/dashboard", 
    "/customer/dashboard"
})
public class DashboardController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Lấy thông tin user và role từ session
        String roleName = (String) session.getAttribute("roleName");
        String fullName = (String) session.getAttribute("fullName");
        Integer userId = (Integer) session.getAttribute("userId");
        
        // Set các attributes cho JSP
        request.setAttribute("roleName", roleName);
        request.setAttribute("fullName", fullName);
        request.setAttribute("userId", userId);
        
        // Set title dựa trên role
        String pageTitle = getPageTitle(roleName);
        request.setAttribute("pageTitle", pageTitle);
        
        // Set welcome message
        request.setAttribute("welcomeMessage", "Chào mừng " + fullName + " đến với hệ thống quản lý!");
        
        // Forward đến dashboard.jsp chung
        request.getRequestDispatcher("/view/dashboard/dashboard.jsp").forward(request, response);
    }
    
    /**
     * Lấy tiêu đề trang dựa trên role
     */
    private String getPageTitle(String roleName) {
        if (roleName == null) {
            return "Dashboard";
        }
        
        switch (roleName.toLowerCase()) {
            case "admin":
                return "Admin Dashboard";
            case "manager":
                return "Manager Dashboard";
            case "sale":
                return "Sale Dashboard";
            case "customer":
                return "Customer Dashboard";
            default:
                return "Dashboard";
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
