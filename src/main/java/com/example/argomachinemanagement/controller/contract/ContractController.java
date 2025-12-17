package com.example.argomachinemanagement.controller.contract;

import com.example.argomachinemanagement.dal.ContractDAO;
import com.example.argomachinemanagement.dal.ContractItemDAO;
import com.example.argomachinemanagement.dal.MachineDAO;
import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.entity.Contract;
import com.example.argomachinemanagement.entity.ContractItem;
import com.example.argomachinemanagement.entity.Machine;
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

@WebServlet(
        name = "ContractController",
        urlPatterns = {"/contracts", "/manager/contracts", "/sale/contracts", "/customer/contracts"}
)
public class ContractController extends HttpServlet {
    
    private static final int PAGE_SIZE = 5;
    
    private ContractDAO contractDAO;
    private UserDAO userDAO;
    private ContractItemDAO contractItemDAO;
    private MachineDAO machineDAO;
    
    @Override
    public void init() throws ServletException {
        contractDAO = new ContractDAO();
        userDAO = new UserDAO();
        contractItemDAO = new ContractItemDAO();
        machineDAO = new MachineDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null || action.equals("list")) {
            handleListWithFilters(request, response);
        } else if ("detail".equals(action)) {
            handleDetail(request, response);
        } else if ("create".equals(action)) {
            showCreateForm(request, response);
        } else if ("add-machine".equals(action)) {
            showAddMachineForm(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            handleCreateContract(request, response);
        } else if ("add-machine".equals(action)) {
            handleAddMachine(request, response);
        } else if ("remove-machine".equals(action)) {
            handleRemoveMachine(request, response);
        } else {
            // Nếu không phải action nào, redirect về list
            response.sendRedirect(request.getContextPath() + "/contracts");
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

    /**
     * Hiển thị form tạo mới contract cho manager/sale/admin
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        User currentUser = userId != null ? userDAO.findById(userId) : null;
        String roleName = currentUser != null ? currentUser.getRoleName() : null;

        // Chỉ cho phép manager, sale, admin tạo contract
        if (roleName == null ||
                !(roleName.equalsIgnoreCase("manager")
                        || roleName.equalsIgnoreCase("sale")
                        || roleName.equalsIgnoreCase("admin"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền tạo hợp đồng");
            return;
        }

        // Sinh mã contract mới
        String nextCode = contractDAO.getNextContractCode();

        // Lấy danh sách customer active và manager/sale active
        List<User> customers = userDAO.findActiveUsersByRole("customer");
        List<User> managers = new ArrayList<>();
        managers.addAll(userDAO.findActiveUsersByRole("manager"));
        managers.addAll(userDAO.findActiveUsersByRole("sale"));

        request.setAttribute("contractCode", nextCode);
        request.setAttribute("customers", customers);
        request.setAttribute("managers", managers);

        request.getRequestDispatcher("/view/dashboard/contract/contract-create.jsp").forward(request, response);
    }
    
    /**
     * Xử lý tạo hợp đồng mới (POST request từ form)
     */
    private void handleCreateContract(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        User currentUser = userId != null ? userDAO.findById(userId) : null;
        String roleName = currentUser != null ? currentUser.getRoleName() : null;
        
        // Kiểm tra quyền
        if (roleName == null ||
                !(roleName.equalsIgnoreCase("manager")
                        || roleName.equalsIgnoreCase("sale")
                        || roleName.equalsIgnoreCase("admin"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền tạo hợp đồng");
            return;
        }
        
        // Lấy dữ liệu từ form
        String contractCode = request.getParameter("contractCode");
        String customerIdStr = request.getParameter("customerId");
        String managerIdStr = request.getParameter("managerId");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String note = request.getParameter("note");
        
        // Validate dữ liệu
        List<String> errors = new ArrayList<>();
        
        if (contractCode == null || contractCode.trim().isEmpty()) {
            errors.add("Contract code is required");
        }
        
        if (customerIdStr == null || customerIdStr.trim().isEmpty()) {
            errors.add("Customer is required");
        }
        
        if (managerIdStr == null || managerIdStr.trim().isEmpty()) {
            errors.add("Manager is required");
        }
        
        if (startDateStr == null || startDateStr.trim().isEmpty()) {
            errors.add("Start date is required");
        }
        
        if (endDateStr == null || endDateStr.trim().isEmpty()) {
            errors.add("End date is required");
        }
        
        // Kiểm tra customer và manager không được giống nhau
        if (customerIdStr != null && managerIdStr != null && 
            customerIdStr.equals(managerIdStr)) {
            errors.add("Customer and Manager cannot be the same person");
        }
        
        // Nếu có lỗi, hiển thị lại form với thông báo lỗi
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("contractCode", contractCode);
            request.setAttribute("selectedCustomerId", customerIdStr);
            request.setAttribute("selectedManagerId", managerIdStr);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            request.setAttribute("note", note);
            
            // Load lại danh sách customers và managers
            List<User> customers = userDAO.findActiveUsersByRole("customer");
            List<User> managers = new ArrayList<>();
            managers.addAll(userDAO.findActiveUsersByRole("manager"));
            managers.addAll(userDAO.findActiveUsersByRole("sale"));
            request.setAttribute("customers", customers);
            request.setAttribute("managers", managers);
            
            request.getRequestDispatcher("/view/dashboard/contract/contract-create.jsp")
                    .forward(request, response);
            return;
        }
        
        // Parse và validate dates
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = Date.valueOf(startDateStr);
            endDate = Date.valueOf(endDateStr);
            
            // Kiểm tra endDate >= startDate
            if (endDate.before(startDate)) {
                errors.add("End date must be after or equal to start date");
                request.setAttribute("errors", errors);
                request.setAttribute("contractCode", contractCode);
                request.setAttribute("selectedCustomerId", customerIdStr);
                request.setAttribute("selectedManagerId", managerIdStr);
                request.setAttribute("startDate", startDateStr);
                request.setAttribute("endDate", endDateStr);
                request.setAttribute("note", note);
                
                List<User> customers = userDAO.findActiveUsersByRole("customer");
                List<User> managers = new ArrayList<>();
                managers.addAll(userDAO.findActiveUsersByRole("manager"));
                managers.addAll(userDAO.findActiveUsersByRole("sale"));
                request.setAttribute("customers", customers);
                request.setAttribute("managers", managers);
                
                request.getRequestDispatcher("/view/dashboard/contract/contract-create.jsp")
                        .forward(request, response);
                return;
            }
        } catch (IllegalArgumentException e) {
            errors.add("Invalid date format");
            request.setAttribute("errors", errors);
            request.setAttribute("contractCode", contractCode);
            request.setAttribute("selectedCustomerId", customerIdStr);
            request.setAttribute("selectedManagerId", managerIdStr);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            request.setAttribute("note", note);
            
            List<User> customers = userDAO.findActiveUsersByRole("customer");
            List<User> managers = new ArrayList<>();
            managers.addAll(userDAO.findActiveUsersByRole("manager"));
            managers.addAll(userDAO.findActiveUsersByRole("sale"));
            request.setAttribute("customers", customers);
            request.setAttribute("managers", managers);
            
            request.getRequestDispatcher("/view/dashboard/contract/contract-create.jsp")
                    .forward(request, response);
            return;
        }
        
        // Tạo Contract object
        Contract contract = Contract.builder()
                .contractCode(contractCode.trim())
                .customerId(Integer.parseInt(customerIdStr))
                .managerId(Integer.parseInt(managerIdStr))
                .startDate(startDate)
                .endDate(endDate)
                .status("DRAFT")
                .note(note != null ? note.trim() : null)
                .build();
        
        // Lưu vào database
        int contractId = contractDAO.insert(contract);
        
        if (contractId > 0) {
            // Thành công - redirect về danh sách với thông báo
            String redirectPath = "/contracts";
            if ("manager".equalsIgnoreCase(roleName)) {
                redirectPath = "/manager/contracts";
            } else if ("sale".equalsIgnoreCase(roleName)) {
                redirectPath = "/sale/contracts";
            }
            response.sendRedirect(request.getContextPath() + redirectPath + "?success=Contract created successfully");
        } else {
            // Lỗi khi lưu
            errors.add("Failed to create contract. Please try again.");
            request.setAttribute("errors", errors);
            request.setAttribute("contractCode", contractCode);
            request.setAttribute("selectedCustomerId", customerIdStr);
            request.setAttribute("selectedManagerId", managerIdStr);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            request.setAttribute("note", note);
            
            List<User> customers = userDAO.findActiveUsersByRole("customer");
            List<User> managers = new ArrayList<>();
            managers.addAll(userDAO.findActiveUsersByRole("manager"));
            managers.addAll(userDAO.findActiveUsersByRole("sale"));
            request.setAttribute("customers", customers);
            request.setAttribute("managers", managers);
            
            request.getRequestDispatcher("/view/dashboard/contract/contract-create.jsp")
                    .forward(request, response);
        }
    }

    /**
     * Hiển thị chi tiết hợp đồng, đảm bảo customer chỉ xem hợp đồng của mình
     * và manager/sale chỉ xem hợp đồng mình quản lý.
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        User currentUser = userId != null ? userDAO.findById(userId) : null;

        Contract contract = contractDAO.findById(id);
        if (contract == null) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }

        // Check quyền xem theo role
        if (currentUser != null && currentUser.getRoleName() != null) {
            String roleName = currentUser.getRoleName().toLowerCase();
            if ("customer".equals(roleName) && !userId.equals(contract.getCustomerId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền xem hợp đồng này");
                return;
            }
            if (("manager".equals(roleName) || "sale".equals(roleName))
                    && !userId.equals(contract.getManagerId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền xem hợp đồng này");
                return;
            }
        }

        // Load danh sách machines trong contract
        List<ContractItem> contractItems = contractItemDAO.findByContractId(id);
        request.setAttribute("contract", contract);
        request.setAttribute("contractItems", contractItems);

        // Customer sẽ sử dụng view "my contract" riêng, các role khác dùng view chung
        if (currentUser != null && "customer".equalsIgnoreCase(currentUser.getRoleName())) {
            request.getRequestDispatcher("/view/dashboard/contract/contract-detail-customer.jsp")
                    .forward(request, response);
        } else {
            request.getRequestDispatcher("/view/dashboard/contract/contract-detail.jsp")
                    .forward(request, response);
        }
    }
    
    /**
     * Hiển thị form thêm machine vào contract
     */
    private void showAddMachineForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contractIdStr = request.getParameter("contractId");
        if (contractIdStr == null || contractIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }
        
        int contractId;
        try {
            contractId = Integer.parseInt(contractIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }
        
        // Kiểm tra contract tồn tại
        Contract contract = contractDAO.findById(contractId);
        if (contract == null) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }
        
        // Lấy danh sách machines có thể thêm (chưa được thêm vào contract này)
        List<Machine> allMachines = machineDAO.findAll();
        List<Machine> availableMachines = new ArrayList<>();
        for (Machine machine : allMachines) {
            if (!contractItemDAO.exists(contractId, machine.getId())) {
                availableMachines.add(machine);
            }
        }
        
        request.setAttribute("contract", contract);
        request.setAttribute("machines", availableMachines);
        request.getRequestDispatcher("/view/dashboard/contract/contract-add-machine.jsp")
                .forward(request, response);
    }
    
    /**
     * Xử lý thêm machine vào contract (POST)
     */
    private void handleAddMachine(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contractIdStr = request.getParameter("contractId");
        String machineIdStr = request.getParameter("machineId");
        String note = request.getParameter("note");
        
        if (contractIdStr == null || machineIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }
        
        int contractId;
        int machineId;
        try {
            contractId = Integer.parseInt(contractIdStr);
            machineId = Integer.parseInt(machineIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }
        
        // Kiểm tra contract tồn tại
        Contract contract = contractDAO.findById(contractId);
        if (contract == null) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }
        
        // Kiểm tra machine đã được thêm chưa
        if (contractItemDAO.exists(contractId, machineId)) {
            response.sendRedirect(request.getContextPath() + "/contracts?action=detail&id=" + contractId + "&error=Machine already added");
            return;
        }
        
        // Lấy thông tin machine để lưu snapshot
        Machine machine = machineDAO.findById(machineId);
        if (machine == null) {
            response.sendRedirect(request.getContextPath() + "/contracts?action=detail&id=" + contractId + "&error=Machine not found");
            return;
        }
        
        // Tạo ContractItem
        ContractItem item = ContractItem.builder()
                .contractId(contractId)
                .machineId(machineId)
                .machineNameSnapshot(machine.getMachineName())
                .note(note != null ? note.trim() : null)
                .build();
        
        int itemId = contractItemDAO.insert(item);
        if (itemId > 0) {
            response.sendRedirect(request.getContextPath() + "/contracts?action=detail&id=" + contractId + "&success=Machine added successfully");
        } else {
            response.sendRedirect(request.getContextPath() + "/contracts?action=detail&id=" + contractId + "&error=Failed to add machine");
        }
    }
    
    /**
     * Xử lý xóa machine khỏi contract
     */
    private void handleRemoveMachine(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String itemIdStr = request.getParameter("itemId");
        String contractIdStr = request.getParameter("contractId");
        
        if (itemIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }
        
        int itemId;
        try {
            itemId = Integer.parseInt(itemIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }
        
        boolean success = contractItemDAO.delete(itemId);
        int contractId = contractIdStr != null ? Integer.parseInt(contractIdStr) : 0;
        
        if (success) {
            response.sendRedirect(request.getContextPath() + "/contracts?action=detail&id=" + contractId + "&success=Machine removed successfully");
        } else {
            response.sendRedirect(request.getContextPath() + "/contracts?action=detail&id=" + contractId + "&error=Failed to remove machine");
        }
    }
}

