package com.example.argomachinemanagement.controller.dashboard.manager.order;

import com.example.argomachinemanagement.dal.MachineAssetDAO;
import com.example.argomachinemanagement.dal.OrderDAO;
import com.example.argomachinemanagement.dal.OrderItemDAO;
import com.example.argomachinemanagement.entity.MachineAsset;
import com.example.argomachinemanagement.entity.Order;
import com.example.argomachinemanagement.entity.OrderItem;
import com.example.argomachinemanagement.entity.User;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller quản lý duyệt Orders cho Manager
 * URL: /manager/orders
 */
@WebServlet(name = "OrderApprovalController", urlPatterns = {"/manager/orders"})
public class OrderApprovalController extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private MachineAssetDAO machineAssetDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
        orderItemDAO = new OrderItemDAO();
        machineAssetDAO = new MachineAssetDAO();
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
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/manager/orders");
            return;
        }

        switch (action) {
            case "approve":
                handleApprove(request, response);
                break;
            case "reject":
                handleReject(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/manager/orders");
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
     * Hiển thị danh sách orders (ưu tiên PENDING)
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

        // Get orders for manager (all orders, prioritize PENDING)
        List<Order> allOrders = orderDAO.findForManager(status, keyword);

        // Pagination
        int totalOrders = allOrders.size();
        int totalPages = (int) Math.ceil((double) totalOrders / PAGE_SIZE);
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }

        int startIndex = (currentPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalOrders);

        List<Order> paginatedOrders = new ArrayList<>();
        if (startIndex < totalOrders) {
            paginatedOrders = allOrders.subList(startIndex, endIndex);
        }

        // Load items for each order
        for (Order order : paginatedOrders) {
            List<OrderItem> items = orderItemDAO.findByOrderId(order.getId());
            order.setItems(items);
        }

        // Count pending orders
        int pendingCount = orderDAO.countByStatus("PENDING");

        // Set attributes
        request.setAttribute("orders", paginatedOrders);
        request.setAttribute("statusFilter", status != null ? status : "All");
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("pendingCount", pendingCount);

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

        request.getRequestDispatcher("/view/dashboard/manager/order/order-approval-list.jsp").forward(request, response);
    }

    /**
     * Hiển thị chi tiết order (JSON cho modal)
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Map<String, Object> result = new HashMap<>();

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            result.put("success", false);
            result.put("message", "Order ID is required");
            out.print(gson.toJson(result));
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Order order = orderDAO.findById(id);

            if (order != null) {
                result.put("success", true);
                result.put("order", order);

                // Check machine availability for each item
                List<Map<String, Object>> itemsWithAvailability = new ArrayList<>();
                for (OrderItem item : order.getItems()) {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("item", item);

                    MachineAsset asset = machineAssetDAO.findById(item.getAssetId());
                    if (asset != null) {
                        boolean isAvailable = "ACTIVE".equals(asset.getStatus()) &&
                                "AVAILABLE".equals(asset.getRentalStatus());
                        itemMap.put("isAvailable", isAvailable);
                        itemMap.put("currentStatus", asset.getStatus());
                        itemMap.put("currentRentalStatus", asset.getRentalStatus());
                    } else {
                        itemMap.put("isAvailable", false);
                        itemMap.put("currentStatus", "NOT_FOUND");
                        itemMap.put("currentRentalStatus", "NOT_FOUND");
                    }

                    itemsWithAvailability.add(itemMap);
                }
                result.put("itemsWithAvailability", itemsWithAvailability);
            } else {
                result.put("success", false);
                result.put("message", "Order not found");
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid order ID");
        }

        out.print(gson.toJson(result));
    }

    /**
     * Duyệt order và chuyển thành contract
     */
    private void handleApprove(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/authen/login");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Order ID is required");
            response.sendRedirect(request.getContextPath() + "/manager/orders");
            return;
        }

        try {
            int orderId = Integer.parseInt(idStr);
            Order order = orderDAO.findById(orderId);

            if (order == null) {
                request.getSession().setAttribute("errorMsg", "Không tìm thấy đơn hàng");
                response.sendRedirect(request.getContextPath() + "/manager/orders");
                return;
            }

            if (!"PENDING".equals(order.getStatus())) {
                request.getSession().setAttribute("errorMsg", "Đơn hàng không ở trạng thái chờ duyệt");
                response.sendRedirect(request.getContextPath() + "/manager/orders");
                return;
            }

            // Check all machines are still available
            List<OrderItem> items = order.getItems();
            List<String> unavailableMachines = new ArrayList<>();

            for (OrderItem item : items) {
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
                        "Không thể duyệt. Các máy sau không còn sẵn sàng: " + String.join(", ", unavailableMachines));
                response.sendRedirect(request.getContextPath() + "/manager/orders");
                return;
            }

            // Create contract from order
            Connection conn = null;
            try {
                conn = new com.example.argomachinemanagement.dal.DBContext().getConnection();
                conn.setAutoCommit(false);

                // 1. Generate contract code
                String contractCode = generateContractCode(conn);

                // 2. Create contract (lưu sale_id từ order)
                String insertContractSql = "INSERT INTO contracts (contract_code, customer_id, manager_id, sale_id, start_date, end_date, status, note) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE', ?)";
                PreparedStatement psContract = conn.prepareStatement(insertContractSql, Statement.RETURN_GENERATED_KEYS);
                psContract.setString(1, contractCode);
                psContract.setInt(2, order.getCustomerId());
                psContract.setInt(3, currentUser.getId());
                // Lưu sale_id từ order (nếu có)
                if (order.getSaleId() != null) {
                    psContract.setInt(4, order.getSaleId());
                } else {
                    psContract.setNull(4, Types.INTEGER);
                }
                psContract.setDate(5, order.getStartDate());
                psContract.setDate(6, order.getEndDate());
                psContract.setString(7, "Tạo từ đơn hàng " + order.getOrderCode() + ". " +
                        (order.getNote() != null ? order.getNote() : ""));
                psContract.executeUpdate();

                ResultSet rsContract = psContract.getGeneratedKeys();
                int contractId = 0;
                if (rsContract.next()) {
                    contractId = rsContract.getInt(1);
                }
                rsContract.close();
                psContract.close();

                // 3. Copy order_items to contract_items and update rental_status
                String insertItemSql = "INSERT INTO contract_items (contract_id, asset_id, price, note) VALUES (?, ?, ?, ?)";
                String updateAssetSql = "UPDATE machine_assets SET rental_status = 'RENTED' WHERE id = ?";

                for (OrderItem item : items) {
                    // Insert contract item
                    PreparedStatement psItem = conn.prepareStatement(insertItemSql);
                    psItem.setInt(1, contractId);
                    psItem.setInt(2, item.getAssetId());
                    psItem.setBigDecimal(3, item.getPrice());
                    psItem.setString(4, item.getNote());
                    psItem.executeUpdate();
                    psItem.close();

                    // Update asset rental status
                    PreparedStatement psAsset = conn.prepareStatement(updateAssetSql);
                    psAsset.setInt(1, item.getAssetId());
                    psAsset.executeUpdate();
                    psAsset.close();
                }

                // 4. Update order status to CONVERTED
                String updateOrderSql = "UPDATE orders SET status = 'CONVERTED', manager_id = ?, contract_id = ?, approved_at = CURRENT_TIMESTAMP WHERE id = ?";
                PreparedStatement psOrder = conn.prepareStatement(updateOrderSql);
                psOrder.setInt(1, currentUser.getId());
                psOrder.setInt(2, contractId);
                psOrder.setInt(3, orderId);
                psOrder.executeUpdate();
                psOrder.close();

                conn.commit();

                // Auto-cancel conflicting orders (orders khác có chứa cùng máy)
                String successMsg = "Đã duyệt đơn hàng và tạo hợp đồng " + contractCode + " thành công";

                List<Integer> assetIds = items.stream()
                        .map(OrderItem::getAssetId)
                        .collect(Collectors.toList());

                List<Integer> conflictingOrderIds = orderDAO.findPendingOrdersContainingAssets(assetIds, orderId);

                if (!conflictingOrderIds.isEmpty()) {
                    String rejectReason = "Máy trong đơn hàng này đã được thuê trong đơn hàng " + order.getOrderCode();
                    int cancelledCount = orderDAO.rejectMultiple(conflictingOrderIds, currentUser.getId(), rejectReason);

                    if (cancelledCount > 0) {
                        successMsg += " (" + cancelledCount + " đơn hàng khác đã bị hủy do trùng máy)";
                    }
                }

                request.getSession().setAttribute("successMsg", successMsg);

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

        response.sendRedirect(request.getContextPath() + "/manager/orders");
    }

    /**
     * Generate contract code: CT-YYYY-NNN
     */
    private String generateContractCode(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(contract_code, 9) AS UNSIGNED)) as max_num " +
                "FROM contracts WHERE contract_code LIKE CONCAT('CT-', YEAR(CURRENT_DATE), '-%')";
        int nextNum = 1;

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Integer maxNum = rs.getInt("max_num");
            if (!rs.wasNull()) {
                nextNum = maxNum + 1;
            }
        }
        rs.close();
        ps.close();

        java.util.Calendar cal = java.util.Calendar.getInstance();
        int year = cal.get(java.util.Calendar.YEAR);
        return String.format("CT-%d-%03d", year, nextNum);
    }

    /**
     * Từ chối order
     */
    private void handleReject(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/authen/login");
            return;
        }

        String idStr = request.getParameter("id");
        String rejectReason = request.getParameter("rejectReason");

        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Order ID is required");
            response.sendRedirect(request.getContextPath() + "/manager/orders");
            return;
        }

        if (rejectReason == null || rejectReason.trim().isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Vui lòng nhập lý do từ chối");
            response.sendRedirect(request.getContextPath() + "/manager/orders");
            return;
        }

        try {
            int orderId = Integer.parseInt(idStr);
            Order order = orderDAO.findById(orderId);

            if (order == null) {
                request.getSession().setAttribute("errorMsg", "Không tìm thấy đơn hàng");
                response.sendRedirect(request.getContextPath() + "/manager/orders");
                return;
            }

            if (!"PENDING".equals(order.getStatus())) {
                request.getSession().setAttribute("errorMsg", "Đơn hàng không ở trạng thái chờ duyệt");
                response.sendRedirect(request.getContextPath() + "/manager/orders");
                return;
            }

            boolean success = orderDAO.reject(orderId, currentUser.getId(), rejectReason.trim());

            if (success) {
                request.getSession().setAttribute("successMsg", "Đã từ chối đơn hàng");
            } else {
                request.getSession().setAttribute("errorMsg", "Từ chối đơn hàng thất bại");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/orders");
    }

    @Override
    public String getServletInfo() {
        return "Order Approval Controller - Manager approves/rejects orders from Sales";
    }
}
