package com.example.argomachinemanagement.controller.dashboard.manager.contract;

import com.example.argomachinemanagement.dal.*;
import com.example.argomachinemanagement.entity.*;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller quản lý Contracts cho Manager
 * URL: /manager/contracts
 */
@WebServlet(name = "ManagerContractController", urlPatterns = {"/manager/contracts"})
public class ManagerContractController extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    private ContractDAO contractDAO;
    private ContractItemDAO contractItemDAO;
    private MachineAssetDAO machineAssetDAO;
    private UserDAO userDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        contractDAO = new ContractDAO();
        contractItemDAO = new ContractItemDAO();
        machineAssetDAO = new MachineAssetDAO();
        userDAO = new UserDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            handleList(request, response);
        } else if (action.equals("detail")) {
            handleDetail(request, response);
        } else if (action.equals("create")) {
            handleCreateForm(request, response);
        } else if (action.equals("edit")) {
            handleEditForm(request, response);
        } else if (action.equals("getAvailableAssets")) {
            handleGetAvailableAssets(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/manager/contracts");
            return;
        }

        switch (action) {
            case "create":
                handleCreate(request, response);
                break;
            case "update":
                handleUpdate(request, response);
                break;
            case "activate":
                handleActivate(request, response);
                break;
            case "finish":
                handleFinish(request, response);
                break;
            case "cancel":
                handleCancel(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
        }
    }

    /**
     * Lấy current user từ session
     */
    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (User) session.getAttribute("user");
    }

    /**
     * Hiển thị danh sách contracts
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/authen/login");
            return;
        }

        // Get filter parameters
        String status = request.getParameter("status");
        String keyword = request.getParameter("keyword");

        // Get pagination parameter
        String pageStr = request.getParameter("page");
        int currentPage = 1;
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageStr);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        // Get all contracts with filters
        List<Contract> allContracts = contractDAO.findByFilters(status, keyword, null, null);

        // Load items for each contract and calculate total
        for (Contract contract : allContracts) {
            List<ContractItem> items = contractItemDAO.findByContractId(contract.getId());
            contract.setItems(items);
        }

        // Pagination
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

        // Count by status
        int draftCount = contractDAO.countByStatus("DRAFT");
        int activeCount = contractDAO.countByStatus("ACTIVE");
        int finishedCount = contractDAO.countByStatus("FINISHED");

        // Set attributes
        request.setAttribute("contracts", paginatedContracts);
        request.setAttribute("statusFilter", status != null ? status : "All");
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalContracts", totalContracts);
        request.setAttribute("draftCount", draftCount);
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("finishedCount", finishedCount);

        // Check for messages
        String successMsg = (String) request.getSession().getAttribute("successMsg");
        String errorMsg = (String) request.getSession().getAttribute("errorMsg");
        if (successMsg != null) {
            request.setAttribute("successMsg", successMsg);
            request.getSession().removeAttribute("successMsg");
        }
        if (errorMsg != null) {
            request.setAttribute("errorMsg", errorMsg);
            request.getSession().removeAttribute("errorMsg");
        }

        request.getRequestDispatcher("/view/dashboard/manager/contract/contract-list.jsp").forward(request, response);
    }

    /**
     * Hiển thị chi tiết contract (JSON cho modal hoặc page)
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String format = request.getParameter("format");
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            if ("json".equals(format)) {
                sendJsonError(response, "Contract ID is required");
            } else {
                request.getSession().setAttribute("errorMsg", "Contract ID is required");
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
            }
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Contract contract = contractDAO.findById(id);

            if (contract == null) {
                if ("json".equals(format)) {
                    sendJsonError(response, "Contract not found");
                } else {
                    request.getSession().setAttribute("errorMsg", "Không tìm thấy hợp đồng");
                    response.sendRedirect(request.getContextPath() + "/manager/contracts");
                }
                return;
            }

            // Load contract items
            List<ContractItem> items = contractItemDAO.findByContractId(id);
            contract.setItems(items);

            // Calculate total
            BigDecimal total = contractItemDAO.getTotalPriceByContractId(id);

            if ("json".equals(format)) {
                // Return JSON for modal
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("contract", contract);
                result.put("total", total);
                out.print(gson.toJson(result));
            } else {
                // Forward to detail page
                request.setAttribute("contract", contract);
                request.setAttribute("totalPrice", total);
                request.getRequestDispatcher("/view/dashboard/manager/contract/contract-detail.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            if ("json".equals(format)) {
                sendJsonError(response, "Invalid contract ID");
            } else {
                request.getSession().setAttribute("errorMsg", "Contract ID không hợp lệ");
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
            }
        }
    }

    /**
     * Hiển thị form tạo contract
     */
    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/authen/login");
            return;
        }

        // Get customers for dropdown
        List<User> customers = userDAO.findByRole("customer");
        request.setAttribute("customers", customers);

        // Get available machines
        List<MachineAsset> availableAssets = machineAssetDAO.findAvailable();
        request.setAttribute("availableAssets", availableAssets);

        // Generate contract code
        String contractCode = contractDAO.generateContractCode();
        request.setAttribute("contractCode", contractCode);

        request.getRequestDispatcher("/view/dashboard/manager/contract/contract-create.jsp").forward(request, response);
    }

    /**
     * Hiển thị form sửa contract (chỉ DRAFT)
     */
    private void handleEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/authen/login");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Contract ID is required");
            response.sendRedirect(request.getContextPath() + "/manager/contracts");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Contract contract = contractDAO.findById(id);

            if (contract == null) {
                request.getSession().setAttribute("errorMsg", "Không tìm thấy hợp đồng");
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
                return;
            }

            if (!"DRAFT".equals(contract.getStatus())) {
                request.getSession().setAttribute("errorMsg", "Chỉ có thể sửa hợp đồng ở trạng thái DRAFT");
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
                return;
            }

            // Load contract items
            List<ContractItem> items = contractItemDAO.findByContractId(id);
            contract.setItems(items);

            // Get customers for dropdown
            List<User> customers = userDAO.findByRole("customer");
            request.setAttribute("customers", customers);

            // Get available machines (including ones already in this contract)
            List<MachineAsset> availableAssets = machineAssetDAO.findAvailable();
            request.setAttribute("availableAssets", availableAssets);

            request.setAttribute("contract", contract);
            request.setAttribute("editMode", true);

            request.getRequestDispatcher("/view/dashboard/manager/contract/contract-create.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMsg", "Contract ID không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/manager/contracts");
        }
    }

    /**
     * API lấy available assets (for AJAX)
     */
    private void handleGetAvailableAssets(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        List<MachineAsset> assets = machineAssetDAO.findAvailable();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("assets", assets);
        out.print(gson.toJson(result));
    }

    /**
     * Tạo contract mới
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/authen/login");
            return;
        }

        try {
            String contractCode = request.getParameter("contractCode");
            String customerIdStr = request.getParameter("customerId");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String status = request.getParameter("status");
            String note = request.getParameter("note");
            String[] assetIds = request.getParameterValues("assetId[]");
            String[] prices = request.getParameterValues("price[]");
            String[] notes = request.getParameterValues("itemNote[]");

            // Validation
            if (contractCode == null || contractCode.trim().isEmpty()) {
                contractCode = contractDAO.generateContractCode();
            }

            if (customerIdStr == null || customerIdStr.isEmpty()) {
                request.getSession().setAttribute("errorMsg", "Vui lòng chọn khách hàng");
                response.sendRedirect(request.getContextPath() + "/manager/contracts?action=create");
                return;
            }

            if (startDateStr == null || startDateStr.isEmpty()) {
                request.getSession().setAttribute("errorMsg", "Vui lòng chọn ngày bắt đầu");
                response.sendRedirect(request.getContextPath() + "/manager/contracts?action=create");
                return;
            }

            if (assetIds == null || assetIds.length == 0) {
                request.getSession().setAttribute("errorMsg", "Vui lòng chọn ít nhất một máy");
                response.sendRedirect(request.getContextPath() + "/manager/contracts?action=create");
                return;
            }

            // Check if contract code already exists
            if (contractDAO.isContractCodeExists(contractCode)) {
                contractCode = contractDAO.generateContractCode();
            }

            // Parse dates
            Date startDate = Date.valueOf(startDateStr);
            Date endDate = null;
            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = Date.valueOf(endDateStr);
            }

            // Default status
            if (status == null || status.isEmpty()) {
                status = "DRAFT";
            }

            // Create contract using transaction
            Connection conn = null;
            try {
                conn = new DBContext().getConnection();
                conn.setAutoCommit(false);

                // 1. Insert contract
                String insertContractSql = "INSERT INTO contracts (contract_code, customer_id, manager_id, start_date, end_date, status, note) " +
                                           "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psContract = conn.prepareStatement(insertContractSql, Statement.RETURN_GENERATED_KEYS);
                psContract.setString(1, contractCode);
                psContract.setInt(2, Integer.parseInt(customerIdStr));
                psContract.setInt(3, currentUser.getId());
                psContract.setDate(4, startDate);
                psContract.setDate(5, endDate);
                psContract.setString(6, status);
                psContract.setString(7, note);
                psContract.executeUpdate();

                ResultSet rsContract = psContract.getGeneratedKeys();
                int contractId = 0;
                if (rsContract.next()) {
                    contractId = rsContract.getInt(1);
                }
                rsContract.close();
                psContract.close();

                // 2. Insert contract items
                String insertItemSql = "INSERT INTO contract_items (contract_id, asset_id, price, note) VALUES (?, ?, ?, ?)";

                for (int i = 0; i < assetIds.length; i++) {
                    if (assetIds[i] != null && !assetIds[i].isEmpty()) {
                        PreparedStatement psItem = conn.prepareStatement(insertItemSql);
                        psItem.setInt(1, contractId);
                        psItem.setInt(2, Integer.parseInt(assetIds[i]));
                        psItem.setBigDecimal(3, prices != null && i < prices.length && prices[i] != null && !prices[i].isEmpty()
                                ? new BigDecimal(prices[i]) : BigDecimal.ZERO);
                        psItem.setString(4, notes != null && i < notes.length ? notes[i] : null);
                        psItem.executeUpdate();
                        psItem.close();

                        // 3. If status is ACTIVE, update rental_status
                        if ("ACTIVE".equals(status)) {
                            String updateAssetSql = "UPDATE machine_assets SET rental_status = 'RENTED' WHERE id = ?";
                            PreparedStatement psAsset = conn.prepareStatement(updateAssetSql);
                            psAsset.setInt(1, Integer.parseInt(assetIds[i]));
                            psAsset.executeUpdate();
                            psAsset.close();
                        }
                    }
                }

                conn.commit();
                request.getSession().setAttribute("successMsg", "Tạo hợp đồng " + contractCode + " thành công");

            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                request.getSession().setAttribute("errorMsg", "Lỗi khi tạo hợp đồng: " + e.getMessage());
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/contracts");
    }

    /**
     * Cập nhật contract (chỉ DRAFT)
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/authen/login");
            return;
        }

        try {
            String idStr = request.getParameter("id");
            String customerIdStr = request.getParameter("customerId");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            String note = request.getParameter("note");
            String[] assetIds = request.getParameterValues("assetId[]");
            String[] prices = request.getParameterValues("price[]");
            String[] notes = request.getParameterValues("itemNote[]");

            if (idStr == null || idStr.isEmpty()) {
                request.getSession().setAttribute("errorMsg", "Contract ID is required");
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
                return;
            }

            int contractId = Integer.parseInt(idStr);
            Contract existingContract = contractDAO.findById(contractId);

            if (existingContract == null) {
                request.getSession().setAttribute("errorMsg", "Không tìm thấy hợp đồng");
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
                return;
            }

            if (!"DRAFT".equals(existingContract.getStatus())) {
                request.getSession().setAttribute("errorMsg", "Chỉ có thể sửa hợp đồng ở trạng thái DRAFT");
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
                return;
            }

            // Update using transaction
            Connection conn = null;
            try {
                conn = new DBContext().getConnection();
                conn.setAutoCommit(false);

                // 1. Update contract
                String updateContractSql = "UPDATE contracts SET customer_id = ?, start_date = ?, end_date = ?, note = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                PreparedStatement psContract = conn.prepareStatement(updateContractSql);
                psContract.setInt(1, Integer.parseInt(customerIdStr));
                psContract.setDate(2, Date.valueOf(startDateStr));
                psContract.setDate(3, endDateStr != null && !endDateStr.isEmpty() ? Date.valueOf(endDateStr) : null);
                psContract.setString(4, note);
                psContract.setInt(5, contractId);
                psContract.executeUpdate();
                psContract.close();

                // 2. Delete old items
                String deleteItemsSql = "DELETE FROM contract_items WHERE contract_id = ?";
                PreparedStatement psDelete = conn.prepareStatement(deleteItemsSql);
                psDelete.setInt(1, contractId);
                psDelete.executeUpdate();
                psDelete.close();

                // 3. Insert new items
                String insertItemSql = "INSERT INTO contract_items (contract_id, asset_id, price, note) VALUES (?, ?, ?, ?)";
                if (assetIds != null) {
                    for (int i = 0; i < assetIds.length; i++) {
                        if (assetIds[i] != null && !assetIds[i].isEmpty()) {
                            PreparedStatement psItem = conn.prepareStatement(insertItemSql);
                            psItem.setInt(1, contractId);
                            psItem.setInt(2, Integer.parseInt(assetIds[i]));
                            psItem.setBigDecimal(3, prices != null && i < prices.length && prices[i] != null && !prices[i].isEmpty()
                                    ? new BigDecimal(prices[i]) : BigDecimal.ZERO);
                            psItem.setString(4, notes != null && i < notes.length ? notes[i] : null);
                            psItem.executeUpdate();
                            psItem.close();
                        }
                    }
                }

                conn.commit();
                request.getSession().setAttribute("successMsg", "Cập nhật hợp đồng thành công");

            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                request.getSession().setAttribute("errorMsg", "Lỗi khi cập nhật: " + e.getMessage());
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/contracts");
    }

    /**
     * Kích hoạt contract (DRAFT → ACTIVE)
     */
    private void handleActivate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/authen/login");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Contract ID is required");
            response.sendRedirect(request.getContextPath() + "/manager/contracts");
            return;
        }

        try {
            int contractId = Integer.parseInt(idStr);
            Contract contract = contractDAO.findById(contractId);

            if (contract == null) {
                request.getSession().setAttribute("errorMsg", "Không tìm thấy hợp đồng");
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
                return;
            }

            if (!"DRAFT".equals(contract.getStatus())) {
                request.getSession().setAttribute("errorMsg", "Chỉ có thể kích hoạt hợp đồng DRAFT");
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
                return;
            }

            // Check all machines are still available
            List<ContractItem> items = contractItemDAO.findByContractId(contractId);
            List<String> unavailableMachines = new ArrayList<>();

            for (ContractItem item : items) {
                MachineAsset asset = machineAssetDAO.findById(item.getAssetId());
                if (asset == null ||
                    !"ACTIVE".equals(asset.getStatus()) ||
                    !"AVAILABLE".equals(asset.getRentalStatus())) {
                    unavailableMachines.add(item.getSerialNumber() != null ?
                            item.getSerialNumber() : "ID: " + item.getAssetId());
                }
            }

            if (!unavailableMachines.isEmpty()) {
                request.getSession().setAttribute("errorMsg",
                        "Không thể kích hoạt. Các máy sau không còn sẵn sàng: " + String.join(", ", unavailableMachines));
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
                return;
            }

            // Activate using transaction
            Connection conn = null;
            try {
                conn = new DBContext().getConnection();
                conn.setAutoCommit(false);

                // 1. Update contract status
                String updateContractSql = "UPDATE contracts SET status = 'ACTIVE', updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                PreparedStatement psContract = conn.prepareStatement(updateContractSql);
                psContract.setInt(1, contractId);
                psContract.executeUpdate();
                psContract.close();

                // 2. Update all assets to RENTED
                String updateAssetSql = "UPDATE machine_assets SET rental_status = 'RENTED' WHERE id = ?";
                for (ContractItem item : items) {
                    PreparedStatement psAsset = conn.prepareStatement(updateAssetSql);
                    psAsset.setInt(1, item.getAssetId());
                    psAsset.executeUpdate();
                    psAsset.close();
                }

                conn.commit();
                request.getSession().setAttribute("successMsg", "Đã kích hoạt hợp đồng thành công");

            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                request.getSession().setAttribute("errorMsg", "Lỗi khi kích hoạt: " + e.getMessage());
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/contracts");
    }

    /**
     * Hoàn thành contract (ACTIVE → FINISHED) - trả máy
     */
    private void handleFinish(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/authen/login");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Contract ID is required");
            response.sendRedirect(request.getContextPath() + "/manager/contracts");
            return;
        }

        try {
            int contractId = Integer.parseInt(idStr);
            Contract contract = contractDAO.findById(contractId);

            if (contract == null) {
                request.getSession().setAttribute("errorMsg", "Không tìm thấy hợp đồng");
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
                return;
            }

            if (!"ACTIVE".equals(contract.getStatus())) {
                request.getSession().setAttribute("errorMsg", "Chỉ có thể hoàn thành hợp đồng ACTIVE");
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
                return;
            }

            // Finish using transaction
            Connection conn = null;
            try {
                conn = new DBContext().getConnection();
                conn.setAutoCommit(false);

                // 1. Update contract status
                String updateContractSql = "UPDATE contracts SET status = 'FINISHED', updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                PreparedStatement psContract = conn.prepareStatement(updateContractSql);
                psContract.setInt(1, contractId);
                psContract.executeUpdate();
                psContract.close();

                // 2. Return all machines (set rental_status = AVAILABLE)
                List<ContractItem> items = contractItemDAO.findByContractId(contractId);
                String updateAssetSql = "UPDATE machine_assets SET rental_status = 'AVAILABLE' WHERE id = ?";
                for (ContractItem item : items) {
                    PreparedStatement psAsset = conn.prepareStatement(updateAssetSql);
                    psAsset.setInt(1, item.getAssetId());
                    psAsset.executeUpdate();
                    psAsset.close();
                }

                conn.commit();
                request.getSession().setAttribute("successMsg", "Đã hoàn thành hợp đồng và trả máy thành công");

            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                request.getSession().setAttribute("errorMsg", "Lỗi khi hoàn thành: " + e.getMessage());
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/contracts");
    }

    /**
     * Hủy contract (chỉ DRAFT)
     */
    private void handleCancel(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/authen/login");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Contract ID is required");
            response.sendRedirect(request.getContextPath() + "/manager/contracts");
            return;
        }

        try {
            int contractId = Integer.parseInt(idStr);
            Contract contract = contractDAO.findById(contractId);

            if (contract == null) {
                request.getSession().setAttribute("errorMsg", "Không tìm thấy hợp đồng");
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
                return;
            }

            if (!"DRAFT".equals(contract.getStatus())) {
                request.getSession().setAttribute("errorMsg", "Chỉ có thể hủy hợp đồng ở trạng thái DRAFT");
                response.sendRedirect(request.getContextPath() + "/manager/contracts");
                return;
            }

            boolean success = contractDAO.updateStatus(contractId, "CANCELLED");

            if (success) {
                request.getSession().setAttribute("successMsg", "Đã hủy hợp đồng thành công");
            } else {
                request.getSession().setAttribute("errorMsg", "Hủy hợp đồng thất bại");
            }

        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/contracts");
    }

    /**
     * Helper method to send JSON error
     */
    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        out.print(gson.toJson(result));
    }

    @Override
    public String getServletInfo() {
        return "Manager Contract Controller - Manage contracts directly";
    }
}
