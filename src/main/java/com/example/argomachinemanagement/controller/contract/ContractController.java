package com.example.argomachinemanagement.controller.contract;

import com.example.argomachinemanagement.dal.ContractDAO;
import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.entity.Contract;
import com.example.argomachinemanagement.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ContractController", urlPatterns = {"/contracts"})
public class ContractController extends HttpServlet {
    
    private static final int PAGE_SIZE = 5;
    
    private ContractDAO contractDAO;
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        contractDAO = new ContractDAO();
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null || action.equals("list")) {
            handleListWithFilters(request, response);
        }
    }
    
    /**
     * C1: Contract List với Search + Filter
     */
    private void handleListWithFilters(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get filter parameters
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
        
        // Get user ID from session (for filtering by customer/manager)
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        User currentUser = userId != null ? userDAO.findById(userId) : null;
        
        // Determine filter by role
        Integer customerId = null;
        Integer managerId = null;
        if (currentUser != null && currentUser.getRoleName() != null) {
            String roleName = currentUser.getRoleName().toLowerCase();
            if ("customer".equals(roleName)) {
                customerId = userId; // Customer chỉ xem contracts của mình
            } else if ("manager".equals(roleName) || "sale".equals(roleName)) {
                managerId = userId; // Manager/Sale chỉ xem contracts mình quản lý
            }
            // Admin xem tất cả (không set filter)
        }
        
        // Get filtered contracts
        List<Contract> allContracts = contractDAO.findByFilters(
            statusFilter, keyword, customerId, managerId
        );
        
        // Pagination logic
        int totalContracts = allContracts.size();
        int totalPages = (int) Math.ceil((double) totalContracts / PAGE_SIZE);
        
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        
        int startIndex = (currentPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalContracts);
        
        List<Contract> paginatedContracts = new ArrayList<>();
        if (startIndex < totalContracts) {
            paginatedContracts = allContracts.subList(startIndex, endIndex);
        }
        
        // Set attributes for JSP
        request.setAttribute("contracts", paginatedContracts);
        request.setAttribute("statusFilter", statusFilter != null ? statusFilter : "All Status");
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalContracts", totalContracts);
        request.setAttribute("pageSize", PAGE_SIZE);
        request.setAttribute("startIndex", startIndex);
        
        // Forward to JSP
        request.getRequestDispatcher("/view/dashboard/contract/contract-list.jsp").forward(request, response);
    }
}

