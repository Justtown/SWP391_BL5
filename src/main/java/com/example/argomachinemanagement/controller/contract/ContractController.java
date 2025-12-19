package com.example.argomachinemanagement.controller.contract;

import com.example.argomachinemanagement.dal.ContractDAO;
import com.example.argomachinemanagement.dal.MachineDAO;
import com.example.argomachinemanagement.dal.MachineTypeDAO;
import com.example.argomachinemanagement.dal.OrderDAO;
import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.entity.Contract;
import com.example.argomachinemanagement.entity.Machine;
import com.example.argomachinemanagement.entity.MachineType;
import com.example.argomachinemanagement.entity.Order;
import com.example.argomachinemanagement.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@WebServlet(
        name = "ContractController",
        urlPatterns = {"/contracts", "/manager/contracts", "/sale/contracts", "/customer/contracts"}
)
public class ContractController extends HttpServlet {
    
    private static final int PAGE_SIZE = 5;
    private static final Logger LOGGER = Logger.getLogger(ContractController.class.getName());
    
    private ContractDAO contractDAO;
    private UserDAO userDAO;
    private MachineTypeDAO machineTypeDAO;
    private MachineDAO machineDAO;
    private OrderDAO orderDAO;
    
    @Override
    public void init() throws ServletException {
        contractDAO = new ContractDAO();
        userDAO = new UserDAO();
        machineTypeDAO = new MachineTypeDAO();
        machineDAO = new MachineDAO();
        orderDAO = new OrderDAO();
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
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            handleCreateContract(request, response);
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
        String statusParam = request.getParameter("status");
        String keyword = request.getParameter("keyword");

        String statusFilterForQuery = statusParam;
        
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
            statusFilterForQuery, keyword, customerId, managerId
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
        request.setAttribute("statusFilter", statusParam != null ? statusParam : "All Status");
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
     * Có thể prefill từ Order nếu có orderId
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

        // Kiểm tra xem có orderId để prefill không
        String orderIdStr = request.getParameter("orderId");
        Order order = null;
        if (orderIdStr != null && !orderIdStr.trim().isEmpty()) {
            try {
                int orderId = Integer.parseInt(orderIdStr);
                order = orderDAO.findById(orderId);
            } catch (NumberFormatException e) {
                // Ignore invalid orderId
            }
        }

        // Sinh mã contract mới (hoặc dùng từ Order nếu có)
        String nextCode = order != null && order.getContractCode() != null 
                ? order.getContractCode() 
                : contractDAO.getNextContractCode();

        // Lấy danh sách customer active và manager/sale active
        List<User> customers = userDAO.findActiveUsersByRole("customer");
        List<User> managers = new ArrayList<>();
        managers.addAll(userDAO.findActiveUsersByRole("manager"));
        managers.addAll(userDAO.findActiveUsersByRole("sale"));
        
        // Lấy danh sách machines (để giống form tạo Order)
        List<Machine> machines = machineDAO.findAll();

        // Prefill từ Order nếu có
        if (order != null) {
            request.setAttribute("contractCode", order.getContractCode());
            request.setAttribute("customerName", order.getCustomerName());
            request.setAttribute("customerPhone", order.getCustomerPhone());
            request.setAttribute("customerAddress", order.getCustomerAddress());
            request.setAttribute("machineId", order.getMachineId());
            request.setAttribute("quantity", order.getQuantity());
            request.setAttribute("startDate", order.getStartDate() != null ? order.getStartDate().toString() : null);
            request.setAttribute("endDate", order.getEndDate() != null ? order.getEndDate().toString() : null);
            request.setAttribute("totalCost", order.getTotalCost());
            request.setAttribute("serviceDescription", order.getServiceDescription());
            request.setAttribute("fromOrder", true);
            request.setAttribute("orderId", order.getId());
        } else {
            request.setAttribute("contractCode", nextCode);
        }

        request.setAttribute("customers", customers);
        request.setAttribute("managers", managers);
        request.setAttribute("machines", machines);
        
        // Set manager mặc định là user hiện tại nếu là manager
        if ("manager".equalsIgnoreCase(roleName)) {
            request.setAttribute("defaultManagerId", userId);
        }

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
        String customerName = request.getParameter("customerName");
        String customerPhone = request.getParameter("customerPhone");
        String customerAddress = request.getParameter("customerAddress");
        String machineIdStr = request.getParameter("machineId");
        String quantityStr = request.getParameter("quantity");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String totalCostStr = request.getParameter("totalCost");
        String serviceDescription = request.getParameter("serviceDescription");
        String note = request.getParameter("note"); // legacy
        // String orderIdStr = request.getParameter("orderId"); // Nếu tạo từ Order (có thể dùng sau để update Order status)
        
        // Validate dữ liệu
        List<String> errors = new ArrayList<>();
        
        if (contractCode == null || contractCode.trim().isEmpty()) {
            errors.add("Contract code is required");
        }
        
        if (customerName == null || customerName.trim().isEmpty()) {
            errors.add("Customer name is required");
        }
        
        // Resolve customerId from input (if not provided)
        if (customerIdStr == null || customerIdStr.trim().isEmpty()) {
            User u = userDAO.findByUsername(customerName != null ? customerName.trim() : null);
            if (u == null) {
                u = userDAO.findByFullName(customerName != null ? customerName.trim() : null);
            }
            if (u != null) {
                customerIdStr = String.valueOf(u.getId());
            } else {
                errors.add("Customer is required");
            }
        }

        // If managerId not provided, use current user (manager/sale)
        if (managerIdStr == null || managerIdStr.trim().isEmpty()) {
            managerIdStr = userId != null ? String.valueOf(userId) : null;
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

        if (machineIdStr == null || machineIdStr.trim().isEmpty()) {
            errors.add("Machine is required");
        }

        if (serviceDescription == null || serviceDescription.trim().isEmpty()) {
            // allow legacy note field
            if (note == null || note.trim().isEmpty()) {
                errors.add("Service description is required");
            } else {
                serviceDescription = note;
            }
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
            request.setAttribute("customerName", customerName);
            request.setAttribute("customerPhone", customerPhone);
            request.setAttribute("customerAddress", customerAddress);
            request.setAttribute("selectedCustomerId", customerIdStr);
            request.setAttribute("selectedManagerId", managerIdStr);
            request.setAttribute("machineId", machineIdStr);
            request.setAttribute("quantity", quantityStr);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            request.setAttribute("totalCost", totalCostStr);
            request.setAttribute("serviceDescription", serviceDescription);
            
            // Load lại danh sách customers, managers và machine types
            List<User> customers = userDAO.findActiveUsersByRole("customer");
            List<User> managers = new ArrayList<>();
            managers.addAll(userDAO.findActiveUsersByRole("manager"));
            managers.addAll(userDAO.findActiveUsersByRole("sale"));
            request.setAttribute("customers", customers);
            request.setAttribute("managers", managers);
            request.setAttribute("machines", machineDAO.findAll());
            
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

            // startDate must be today or later
            Date today = Date.valueOf(LocalDate.now());
            if (startDate.before(today)) {
                errors.add("Ngày bắt đầu thuê phải từ hôm nay trở đi");
                request.setAttribute("errors", errors);
                request.setAttribute("contractCode", contractCode);
                request.setAttribute("customerName", customerName);
                request.setAttribute("customerPhone", customerPhone);
                request.setAttribute("customerAddress", customerAddress);
                request.setAttribute("selectedCustomerId", customerIdStr);
                request.setAttribute("selectedManagerId", managerIdStr);
                request.setAttribute("machineId", machineIdStr);
                request.setAttribute("quantity", quantityStr);
                request.setAttribute("startDate", startDateStr);
                request.setAttribute("endDate", endDateStr);
                request.setAttribute("totalCost", totalCostStr);
                request.setAttribute("serviceDescription", serviceDescription);

                List<User> customers = userDAO.findActiveUsersByRole("customer");
                List<User> managers = new ArrayList<>();
                managers.addAll(userDAO.findActiveUsersByRole("manager"));
                managers.addAll(userDAO.findActiveUsersByRole("sale"));
                request.setAttribute("customers", customers);
                request.setAttribute("managers", managers);
                request.setAttribute("machines", machineDAO.findAll());

                request.getRequestDispatcher("/view/dashboard/contract/contract-create.jsp")
                        .forward(request, response);
                return;
            }
            
            // Kiểm tra endDate >= startDate
            if (endDate.before(startDate)) {
                errors.add("End date must be after or equal to start date");
                request.setAttribute("errors", errors);
                request.setAttribute("contractCode", contractCode);
                request.setAttribute("customerName", customerName);
                request.setAttribute("customerPhone", customerPhone);
                request.setAttribute("customerAddress", customerAddress);
                request.setAttribute("selectedCustomerId", customerIdStr);
                request.setAttribute("selectedManagerId", managerIdStr);
                request.setAttribute("machineId", machineIdStr);
                request.setAttribute("quantity", quantityStr);
                request.setAttribute("startDate", startDateStr);
                request.setAttribute("endDate", endDateStr);
                request.setAttribute("totalCost", totalCostStr);
                request.setAttribute("note", note);
                
                List<User> customers = userDAO.findActiveUsersByRole("customer");
                List<User> managers = new ArrayList<>();
                managers.addAll(userDAO.findActiveUsersByRole("manager"));
                managers.addAll(userDAO.findActiveUsersByRole("sale"));
                request.setAttribute("customers", customers);
                request.setAttribute("managers", managers);
                request.setAttribute("machines", machineDAO.findAll());
                
                request.getRequestDispatcher("/view/dashboard/contract/contract-create.jsp")
                        .forward(request, response);
                return;
            }
        } catch (IllegalArgumentException e) {
            errors.add("Invalid date format");
            request.setAttribute("errors", errors);
            request.setAttribute("contractCode", contractCode);
            request.setAttribute("customerName", customerName);
            request.setAttribute("customerPhone", customerPhone);
            request.setAttribute("customerAddress", customerAddress);
            request.setAttribute("selectedCustomerId", customerIdStr);
            request.setAttribute("selectedManagerId", managerIdStr);
            request.setAttribute("machineId", machineIdStr);
            request.setAttribute("quantity", quantityStr);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            request.setAttribute("totalCost", totalCostStr);
            request.setAttribute("note", note);
            
            List<User> customers = userDAO.findActiveUsersByRole("customer");
            List<User> managers = new ArrayList<>();
            managers.addAll(userDAO.findActiveUsersByRole("manager"));
            managers.addAll(userDAO.findActiveUsersByRole("sale"));
            request.setAttribute("customers", customers);
            request.setAttribute("managers", managers);
            request.setAttribute("machines", machineDAO.findAll());
            
            request.getRequestDispatcher("/view/dashboard/contract/contract-create.jsp")
                    .forward(request, response);
            return;
        }
        
        // Parse các giá trị số
        Integer machineId = null;
        Integer machineTypeId = null;
        Integer quantity = null;
        Double totalCost = null;
        try {
            if (machineIdStr != null && !machineIdStr.trim().isEmpty()) {
                machineId = Integer.parseInt(machineIdStr);
                Machine selectedMachine = machineDAO.findById(machineId);
                if (selectedMachine != null) {
                    machineTypeId = selectedMachine.getMachineTypeId();

                    String mStatus = selectedMachine.getStatus() != null ? selectedMachine.getStatus().trim().toUpperCase() : "UNKNOWN";
                    boolean rentable = selectedMachine.getIsRentable() != null ? selectedMachine.getIsRentable() : false;
                    if (!"ACTIVE".equals(mStatus) || !rentable) {
                        errors.add("Máy đang ở trạng thái " + mStatus + " nên không thể tạo hợp đồng");
                        LOGGER.warning("[CreateContract][BLOCKED] machineId=" + machineId
                                + ", machineCode=" + selectedMachine.getMachineCode()
                                + ", status=" + mStatus
                                + ", isRentable=" + rentable);
                    }
                }
            }
            if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                quantity = Integer.parseInt(quantityStr);
            }
            if (totalCostStr != null && !totalCostStr.trim().isEmpty()) {
                totalCost = Double.parseDouble(totalCostStr);
            }
        } catch (NumberFormatException e) {
            errors.add("Invalid number format");
            request.setAttribute("errors", errors);
            request.setAttribute("contractCode", contractCode);
            request.setAttribute("customerName", customerName);
            request.setAttribute("customerPhone", customerPhone);
            request.setAttribute("customerAddress", customerAddress);
            request.setAttribute("selectedCustomerId", customerIdStr);
            request.setAttribute("selectedManagerId", managerIdStr);
            request.setAttribute("machineId", machineIdStr);
            request.setAttribute("quantity", quantityStr);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            request.setAttribute("totalCost", totalCostStr);
            request.setAttribute("serviceDescription", serviceDescription);

            List<User> customers = userDAO.findActiveUsersByRole("customer");
            List<User> managers = new ArrayList<>();
            managers.addAll(userDAO.findActiveUsersByRole("manager"));
            managers.addAll(userDAO.findActiveUsersByRole("sale"));
            request.setAttribute("customers", customers);
            request.setAttribute("managers", managers);
            request.setAttribute("machines", machineDAO.findAll());

            request.getRequestDispatcher("/view/dashboard/contract/contract-create.jsp")
                    .forward(request, response);
            return;
        }

        // If machine validation failed, return to form
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("contractCode", contractCode);
            request.setAttribute("customerName", customerName);
            request.setAttribute("customerPhone", customerPhone);
            request.setAttribute("customerAddress", customerAddress);
            request.setAttribute("selectedCustomerId", customerIdStr);
            request.setAttribute("selectedManagerId", managerIdStr);
            request.setAttribute("machineId", machineIdStr);
            request.setAttribute("quantity", quantityStr);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            request.setAttribute("totalCost", totalCostStr);
            request.setAttribute("serviceDescription", serviceDescription);

            List<User> customers = userDAO.findActiveUsersByRole("customer");
            List<User> managers = new ArrayList<>();
            managers.addAll(userDAO.findActiveUsersByRole("manager"));
            managers.addAll(userDAO.findActiveUsersByRole("sale"));
            request.setAttribute("customers", customers);
            request.setAttribute("managers", managers);
            request.setAttribute("machines", machineDAO.findAll());

            request.getRequestDispatcher("/view/dashboard/contract/contract-create.jsp")
                    .forward(request, response);
            return;
        }
        
        // Tạo Contract object với các trường mới
        Contract contract = Contract.builder()
                .contractCode(contractCode.trim())
                .customerId(Integer.parseInt(customerIdStr))
                .managerId(Integer.parseInt(managerIdStr))
                .startDate(startDate)
                .endDate(endDate)
                // Sau khi manager tạo hợp đồng -> ACTIVE (không để DRAFT)
                .status("APPROVED")
                .serviceDescription(serviceDescription != null ? serviceDescription.trim() : null)
                .note(serviceDescription != null ? serviceDescription.trim() : (note != null ? note.trim() : null))
                .customerName(customerName != null ? customerName.trim() : null)
                .customerPhone(customerPhone != null ? customerPhone.trim() : null)
                .customerAddress(customerAddress != null ? customerAddress.trim() : null)
                .machineId(machineId)
                .machineTypeId(machineTypeId)
                .quantity(quantity)
                .totalCost(totalCost)
                .build();

        // Defensive: prevent duplicate contract code
        Integer existedId = contractDAO.findIdByContractCode(contract.getContractCode());
        if (existedId != null) {
            errors.add("Contract code already exists. Please reload page to get a new code.");
            request.setAttribute("errors", errors);
            request.setAttribute("contractCode", contractCode);
            request.setAttribute("customerName", customerName);
            request.setAttribute("customerPhone", customerPhone);
            request.setAttribute("customerAddress", customerAddress);
            request.setAttribute("selectedCustomerId", customerIdStr);
            request.setAttribute("selectedManagerId", managerIdStr);
            request.setAttribute("machineId", machineIdStr);
            request.setAttribute("quantity", quantityStr);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            request.setAttribute("totalCost", totalCostStr);
            request.setAttribute("serviceDescription", serviceDescription);

            List<User> customers = userDAO.findActiveUsersByRole("customer");
            List<User> managers = new ArrayList<>();
            managers.addAll(userDAO.findActiveUsersByRole("manager"));
            managers.addAll(userDAO.findActiveUsersByRole("sale"));
            request.setAttribute("customers", customers);
            request.setAttribute("managers", managers);
            request.setAttribute("machines", machineDAO.findAll());

            request.getRequestDispatcher("/view/dashboard/contract/contract-create.jsp")
                    .forward(request, response);
            return;
        }
        
        // Lưu vào database
        int contractId = contractDAO.insert(contract);
        
        if (contractId > 0) {
            // Nếu tạo từ Order, có thể cập nhật status của Order (tùy chọn)
            // Ví dụ: orderDAO.updateStatus(orderId, "CONVERTED_TO_CONTRACT");
            
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
            String daoErr = contractDAO.getLastError();
            if (daoErr != null && !daoErr.trim().isEmpty()) {
                LOGGER.warning("[CreateContract][FAILED] " + daoErr);
                errors.add("DB error: " + daoErr);
            }
            request.setAttribute("errors", errors);
            request.setAttribute("contractCode", contractCode);
            request.setAttribute("customerName", customerName);
            request.setAttribute("customerPhone", customerPhone);
            request.setAttribute("customerAddress", customerAddress);
            request.setAttribute("selectedCustomerId", customerIdStr);
            request.setAttribute("selectedManagerId", managerIdStr);
            request.setAttribute("machineId", machineIdStr);
            request.setAttribute("quantity", quantityStr);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);
            request.setAttribute("totalCost", totalCostStr);
            request.setAttribute("note", note);
            
            List<User> customers = userDAO.findActiveUsersByRole("customer");
            List<User> managers = new ArrayList<>();
            managers.addAll(userDAO.findActiveUsersByRole("manager"));
            managers.addAll(userDAO.findActiveUsersByRole("sale"));
            request.setAttribute("customers", customers);
            request.setAttribute("managers", managers);
            request.setAttribute("machines", machineDAO.findAll());
            
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

        // Load machine types để hiển thị tên loại máy (cho cả customer và manager)
        List<MachineType> machineTypes = machineTypeDAO.findAll();
        
        request.setAttribute("contract", contract);
        request.setAttribute("machineTypes", machineTypes);

        // Customer sẽ sử dụng view "my contract" riêng, các role khác dùng view chung
        if (currentUser != null && "customer".equalsIgnoreCase(currentUser.getRoleName())) {
            request.getRequestDispatcher("/view/dashboard/contract/contract-detail-customer.jsp")
                    .forward(request, response);
        } else {
            request.getRequestDispatcher("/view/dashboard/contract/contract-detail.jsp")
                    .forward(request, response);
        }
    }
}

