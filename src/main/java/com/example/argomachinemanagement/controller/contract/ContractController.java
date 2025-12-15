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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "ContractController", urlPatterns = {"/contracts", "/manager/contracts", "/sale/contracts"})
public class ContractController extends HttpServlet {
    
    private static final int PAGE_SIZE = 3;
    
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
        } else if ("create".equals(action)) {
            showCreateForm(request, response);
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
        
        // Get user ID from session (for filtering by customer/manager)
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        User currentUser = userId != null ? userDAO.findById(userId) : null;
        
        // Determine filter by role
        Integer customerId = null;
        Integer managerId = null;
        if (currentUser != null && currentUser.getRoleName() != null) {
            String roleName = currentUser.getRoleName().toLowerCase();
            if ("customer".equals(roleName)) {
                // Customer chỉ xem contracts của mình
                customerId = userId;
            } else if ("sale".equals(roleName)) {
                // Sale chỉ xem các contract mình phụ trách
                managerId = userId;
            }
            // Manager & Admin xem tất cả contracts (không set filter)
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

        // Chọn view theo role: Customer dùng trang My Contracts riêng
        String viewPath = "/view/dashboard/contract/contract-list.jsp";
        if (currentUser != null && currentUser.getRoleName() != null
                && "customer".equalsIgnoreCase(currentUser.getRoleName())) {
            viewPath = "/view/dashboard/contract/customer-contract-list.jsp";
        }

        // Forward to JSP
        request.getRequestDispatcher(viewPath).forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null || "create".equals(action)) {
            handleCreate(request, response);
        }
    }
    
    /**
     * C2: Show Create Contract Form
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get all customers (users with role 'customer')
        List<User> allUsers = userDAO.findAll();
        List<User> customers = allUsers.stream()
                .filter(u -> u.getRoleName() != null && "customer".equalsIgnoreCase(u.getRoleName()) && 
                            u.getStatus() != null && u.getStatus() == 1)
                .collect(Collectors.toList());
        
        // Get all managers (users with role 'manager' or 'sale')
        List<User> managers = allUsers.stream()
                .filter(u -> u.getRoleName() != null && 
                            ("manager".equalsIgnoreCase(u.getRoleName()) || "sale".equalsIgnoreCase(u.getRoleName())) &&
                            u.getStatus() != null && u.getStatus() == 1)
                .collect(Collectors.toList());
        
        // Generate contract code
        String contractCode = contractDAO.generateContractCode();
        
        request.setAttribute("customers", customers);
        request.setAttribute("managers", managers);
        request.setAttribute("contractCode", contractCode);
        
        request.getRequestDispatcher("/view/dashboard/contract/contract-create.jsp").forward(request, response);
    }
    
    /**
     * C2: Handle Create Contract
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get parameters
        String contractCode = request.getParameter("contractCode");
        String customerIdStr = request.getParameter("customerId");
        String managerIdStr = request.getParameter("managerId");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String note = request.getParameter("note");
        
        // Validation
        List<String> errors = new ArrayList<>();
        
        if (contractCode == null || contractCode.trim().isEmpty()) {
            errors.add("Contract code không được để trống!");
        }
        
        if (customerIdStr == null || customerIdStr.trim().isEmpty()) {
            errors.add("Vui lòng chọn khách hàng!");
        }
        
        if (managerIdStr == null || managerIdStr.trim().isEmpty()) {
            errors.add("Vui lòng chọn manager!");
        }
        
        // Validate customer and manager are different
        if (customerIdStr != null && !customerIdStr.trim().isEmpty() && 
            managerIdStr != null && !managerIdStr.trim().isEmpty()) {
            try {
                int customerId = Integer.parseInt(customerIdStr);
                int managerId = Integer.parseInt(managerIdStr);
                if (customerId == managerId) {
                    errors.add("Customer và Manager không được trùng nhau! Vui lòng chọn người khác.");
                }
            } catch (NumberFormatException e) {
                // Will be caught by other validations
            }
        }
        
        if (startDateStr == null || startDateStr.trim().isEmpty()) {
            errors.add("Ngày bắt đầu không được để trống!");
        }
        
        if (endDateStr == null || endDateStr.trim().isEmpty()) {
            errors.add("Ngày kết thúc không được để trống!");
        }
        
        Date startDate = null;
        Date endDate = null;
        
        try {
            if (startDateStr != null && !startDateStr.trim().isEmpty()) {
                startDate = Date.valueOf(startDateStr);
            }
            if (endDateStr != null && !endDateStr.trim().isEmpty()) {
                endDate = Date.valueOf(endDateStr);
            }
            
            // Validate date range
            if (startDate != null && endDate != null && startDate.after(endDate)) {
                errors.add("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc!");
            }
        } catch (IllegalArgumentException e) {
            errors.add("Định dạng ngày không hợp lệ!");
        }
        
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            // Re-show form with entered data
            showCreateFormWithData(request, response, contractCode, customerIdStr, managerIdStr, 
                                 startDateStr, endDateStr, note, errors);
            return;
        }
        
        // Create contract
        Contract contract = Contract.builder()
                .contractCode(contractCode.trim())
                .customerId(Integer.parseInt(customerIdStr))
                .managerId(Integer.parseInt(managerIdStr))
                .startDate(startDate)
                .endDate(endDate)
                .status("DRAFT")
                .note(note != null && !note.trim().isEmpty() ? note.trim() : null)
                .build();
        
        int contractId = contractDAO.insert(contract);
        
        if (contractId > 0) {
            // TODO: Redirect sau khi create thành công
            // Để redirect về trang contracts list, thay dòng dưới thành:
            // response.sendRedirect(request.getContextPath() + "/contracts?success=Contract created successfully");
            response.sendRedirect(request.getContextPath() + "/contracts?action=detail&id=" + contractId);
        } else {
            request.setAttribute("error", "Failed to create contract. Please try again!");
            showCreateFormWithData(request, response, contractCode, customerIdStr, managerIdStr, 
                                 startDateStr, endDateStr, note, errors);
        }
    }
    
    /**
     * Helper method to re-show form with entered data and errors
     */
    private void showCreateFormWithData(HttpServletRequest request, HttpServletResponse response,
                                       String contractCode, String customerId, String managerId,
                                       String startDate, String endDate, String note, List<String> errors)
            throws ServletException, IOException {
        // Get all customers and managers
        List<User> allUsers = userDAO.findAll();
        List<User> customers = allUsers.stream()
                .filter(u -> u.getRoleName() != null && "customer".equalsIgnoreCase(u.getRoleName()) && 
                            u.getStatus() != null && u.getStatus() == 1)
                .collect(Collectors.toList());
        
        List<User> managers = allUsers.stream()
                .filter(u -> u.getRoleName() != null && 
                            ("manager".equalsIgnoreCase(u.getRoleName()) || "sale".equalsIgnoreCase(u.getRoleName())) &&
                            u.getStatus() != null && u.getStatus() == 1)
                .collect(Collectors.toList());
        
        request.setAttribute("customers", customers);
        request.setAttribute("managers", managers);
        request.setAttribute("contractCode", contractCode != null ? contractCode : contractDAO.generateContractCode());
        request.setAttribute("selectedCustomerId", customerId);
        request.setAttribute("selectedManagerId", managerId);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("note", note);
        request.setAttribute("errors", errors);
        
        request.getRequestDispatcher("/view/dashboard/contract/contract-create.jsp").forward(request, response);
    }
    
    /**
     * C3: Show Contract Detail
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/contracts?error=Contract ID is required");
            return;
        }
        
        try {
            Integer contractId = Integer.parseInt(idStr);
            Contract contract = contractDAO.findById(contractId);
            
            if (contract == null) {
                response.sendRedirect(request.getContextPath() + "/contracts?error=Contract not found");
                return;
            }
            
            request.setAttribute("contract", contract);
            request.getRequestDispatcher("/view/dashboard/contract/contract-detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/contracts?error=Invalid contract ID");
        }
    }
}

