package com.example.argomachinemanagement.controller.dashboard;

import com.example.argomachinemanagement.dal.PasswordResetRequestDAO;
import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.dal.MachineDAO;
import com.example.argomachinemanagement.dal.ContractDAO;
import com.example.argomachinemanagement.dal.MaintenanceDAO;
import com.example.argomachinemanagement.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller cho Admin Statistics với dữ liệu cho charts
 */
@WebServlet(name = "AdminStatisticsController", urlPatterns = {"/admin/statistics"})
public class AdminStatisticsController extends HttpServlet {
    
    private UserDAO userDAO;
    private PasswordResetRequestDAO passwordResetRequestDAO;
    private MachineDAO machineDAO;
    private ContractDAO contractDAO;
    private MaintenanceDAO maintenanceDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        passwordResetRequestDAO = new PasswordResetRequestDAO();
        machineDAO = new MachineDAO();
        contractDAO = new ContractDAO();
        maintenanceDAO = new MaintenanceDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Check admin
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = userDAO.findById(userId);
        if (user == null || !"admin".equalsIgnoreCase(user.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin only.");
            return;
        }
        
        try {
            // ========== USER STATISTICS ==========
            List<User> allUsers = userDAO.findAll();
            int totalUsers = allUsers.size();
            int activeUsers = 0;
            int inactiveUsers = 0;
            
            // Users by role
            Map<String, Integer> usersByRole = new HashMap<>();
            
            for (User u : allUsers) {
                if (u.getStatus() != null && u.getStatus() == 1) {
                    activeUsers++;
                } else {
                    inactiveUsers++;
                }
                
                // Count by role
                String role = u.getRoleName() != null ? u.getRoleName() : "Khác";
                usersByRole.put(role, usersByRole.getOrDefault(role, 0) + 1);
            }
            
            // ========== PASSWORD RESET STATISTICS ==========
            int totalPendingRequests = passwordResetRequestDAO.countRequests("", "pending");
            int totalApprovedRequests = passwordResetRequestDAO.countRequests("", "approved");
            int totalRejectedRequests = passwordResetRequestDAO.countRequests("", "rejected");
            int totalResetRequests = passwordResetRequestDAO.countRequests("", "");
            
            // ========== SYSTEM STATISTICS ==========
            int totalMachines = machineDAO.findAll().size();
            int totalContracts = contractDAO.findAll().size();
            int totalMaintenances = maintenanceDAO.countAll();
            
            // Contracts by month (last 6 months)
            Calendar cal = Calendar.getInstance();
            Map<String, Integer> contractsByMonth = new HashMap<>();
            for (int i = 5; i >= 0; i--) {
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -i);
                int month = cal.get(Calendar.MONTH) + 1;
                int year = cal.get(Calendar.YEAR);
                String monthKey = String.format("%02d/%d", month, year);
                contractsByMonth.put(monthKey, 0);
            }
            
            List<com.example.argomachinemanagement.entity.Contract> contracts = contractDAO.findAll();
            for (com.example.argomachinemanagement.entity.Contract c : contracts) {
                if (c.getCreatedAt() != null) {
                    cal.setTime(c.getCreatedAt());
                    int month = cal.get(Calendar.MONTH) + 1;
                    int year = cal.get(Calendar.YEAR);
                    String monthKey = String.format("%02d/%d", month, year);
                    if (contractsByMonth.containsKey(monthKey)) {
                        contractsByMonth.put(monthKey, contractsByMonth.get(monthKey) + 1);
                    }
                }
            }
            
            // Set attributes
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("activeUsers", activeUsers);
            request.setAttribute("inactiveUsers", inactiveUsers);
            
            request.setAttribute("totalPendingRequests", totalPendingRequests);
            request.setAttribute("totalApprovedRequests", totalApprovedRequests);
            request.setAttribute("totalRejectedRequests", totalRejectedRequests);
            request.setAttribute("totalResetRequests", totalResetRequests);
            
            request.setAttribute("totalMachines", totalMachines);
            request.setAttribute("totalContracts", totalContracts);
            request.setAttribute("totalMaintenances", totalMaintenances);
            
            // Chart data
            request.setAttribute("usersByRole", usersByRole);
            request.setAttribute("contractsByMonth", contractsByMonth);
            
            // Forward to JSP
            request.getRequestDispatcher("/view/dashboard/admin/statistics.jsp").forward(request, response);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading statistics");
        }
    }
}

