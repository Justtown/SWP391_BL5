package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends DBContext {

    public void create(Order o) {
        String sql = "INSERT INTO service_orders " +
                "(contract_code, customer_name, customer_phone, customer_address, " +
                "machine_id, quantity, service_description, start_date, end_date, status, total_cost, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.println("=== Creating Order ===");
            System.out.println("Contract Code: " + o.getContractCode());
            System.out.println("Customer: " + o.getCustomerName());
            System.out.println("Created By: " + o.getCreatedBy());
            System.out.println("Status: " + o.getStatus());

            ps.setString(1, o.getContractCode());
            ps.setString(2, o.getCustomerName());
            ps.setString(3, o.getCustomerPhone());
            ps.setString(4, o.getCustomerAddress());

            if (o.getMachineId() != null && o.getMachineId() > 0) {
                ps.setInt(5, o.getMachineId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            
            if (o.getQuantity() != null && o.getQuantity() > 0) {
                ps.setInt(6, o.getQuantity());
            } else {
                ps.setInt(6, 1);
            }

            ps.setString(7, o.getServiceDescription());
            ps.setDate(8, o.getStartDate());
            ps.setDate(9, o.getEndDate());
            ps.setString(10, o.getStatus());
            
            if (o.getTotalCost() != null && o.getTotalCost() > 0) {
                ps.setDouble(11, o.getTotalCost());
            } else {
                ps.setNull(11, java.sql.Types.DOUBLE);
            }
            
            if (o.getCreatedBy() != null) {
                ps.setInt(12, o.getCreatedBy());
            } else {
                ps.setNull(12, java.sql.Types.INTEGER);
            }

            int rows = ps.executeUpdate();
            System.out.println("✓ Order created successfully! Rows affected: " + rows);
            
        } catch (Exception e) {
            System.err.println("✗ ERROR creating order: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create order: " + e.getMessage(), e);
        }
    }

    public List<Order> findAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u1.full_name as created_by_name, u2.full_name as approved_by_name, " +
                     "mt.type_name as machine_type_name " +
                     "FROM service_orders o " +
                     "LEFT JOIN users u1 ON o.created_by = u1.id " +
                     "LEFT JOIN users u2 ON o.approved_by = u2.id " +
                     "LEFT JOIN machine_types mt ON o.machine_id = mt.id " +
                     "ORDER BY o.created_at DESC";
        
        System.out.println("=== findAll() ===");
        System.out.println("SQL: " + sql);
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            System.out.println("Connection: " + (con != null ? "OK" : "NULL"));
            System.out.println("Connection closed? " + (con != null ? con.isClosed() : "N/A"));
            
            ResultSet rs = ps.executeQuery();
            System.out.println("Query executed successfully");
            
            int count = 0;
            while (rs.next()) {
                Order o = mapResultSetToOrder(rs);
                list.add(o);
                count++;
                System.out.println("Row " + count + ": ID=" + o.getId() + ", Code=" + o.getContractCode());
            }
            
            System.out.println("✓ Total orders found: " + list.size());
            
        } catch (Exception e) {
            System.err.println("✗ ERROR in findAll(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Order> findBySaleUser(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u1.full_name as created_by_name, u2.full_name as approved_by_name, " +
                     "mt.type_name as machine_type_name " +
                     "FROM service_orders o " +
                     "LEFT JOIN users u1 ON o.created_by = u1.id " +
                     "LEFT JOIN users u2 ON o.approved_by = u2.id " +
                     "LEFT JOIN machine_types mt ON o.machine_id = mt.id " +
                     "WHERE o.created_by = ? " +
                     "ORDER BY o.created_at DESC";
        
        System.out.println("=== findBySaleUser ===");
        System.out.println("Querying orders for user ID: " + userId);
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            System.out.println("Executing SQL: " + sql);
            System.out.println("With parameter: " + userId);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Order o = mapResultSetToOrder(rs);
                list.add(o);
                System.out.println("Found order ID: " + o.getId() + ", Code: " + o.getContractCode());
            }
            
            System.out.println("✓ Total found: " + list.size() + " orders for user ID: " + userId);
            
        } catch (Exception e) {
            System.err.println("✗ Error in findBySaleUser for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }

    /**
     * Tìm kiếm orders theo mã hợp đồng, khách hàng và trạng thái
     */
    public List<Order> searchOrders(String contractCode, String customerName, String status, 
                                     String userRole, Integer userId) {
        List<Order> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT o.*, u1.full_name as created_by_name, u2.full_name as approved_by_name, " +
            "mt.type_name as machine_type_name " +
            "FROM service_orders o " +
            "LEFT JOIN users u1 ON o.created_by = u1.id " +
            "LEFT JOIN users u2 ON o.approved_by = u2.id " +
            "LEFT JOIN machine_types mt ON o.machine_id = mt.id " +
            "WHERE 1=1 "
        );
        
        List<Object> params = new ArrayList<>();
        
        // Thêm điều kiện tìm kiếm
        if (contractCode != null && !contractCode.trim().isEmpty()) {
            sql.append("AND o.contract_code LIKE ? ");
            params.add("%" + contractCode.trim() + "%");
        }
        
        if (customerName != null && !customerName.trim().isEmpty()) {
            sql.append("AND o.customer_name LIKE ? ");
            params.add("%" + customerName.trim() + "%");
        }
        
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND o.status = ? ");
            params.add(status.trim());
        }
        
        sql.append("ORDER BY o.created_at DESC");
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order o = mapResultSetToOrder(rs);
                list.add(o);
            }
            
        } catch (Exception e) {
            System.err.println("Error in searchOrders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }

    public Order findById(int id) {
        String sql = "SELECT o.*, u1.full_name as created_by_name, u2.full_name as approved_by_name, " +
                     "mt.type_name as machine_type_name " +
                     "FROM service_orders o " +
                     "LEFT JOIN users u1 ON o.created_by = u1.id " +
                     "LEFT JOIN users u2 ON o.approved_by = u2.id " +
                     "LEFT JOIN machine_types mt ON o.machine_id = mt.id " +
                     "WHERE o.id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Order o) {
        String sql = "UPDATE service_orders SET " +
                "contract_code = ?, customer_name = ?, customer_phone = ?, customer_address = ?, " +
                "machine_id = ?, quantity = ?, service_description = ?, start_date = ?, end_date = ?, " +
                "total_cost = ? " +
                "WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, o.getContractCode());
            ps.setString(2, o.getCustomerName());
            ps.setString(3, o.getCustomerPhone());
            ps.setString(4, o.getCustomerAddress());

            if (o.getMachineId() != null) {
                ps.setInt(5, o.getMachineId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            
            if (o.getQuantity() != null && o.getQuantity() > 0) {
                ps.setInt(6, o.getQuantity());
            } else {
                ps.setInt(6, 1);
            }

            ps.setString(7, o.getServiceDescription());
            ps.setDate(8, o.getStartDate());
            ps.setDate(9, o.getEndDate());
            
            if (o.getTotalCost() != null) {
                ps.setDouble(10, o.getTotalCost());
            } else {
                ps.setNull(10, java.sql.Types.DOUBLE);
            }
            
            ps.setInt(11, o.getId());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStatus(int orderId, String status) {
        String sql = "UPDATE service_orders SET status = ? WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Kiểm tra mã hợp đồng đã tồn tại chưa
     */
    public boolean isContractCodeExists(String contractCode, Integer excludeOrderId) {
        String sql = "SELECT COUNT(*) FROM service_orders WHERE contract_code = ?";
        if (excludeOrderId != null) {
            sql += " AND id != ?";
        }
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, contractCode);
            if (excludeOrderId != null) {
                ps.setInt(2, excludeOrderId);
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void approve(int orderId, int approvedBy) {
        String sql = "UPDATE service_orders SET status = 'APPROVED', approved_by = ? WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, approvedBy);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reject(int orderId, int approvedBy) {
        String sql = "UPDATE service_orders SET status = 'REJECTED', approved_by = ? WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, approvedBy);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int orderId) {
        String sql = "DELETE FROM service_orders WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws Exception {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setContractCode(rs.getString("contract_code"));
        o.setCustomerName(rs.getString("customer_name"));
        o.setCustomerPhone(rs.getString("customer_phone"));
        o.setCustomerAddress(rs.getString("customer_address"));
        o.setMachineId((Integer) rs.getObject("machine_id"));
        o.setQuantity((Integer) rs.getObject("quantity"));
        o.setServiceDescription(rs.getString("service_description"));
        o.setStartDate(rs.getDate("start_date"));
        o.setEndDate(rs.getDate("end_date"));
        o.setStatus(rs.getString("status"));
        
        // Convert BigDecimal to Double for total_cost
        Object totalCostObj = rs.getObject("total_cost");
        if (totalCostObj != null) {
            if (totalCostObj instanceof java.math.BigDecimal) {
                o.setTotalCost(((java.math.BigDecimal) totalCostObj).doubleValue());
            } else if (totalCostObj instanceof Double) {
                o.setTotalCost((Double) totalCostObj);
            }
        }
        
        o.setCreatedBy((Integer) rs.getObject("created_by"));
        o.setApprovedBy((Integer) rs.getObject("approved_by"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        o.setUpdatedAt(rs.getTimestamp("updated_at"));
        o.setCreatedByName(rs.getString("created_by_name"));
        o.setApprovedByName(rs.getString("approved_by_name"));
        o.setMachineTypeName(rs.getString("machine_type_name"));
        return o;
    }
}