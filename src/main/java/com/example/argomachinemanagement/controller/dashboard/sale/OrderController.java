package com.example.argomachinemanagement.controller.dashboard.sale;

import com.example.argomachinemanagement.dal.MachineAssetDAO;
import com.example.argomachinemanagement.dal.OrderDAO;
import com.example.argomachinemanagement.dal.OrderItemDAO;
import com.example.argomachinemanagement.dal.UserDAO;
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
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller quản lý Orders cho Sale
 * URL: /sale/orders
 */
@WebServlet(name = "SaleOrderController", urlPatterns = {"/sale/orders"})
public class OrderController extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private MachineAssetDAO machineAssetDAO;
    private UserDAO userDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
        orderItemDAO = new OrderItemDAO();
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
        } else if (action.equals("create")) {
            handleShowCreateForm(request, response);
        } else if (action.equals("detail")) {
            handleDetail(request, response);
        } else if (action.equals("edit")) {
            handleShowEditForm(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/sale/orders");
            return;
        }

        switch (action) {
            case "create":
                handleCreate(request, response);
                break;
            case "update":
                handleUpdate(request, response);
                break;
            case "cancel":
                handleCancel(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/sale/orders");
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
     * Hiển thị danh sách orders của Sale
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

        // Get orders for this sale
        List<Order> allOrders = orderDAO.findByFilters(currentUser.getId(), status, keyword);

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

        // Load items count for each order
        for (Order order : paginatedOrders) {
            List<OrderItem> items = orderItemDAO.findByOrderId(order.getId());
            order.setItems(items);
        }

        // Set attributes
        request.setAttribute("orders", paginatedOrders);
        request.setAttribute("statusFilter", status != null ? status : "All");
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalOrders", totalOrders);

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

        request.getRequestDispatcher("/view/dashboard/sale/order-list.jsp").forward(request, response);
    }

    /**
     * Hiển thị form tạo order mới
     */
    private void handleShowCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get available assets for selection
        List<MachineAsset> availableAssets = machineAssetDAO.findAvailableAssets();
        request.setAttribute("availableAssets", availableAssets);

        // Get customers
        List<User> customers = userDAO.findByRole("customer");
        request.setAttribute("customers", customers);

        // Generate order code
        String orderCode = orderDAO.generateOrderCode();
        request.setAttribute("orderCode", orderCode);

        request.getRequestDispatcher("/view/dashboard/sale/order-create.jsp").forward(request, response);
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
                // Check if this order belongs to current sale
                User currentUser = getCurrentUser(request);
                if (currentUser != null && order.getSaleId().equals(currentUser.getId())) {
                    result.put("success", true);
                    result.put("order", order);
                } else {
                    result.put("success", false);
                    result.put("message", "Không có quyền xem đơn hàng này");
                }
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
     * Hiển thị form sửa order (chỉ PENDING)
     */
    private void handleShowEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Order ID is required");
            response.sendRedirect(request.getContextPath() + "/sale/orders");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Order order = orderDAO.findById(id);

            if (order == null) {
                request.getSession().setAttribute("errorMsg", "Không tìm thấy đơn hàng");
                response.sendRedirect(request.getContextPath() + "/sale/orders");
                return;
            }

            // Check ownership
            User currentUser = getCurrentUser(request);
            if (currentUser == null || !order.getSaleId().equals(currentUser.getId())) {
                request.getSession().setAttribute("errorMsg", "Không có quyền sửa đơn hàng này");
                response.sendRedirect(request.getContextPath() + "/sale/orders");
                return;
            }

            // Check status
            if (!"PENDING".equals(order.getStatus())) {
                request.getSession().setAttribute("errorMsg", "Chỉ có thể sửa đơn hàng đang chờ duyệt");
                response.sendRedirect(request.getContextPath() + "/sale/orders");
                return;
            }

            // Get available assets + current order's assets
            List<MachineAsset> availableAssets = machineAssetDAO.findAvailableAssets();
            request.setAttribute("availableAssets", availableAssets);

            // Get customers
            List<User> customers = userDAO.findByRole("customer");
            request.setAttribute("customers", customers);

            request.setAttribute("order", order);
            request.getRequestDispatcher("/view/dashboard/sale/order-create.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMsg", "Invalid order ID");
            response.sendRedirect(request.getContextPath() + "/sale/orders");
        }
    }

    /**
     * Tạo order mới
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/authen/login");
            return;
        }

        String orderCode = request.getParameter("orderCode");
        String customerIdStr = request.getParameter("customerId");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String note = request.getParameter("note");
        String[] assetIds = request.getParameterValues("assetIds");
        String[] prices = request.getParameterValues("prices");
        String[] itemNotes = request.getParameterValues("itemNotes");

        // Validation
        if (orderCode == null || orderCode.trim().isEmpty() ||
            customerIdStr == null || customerIdStr.isEmpty() ||
            startDateStr == null || startDateStr.isEmpty() ||
            assetIds == null || assetIds.length == 0) {
            request.getSession().setAttribute("errorMsg", "Vui lòng điền đầy đủ thông tin bắt buộc và chọn ít nhất 1 máy");
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=create");
            return;
        }

        // Check order code unique
        if (orderDAO.isOrderCodeExists(orderCode.trim())) {
            request.getSession().setAttribute("errorMsg", "Mã đơn hàng đã tồn tại");
            response.sendRedirect(request.getContextPath() + "/sale/orders?action=create");
            return;
        }

        try {
            // Create order
            Order order = Order.builder()
                    .orderCode(orderCode.trim())
                    .customerId(Integer.parseInt(customerIdStr))
                    .saleId(currentUser.getId())
                    .startDate(Date.valueOf(startDateStr))
                    .endDate(endDateStr != null && !endDateStr.isEmpty() ? Date.valueOf(endDateStr) : null)
                    .status("PENDING")
                    .note(note != null ? note.trim() : null)
                    .build();

            int orderId = orderDAO.insert(order);

            if (orderId > 0) {
                // Insert order items
                List<OrderItem> items = new ArrayList<>();
                for (int i = 0; i < assetIds.length; i++) {
                    if (assetIds[i] != null && !assetIds[i].isEmpty()) {
                        OrderItem item = OrderItem.builder()
                                .orderId(orderId)
                                .assetId(Integer.parseInt(assetIds[i]))
                                .price(prices != null && i < prices.length && prices[i] != null && !prices[i].isEmpty()
                                        ? new BigDecimal(prices[i]) : null)
                                .note(itemNotes != null && i < itemNotes.length ? itemNotes[i] : null)
                                .build();
                        items.add(item);
                    }
                }

                if (!items.isEmpty()) {
                    orderItemDAO.insertBatch(items);
                }

                request.getSession().setAttribute("successMsg", "Tạo đơn hàng thành công. Đang chờ Manager duyệt.");
            } else {
                request.getSession().setAttribute("errorMsg", "Tạo đơn hàng thất bại");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/sale/orders");
    }

    /**
     * Cập nhật order (chỉ PENDING)
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/authen/login");
            return;
        }

        String idStr = request.getParameter("id");
        String customerIdStr = request.getParameter("customerId");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String note = request.getParameter("note");
        String[] assetIds = request.getParameterValues("assetIds");
        String[] prices = request.getParameterValues("prices");
        String[] itemNotes = request.getParameterValues("itemNotes");

        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Order ID is required");
            response.sendRedirect(request.getContextPath() + "/sale/orders");
            return;
        }

        try {
            int orderId = Integer.parseInt(idStr);
            Order existingOrder = orderDAO.findById(orderId);

            if (existingOrder == null) {
                request.getSession().setAttribute("errorMsg", "Không tìm thấy đơn hàng");
                response.sendRedirect(request.getContextPath() + "/sale/orders");
                return;
            }

            // Check ownership and status
            if (!existingOrder.getSaleId().equals(currentUser.getId())) {
                request.getSession().setAttribute("errorMsg", "Không có quyền sửa đơn hàng này");
                response.sendRedirect(request.getContextPath() + "/sale/orders");
                return;
            }

            if (!"PENDING".equals(existingOrder.getStatus())) {
                request.getSession().setAttribute("errorMsg", "Chỉ có thể sửa đơn hàng đang chờ duyệt");
                response.sendRedirect(request.getContextPath() + "/sale/orders");
                return;
            }

            // Update order
            Order order = Order.builder()
                    .id(orderId)
                    .customerId(Integer.parseInt(customerIdStr))
                    .startDate(Date.valueOf(startDateStr))
                    .endDate(endDateStr != null && !endDateStr.isEmpty() ? Date.valueOf(endDateStr) : null)
                    .note(note != null ? note.trim() : null)
                    .build();

            boolean success = orderDAO.update(order);

            if (success) {
                // Delete old items and insert new ones
                orderItemDAO.deleteByOrderId(orderId);

                if (assetIds != null && assetIds.length > 0) {
                    List<OrderItem> items = new ArrayList<>();
                    for (int i = 0; i < assetIds.length; i++) {
                        if (assetIds[i] != null && !assetIds[i].isEmpty()) {
                            OrderItem item = OrderItem.builder()
                                    .orderId(orderId)
                                    .assetId(Integer.parseInt(assetIds[i]))
                                    .price(prices != null && i < prices.length && prices[i] != null && !prices[i].isEmpty()
                                            ? new BigDecimal(prices[i]) : null)
                                    .note(itemNotes != null && i < itemNotes.length ? itemNotes[i] : null)
                                    .build();
                            items.add(item);
                        }
                    }

                    if (!items.isEmpty()) {
                        orderItemDAO.insertBatch(items);
                    }
                }

                request.getSession().setAttribute("successMsg", "Cập nhật đơn hàng thành công");
            } else {
                request.getSession().setAttribute("errorMsg", "Cập nhật đơn hàng thất bại");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/sale/orders");
    }

    /**
     * Hủy order (chỉ PENDING)
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
            request.getSession().setAttribute("errorMsg", "Order ID is required");
            response.sendRedirect(request.getContextPath() + "/sale/orders");
            return;
        }

        try {
            int orderId = Integer.parseInt(idStr);
            Order order = orderDAO.findById(orderId);

            if (order == null) {
                request.getSession().setAttribute("errorMsg", "Không tìm thấy đơn hàng");
                response.sendRedirect(request.getContextPath() + "/sale/orders");
                return;
            }

            // Check ownership
            if (!order.getSaleId().equals(currentUser.getId())) {
                request.getSession().setAttribute("errorMsg", "Không có quyền hủy đơn hàng này");
                response.sendRedirect(request.getContextPath() + "/sale/orders");
                return;
            }

            // Check status
            if (!"PENDING".equals(order.getStatus())) {
                request.getSession().setAttribute("errorMsg", "Chỉ có thể hủy đơn hàng đang chờ duyệt");
                response.sendRedirect(request.getContextPath() + "/sale/orders");
                return;
            }

            boolean success = orderDAO.cancel(orderId);

            if (success) {
                request.getSession().setAttribute("successMsg", "Đã hủy đơn hàng");
            } else {
                request.getSession().setAttribute("errorMsg", "Hủy đơn hàng thất bại");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/sale/orders");
    }

    @Override
    public String getServletInfo() {
        return "Sale Order Controller - Manage orders for sale staff";
    }
}
