package com.example.argomachinemanagement.controller.dashboard;

import com.example.argomachinemanagement.dal.PasswordResetRequestDAO;
import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller cho admin dashboard
 * URL: /admin/dashboard
 */
@WebServlet(name = "AdminDashboardController", urlPatterns = {"/admin/t"})
public class AdminDashboardController extends HttpServlet {
    
    private UserDAO userDAO;
    private PasswordResetRequestDAO passwordResetRequestDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        passwordResetRequestDAO = new PasswordResetRequestDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check admin authentication
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin only.");
            return;
        }
        
        try {
            // Lấy thống kê
            int totalUsers = userDAO.findAll().size();
            int totalPendingRequests = passwordResetRequestDAO.countRequests("", "pending");
            int totalApprovedRequests = passwordResetRequestDAO.countRequests("", "approved");
            
            // Set attributes
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("totalPendingRequests", totalPendingRequests);
            request.setAttribute("totalApprovedRequests", totalApprovedRequests);
            
            // Forward to JSP
            request.getRequestDispatcher("/view/dashboard/admin/dashboard.jsp").forward(request, response);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading dashboard");
        }
    }
    
    /**
     * Kiểm tra user có phải admin không
     */
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return false;
        }
        
        // Lấy user từ database để kiểm tra role
        User user = userDAO.findById(userId);
        if (user == null) {
            return false;
        }
        
        // Check if user has admin role
        return "admin".equalsIgnoreCase(user.getRoleName());
    }
}

