package com.example.argomachinemanagement.controller.dashboard;

import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.dal.MachineAssetDAO;
import com.example.argomachinemanagement.dal.ContractDAO;
import com.example.argomachinemanagement.dal.OrderDAO;
import com.example.argomachinemanagement.entity.User;
import com.example.argomachinemanagement.entity.MachineAsset;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Controller cho Admin Statistics với dữ liệu cho charts
 */
@WebServlet(name = "AdminStatisticsController", urlPatterns = {"/admin/statistics"})
public class AdminStatisticsController extends HttpServlet {
    
    private UserDAO userDAO;
    private MachineAssetDAO machineAssetDAO;
    private ContractDAO contractDAO;
    private OrderDAO orderDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        machineAssetDAO = new MachineAssetDAO();
        contractDAO = new ContractDAO();
        orderDAO = new OrderDAO();
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
            
            for (User u : allUsers) {
                if (u.getStatus() != null && u.getStatus() == 1) {
                    activeUsers++;
                } else {
                    inactiveUsers++;
                }
            }
            
            // ========== MACHINE ASSETS STATISTICS ==========
            List<MachineAsset> allAssets = machineAssetDAO.findAll();
            int totalAssets = allAssets.size();
            int availableAssets = 0;
            int rentedAssets = 0;
            int maintenanceAssets = 0;
            
            for (MachineAsset asset : allAssets) {
                if ("AVAILABLE".equals(asset.getRentalStatus())) {
                    availableAssets++;
                } else if ("RENTED".equals(asset.getRentalStatus())) {
                    rentedAssets++;
                } else if ("MAINTENANCE".equals(asset.getRentalStatus())) {
                    maintenanceAssets++;
                }
            }
            
            // ========== CONTRACT STATISTICS ==========
            int totalContracts = contractDAO.findAll().size();
            int activeContracts = contractDAO.countByStatus("ACTIVE");
            int finishedContracts = contractDAO.countByStatus("FINISHED");
            
            // ========== ORDER STATISTICS ==========
            int totalOrders = orderDAO.findAll().size();
            int pendingOrders = orderDAO.countByStatus("PENDING");
            // Đơn đã duyệt = CONVERTED (vì khi duyệt sẽ tạo contract và set status = CONVERTED)
            int approvedOrders = orderDAO.countByStatus("CONVERTED");
            
            // Set attributes - User Statistics
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("activeUsers", activeUsers);
            request.setAttribute("inactiveUsers", inactiveUsers);
            
            // Machine Assets Statistics
            request.setAttribute("totalAssets", totalAssets);
            request.setAttribute("availableAssets", availableAssets);
            request.setAttribute("rentedAssets", rentedAssets);
            request.setAttribute("maintenanceAssets", maintenanceAssets);
            
            // Contract Statistics
            request.setAttribute("totalContracts", totalContracts);
            request.setAttribute("activeContracts", activeContracts);
            request.setAttribute("finishedContracts", finishedContracts);
            
            // Order Statistics
            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("pendingOrders", pendingOrders);
            request.setAttribute("approvedOrders", approvedOrders);
            
            // Forward to JSP
            request.getRequestDispatcher("/view/dashboard/admin/statistics.jsp").forward(request, response);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading statistics");
        }
    }
}


