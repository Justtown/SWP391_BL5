package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO for order_items table
 * Chi tiết máy trong đơn hàng
 */
public class OrderItemDAO extends DBContext implements I_DAO<OrderItem> {

    private static final String BASE_SELECT =
        "SELECT oi.*, a.serial_number, a.status as asset_status, a.rental_status as asset_rental_status, " +
        "m.model_code, m.model_name, m.brand, mt.type_name " +
        "FROM order_items oi " +
        "LEFT JOIN machine_assets a ON oi.asset_id = a.id " +
        "LEFT JOIN machine_models m ON a.model_id = m.id " +
        "LEFT JOIN machine_types mt ON m.type_id = mt.id ";

    @Override
    public List<OrderItem> findAll() {
        List<OrderItem> items = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY oi.id";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                items.add(getFromResultSet(resultSet));
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderItemDAO.findAll: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return items;
    }

    @Override
    public OrderItem findById(Integer id) {
        OrderItem item = null;
        String sql = BASE_SELECT + "WHERE oi.id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                item = getFromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderItemDAO.findById: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return item;
    }

    /**
     * Lấy tất cả items của một order
     */
    public List<OrderItem> findByOrderId(Integer orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE oi.order_id = ? ORDER BY oi.id";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, orderId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                items.add(getFromResultSet(resultSet));
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderItemDAO.findByOrderId: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return items;
    }

    @Override
    public int insert(OrderItem item) {
        int itemId = 0;
        String sql = "INSERT INTO order_items (order_id, asset_id, price, note) VALUES (?, ?, ?, ?)";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, item.getOrderId());
            statement.setInt(2, item.getAssetId());
            statement.setBigDecimal(3, item.getPrice());
            statement.setString(4, item.getNote());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    itemId = generatedKeys.getInt(1);
                    generatedKeys.close();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in OrderItemDAO.insert: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return itemId;
    }

    /**
     * Insert nhiều items cùng lúc
     */
    public boolean insertBatch(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return true;
        }

        String sql = "INSERT INTO order_items (order_id, asset_id, price, note) VALUES (?, ?, ?, ?)";

        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(sql);

            for (OrderItem item : items) {
                statement.setInt(1, item.getOrderId());
                statement.setInt(2, item.getAssetId());
                statement.setBigDecimal(3, item.getPrice());
                statement.setString(4, item.getNote());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
            return true;
        } catch (SQLException ex) {
            System.out.println("Error in OrderItemDAO.insertBatch: " + ex.getMessage());
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e) {
                System.out.println("Error rolling back: " + e.getMessage());
            }
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.out.println("Error resetting auto-commit: " + e.getMessage());
            }
            closeResources();
        }
    }

    @Override
    public boolean update(OrderItem item) {
        boolean success = false;
        String sql = "UPDATE order_items SET asset_id = ?, price = ?, note = ? WHERE id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, item.getAssetId());
            statement.setBigDecimal(2, item.getPrice());
            statement.setString(3, item.getNote());
            statement.setInt(4, item.getId());

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in OrderItemDAO.update: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    @Override
    public boolean delete(OrderItem item) {
        return deleteById(item.getId());
    }

    public boolean deleteById(Integer id) {
        boolean success = false;
        String sql = "DELETE FROM order_items WHERE id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in OrderItemDAO.deleteById: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    /**
     * Xóa tất cả items của một order
     */
    public boolean deleteByOrderId(Integer orderId) {
        boolean success = false;
        String sql = "DELETE FROM order_items WHERE order_id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, orderId);

            statement.executeUpdate();
            success = true; // Success even if no rows deleted
        } catch (SQLException ex) {
            System.out.println("Error in OrderItemDAO.deleteByOrderId: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    @Override
    public Map<Integer, OrderItem> findAllMap() {
        return Map.of();
    }

    @Override
    public OrderItem getFromResultSet(ResultSet rs) throws SQLException {
        OrderItem item = OrderItem.builder()
                .id(rs.getInt("id"))
                .orderId(rs.getInt("order_id"))
                .assetId(rs.getInt("asset_id"))
                .price(rs.getBigDecimal("price"))
                .note(rs.getString("note"))
                .build();

        // Display fields từ JOIN
        try {
            item.setSerialNumber(rs.getString("serial_number"));
            item.setModelCode(rs.getString("model_code"));
            item.setModelName(rs.getString("model_name"));
            item.setBrand(rs.getString("brand"));
            item.setTypeName(rs.getString("type_name"));
            item.setAssetStatus(rs.getString("asset_status"));
            item.setAssetRentalStatus(rs.getString("asset_rental_status"));
        } catch (SQLException e) {
            // Columns might not exist in some queries
        }

        return item;
    }
}
