package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.ContractItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractItemDAO extends DBContext {
    
    /**
     * Lấy danh sách machines trong một contract
     */
    public List<ContractItem> findByContractId(int contractId) {
        List<ContractItem> items = new ArrayList<>();
        String sql = "SELECT ci.*, m.machine_code, m.machine_name, m.status AS machine_status " +
                     "FROM contract_items ci " +
                     "LEFT JOIN machines m ON ci.machine_id = m.id " +
                     "WHERE ci.contract_id = ? " +
                     "ORDER BY ci.id";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, contractId);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                ContractItem item = getFromResultSet(resultSet);
                items.add(item);
            }
        } catch (SQLException ex) {
            System.out.println("Error in ContractItemDAO.findByContractId: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return items;
    }
    
    /**
     * Thêm machine vào contract
     */
    public int insert(ContractItem item) {
        int itemId = 0;
        String sql = "INSERT INTO contract_items (contract_id, machine_id, machine_name_snapshot, note) " +
                     "VALUES (?, ?, ?, ?)";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, item.getContractId());
            statement.setInt(2, item.getMachineId());
            statement.setString(3, item.getMachineNameSnapshot());
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
            System.out.println("Error in ContractItemDAO.insert: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return itemId;
    }
    
    /**
     * Xóa machine khỏi contract
     */
    public boolean delete(int itemId) {
        boolean success = false;
        String sql = "DELETE FROM contract_items WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, itemId);
            
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in ContractItemDAO.delete: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    /**
     * Kiểm tra machine đã được thêm vào contract chưa
     */
    public boolean exists(int contractId, int machineId) {
        boolean exists = false;
        String sql = "SELECT COUNT(*) FROM contract_items WHERE contract_id = ? AND machine_id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, contractId);
            statement.setInt(2, machineId);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error in ContractItemDAO.exists: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return exists;
    }
    
    private ContractItem getFromResultSet(ResultSet rs) throws SQLException {
        ContractItem item = ContractItem.builder()
                .id(rs.getInt("id"))
                .contractId(rs.getInt("contract_id"))
                .machineId(rs.getInt("machine_id"))
                .machineNameSnapshot(rs.getString("machine_name_snapshot"))
                .note(rs.getString("note"))
                .build();
        
        // Set thông tin từ bảng machines
        try {
            item.setMachineCode(rs.getString("machine_code"));
            item.setMachineName(rs.getString("machine_name"));
            item.setMachineStatus(rs.getString("machine_status"));
        } catch (SQLException e) {
            // Ignore if columns don't exist
        }
        
        return item;
    }
}
