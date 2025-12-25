package com.example.argomachinemanagement.controller.contract;

import com.example.argomachinemanagement.dal.ContractDAO;
import com.example.argomachinemanagement.dal.ContractItemDAO;
import com.example.argomachinemanagement.entity.Contract;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ContractController", urlPatterns = {"/contracts", "/customer/my-contracts", "/sale/contracts"})
public class ContractController extends HttpServlet {
    
    private static final int PAGE_SIZE = 5;
    
    private ContractDAO contractDAO;
    private ContractItemDAO contractItemDAO;
    
    @Override
    public void init() throws ServletException {
        contractDAO = new ContractDAO();
        contractItemDAO = new ContractItemDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if (action == null || action.equals("list")) {
            handleListWithFilters(request, response);
        } else if ("detail".equals(action)) {
            handleDetail(request, response);
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
        
        // Get user ID and role from session (for filtering by customer/manager)
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        String roleName = (String) session.getAttribute("roleName");
        
        // Double check authentication (defense in depth)
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Determine filter by role
        Integer customerId = null;
        Integer managerId = null;
        Integer saleId = null;
        if (userId != null && roleName != null) {
            String role = roleName.toLowerCase();
            if ("customer".equals(role)) {
                customerId = userId; // Customer chỉ xem contracts của mình
            } else if ("manager".equals(role)) {
                managerId = userId; // Manager chỉ xem contracts mình quản lý
            } else if ("sale".equals(role)) {
                saleId = userId; // Sale chỉ xem contracts mình đã tạo
            }
            // Admin xem tất cả (không set filter)
        }
        
        // Get filtered contracts
        List<Contract> allContracts = contractDAO.findByFilters(
            statusFilter, keyword, customerId, managerId, saleId
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
    
    /**
     * Xử lý xem chi tiết hợp đồng
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        
        // Kiểm tra quyền truy cập trước
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String roleName = (String) request.getSession().getAttribute("roleName");
        
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Xác định URL redirect phù hợp với role
        String redirectBase = "customer".equalsIgnoreCase(roleName) 
            ? "/customer/my-contracts" 
            : "/contracts";
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + redirectBase);
            return;
        }
        
        try {
            int contractId = Integer.parseInt(idStr);
            System.out.println("[ContractController] Loading contract detail: id=" + contractId + ", userId=" + userId + ", role=" + roleName);
            
            Contract contract = contractDAO.findById(contractId);
            
            if (contract == null) {
                System.out.println("[ContractController] Contract not found: id=" + contractId);
                response.sendRedirect(request.getContextPath() + redirectBase + "?error=notfound");
                return;
            }
            
            System.out.println("[ContractController] Found contract: id=" + contractId + ", code=" + contract.getContractCode() + ", customerId=" + contract.getCustomerId());
            
            // Customer chỉ xem được contracts của mình
            if ("customer".equalsIgnoreCase(roleName)) {
                if (contract.getCustomerId() == null || !contract.getCustomerId().equals(userId)) {
                    System.out.println("[ContractController] Unauthorized: customerId=" + contract.getCustomerId() + ", userId=" + userId);
                    response.sendRedirect(request.getContextPath() + "/customer/my-contracts?error=unauthorized");
                    return;
                }
            }
            
            // Load contract items
            try {
                if (contract.getItems() == null) {
                    List<com.example.argomachinemanagement.entity.ContractItem> items = contractItemDAO.findByContractId(contractId);
                    contract.setItems(items);
                    System.out.println("[ContractController] Loaded " + (items != null ? items.size() : 0) + " contract items");
                }
            } catch (Exception e) {
                System.out.println("[ContractController] Error loading contract items: " + e.getMessage());
                e.printStackTrace();
                // Set empty list if error
                contract.setItems(new ArrayList<>());
            }
            
            System.out.println("[ContractController] Setting contract to request: customerName=" + contract.getCustomerName() + ", customerPhone=" + contract.getCustomerPhone());
            request.setAttribute("contract", contract);
            
            // Forward đến view phù hợp
            String viewPath;
            if ("customer".equalsIgnoreCase(roleName)) {
                viewPath = "/view/dashboard/contract/contract-detail-customer.jsp";
            } else {
                viewPath = "/view/dashboard/contract/contract-detail.jsp";
            }
            
            System.out.println("[ContractController] Forwarding to: " + viewPath);
            request.getRequestDispatcher(viewPath).forward(request, response);
            
        } catch (NumberFormatException e) {
            System.out.println("[ContractController] Invalid contract ID: " + idStr);
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + redirectBase + "?error=invalid");
        } catch (Exception e) {
            System.out.println("[ContractController] Unexpected error in handleDetail: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading contract details: " + e.getMessage());
        }
    }
}

