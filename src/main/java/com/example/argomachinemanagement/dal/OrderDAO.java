package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.Order;
import com.example.argomachinemanagement.entity.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO for orders table
 * Đơn hàng do Sale tạo, cần Manager duyệt
 */
public class OrderDAO extends DBContext implements I_DAO<Order> {

    private static final String BASE_SELECT =
            "SELECT o.*, " +
                    "c.full_name as customer_name, c.phone_number as customer_phone, c.email as customer_email, " +
                    "s.full_name as sale_name, " +
                    "m.full_name as manager_name, " +
                    "ct.contract_code " +
                    "FROM orders o " +
                    "LEFT JOIN users c ON o.customer_id = c.id " +
                    "LEFT JOIN users s ON o.sale_id = s.id " +
                    "LEFT JOIN users m ON o.manager_id = m.id " +
                    "LEFT JOIN contracts ct ON o.contract_id = ct.id ";

    private OrderItemDAO orderItemDAO = new OrderItemDAO();

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY o.created_at DESC";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                orders.add(getFromResultSet(resultSet));
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.findAll: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return orders;
    }

    @Override
    public Order findById(Integer id) {
        Order order = null;
        String sql = BASE_SELECT + "WHERE o.id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                order = getFromResultSet(resultSet);
                // Load items
                order.setItems(orderItemDAO.findByOrderId(id));
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.findById: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return order;
    }

    /**
     * Tìm orders theo Sale ID
     */
    public List<Order> findBySaleId(Integer saleId) {
        List<Order> orders = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE o.sale_id = ? ORDER BY o.created_at DESC";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, saleId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                orders.add(getFromResultSet(resultSet));
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.findBySaleId: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return orders;
    }

    /**
     * Tìm orders chờ duyệt (PENDING)
     */
    public List<Order> findPendingOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE o.status = 'PENDING' ORDER BY o.created_at ASC";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                orders.add(getFromResultSet(resultSet));
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.findPendingOrders: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return orders;
    }

    /**
     * Tìm orders với bộ lọc
     */
    public List<Order> findByFilters(Integer saleId, String status, String keyword) {
        List<Order> orders = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT + "WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (saleId != null && saleId > 0) {
            sql.append(" AND o.sale_id = ?");
            params.add(saleId);
        }

        if (status != null && !status.trim().isEmpty() && !status.equals("All")) {
            sql.append(" AND o.status = ?");
            params.add(status.trim());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (o.order_code LIKE ? OR c.full_name LIKE ? OR c.phone_number LIKE ?)");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        sql.append(" ORDER BY o.created_at DESC");

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                orders.add(getFromResultSet(resultSet));
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.findByFilters: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return orders;
    }

    /**
     * Tìm orders cho Manager duyệt (tất cả hoặc filter theo status)
     */
    public List<Order> findForManager(String status, String keyword) {
        List<Order> orders = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT + "WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty() && !status.equals("All")) {
            sql.append(" AND o.status = ?");
            params.add(status.trim());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (o.order_code LIKE ? OR c.full_name LIKE ? OR s.full_name LIKE ?)");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        sql.append(" ORDER BY CASE WHEN o.status = 'PENDING' THEN 0 ELSE 1 END, o.created_at DESC");

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                orders.add(getFromResultSet(resultSet));
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.findForManager: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return orders;
    }

    @Override
    public int insert(Order order) {
        int orderId = 0;
        String sql = "INSERT INTO orders (order_code, customer_id, sale_id, start_date, end_date, status, note) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, order.getOrderCode());
            statement.setInt(2, order.getCustomerId());
            statement.setInt(3, order.getSaleId());
            statement.setDate(4, order.getStartDate());
            statement.setDate(5, order.getEndDate());
            statement.setString(6, order.getStatus() != null ? order.getStatus() : "PENDING");
            statement.setString(7, order.getNote());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                    generatedKeys.close();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.insert: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return orderId;
    }

    @Override
    public boolean update(Order order) {
        boolean success = false;
        String sql = "UPDATE orders SET customer_id = ?, start_date = ?, end_date = ?, note = ? WHERE id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, order.getCustomerId());
            statement.setDate(2, order.getStartDate());
            statement.setDate(3, order.getEndDate());
            statement.setString(4, order.getNote());
            statement.setInt(5, order.getId());

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.update: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    /**
     * Duyệt đơn hàng
     */
    public boolean approve(Integer orderId, Integer managerId) {
        boolean success = false;
        String sql = "UPDATE orders SET status = 'APPROVED', manager_id = ?, approved_at = CURRENT_TIMESTAMP WHERE id = ? AND status = 'PENDING'";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, managerId);
            statement.setInt(2, orderId);

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.approve: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    /**
     * Từ chối đơn hàng
     */
    public boolean reject(Integer orderId, Integer managerId, String reason) {
        boolean success = false;
        String sql = "UPDATE orders SET status = 'REJECTED', manager_id = ?, reject_reason = ?, approved_at = CURRENT_TIMESTAMP WHERE id = ? AND status = 'PENDING'";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, managerId);
            statement.setString(2, reason);
            statement.setInt(3, orderId);

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.reject: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    /**
     * Đánh dấu order đã chuyển thành contract
     */
    public boolean markAsConverted(Integer orderId, Integer contractId) {
        boolean success = false;
        String sql = "UPDATE orders SET status = 'CONVERTED', contract_id = ? WHERE id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, contractId);
            statement.setInt(2, orderId);

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.markAsConverted: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    /**
     * Hủy đơn hàng (chỉ PENDING mới hủy được)
     */
    public boolean cancel(Integer orderId) {
        boolean success = false;
        String sql = "UPDATE orders SET status = 'REJECTED', reject_reason = 'Đã hủy bởi Sale' WHERE id = ? AND status = 'PENDING'";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, orderId);

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.cancel: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    @Override
    public boolean delete(Order order) {
        // Orders thường không xóa, chỉ cancel
        return cancel(order.getId());
    }

    /**
     * Kiểm tra order_code đã tồn tại chưa
     */
    public boolean isOrderCodeExists(String orderCode) {
        boolean exists = false;
        String sql = "SELECT COUNT(*) FROM orders WHERE order_code = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, orderCode);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.isOrderCodeExists: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return exists;
    }

    /**
     * Tạo order code tự động: ORD-YYYY-NNN
     */
    public String generateOrderCode() {
        String sql = "SELECT MAX(CAST(SUBSTRING(order_code, 10) AS UNSIGNED)) as max_num " +
                "FROM orders WHERE order_code LIKE CONCAT('ORD-', YEAR(CURRENT_DATE), '-%')";
        int nextNum = 1;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Integer maxNum = resultSet.getInt("max_num");
                if (!resultSet.wasNull()) {
                    nextNum = maxNum + 1;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.generateOrderCode: " + ex.getMessage());
        } finally {
            closeResources();
        }

        java.util.Calendar cal = java.util.Calendar.getInstance();
        int year = cal.get(java.util.Calendar.YEAR);
        return String.format("ORD-%d-%03d", year, nextNum);
    }

    /**
     * Đếm số orders theo status
     */
    public int countByStatus(String status) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM orders WHERE status = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, status);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.countByStatus: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return count;
    }

    /**
     * Đếm số orders PENDING của một sale
     */
    public int countPendingBySale(Integer saleId) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM orders WHERE sale_id = ? AND status = 'PENDING'";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, saleId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderDAO.countPendingBySale: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return count;
    }

    @Override
    public Map<Integer, Order> findAllMap() {
        return Map.of();
    }

    @Override
    public Order getFromResultSet(ResultSet rs) throws SQLException {
        Order order = Order.builder()
                .id(rs.getInt("id"))
                .orderCode(rs.getString("order_code"))
                .customerId(rs.getInt("customer_id"))
                .saleId(rs.getInt("sale_id"))
                .startDate(rs.getDate("start_date"))
                .endDate(rs.getDate("end_date"))
                .status(rs.getString("status"))
                .rejectReason(rs.getString("reject_reason"))
                .note(rs.getString("note"))
                .createdAt(rs.getTimestamp("created_at"))
                .approvedAt(rs.getTimestamp("approved_at"))
                .build();

        // Manager ID có thể null
        int managerId = rs.getInt("manager_id");
        if (!rs.wasNull()) {
            order.setManagerId(managerId);
        }

        // Contract ID có thể null
        int contractId = rs.getInt("contract_id");
        if (!rs.wasNull()) {
            order.setContractId(contractId);
        }

        // Display fields từ JOIN
        try {
            order.setCustomerName(rs.getString("customer_name"));
            order.setCustomerPhone(rs.getString("customer_phone"));
            order.setCustomerEmail(rs.getString("customer_email"));
            order.setSaleName(rs.getString("sale_name"));
            order.setManagerName(rs.getString("manager_name"));
            order.setContractCode(rs.getString("contract_code"));
        } catch (SQLException e) {
            // Columns might not exist in some queries
        }

        return order;
    }
}
