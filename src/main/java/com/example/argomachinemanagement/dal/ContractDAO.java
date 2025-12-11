package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.Contract;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContractDAO extends DBContext implements I_DAO<Contract> {
    
    @Override
    public List<Contract> findAll() {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT c.*, " +
                     "cu.full_name AS customer_name, " +
                     "m.full_name AS manager_name " +
                     "FROM contracts c " +
                     "LEFT JOIN users cu ON c.customer_id = cu.id " +
                     "LEFT JOIN users m ON c.manager_id = m.id " +
                     "ORDER BY c.created_at DESC";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Contract contract = getFromResultSet(resultSet);
                contracts.add(contract);
            }
        } catch (SQLException ex) {
            System.out.println("Error in findAll: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return contracts;
    }
    
    @Override
    public Contract findById(Integer id) {
        Contract contract = null;
        String sql = "SELECT c.*, " +
                     "cu.full_name AS customer_name, " +
                     "m.full_name AS manager_name " +
                     "FROM contracts c " +
                     "LEFT JOIN users cu ON c.customer_id = cu.id " +
                     "LEFT JOIN users m ON c.manager_id = m.id " +
                     "WHERE c.id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                contract = getFromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            System.out.println("Error in findById: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return contract;
    }
    
    @Override
    public int insert(Contract contract) {
        int contractId = 0;
        String sql = "INSERT INTO contracts (contract_code, customer_id, manager_id, " +
                     "start_date, end_date, status, note) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, contract.getContractCode());
            statement.setInt(2, contract.getCustomerId());
            statement.setInt(3, contract.getManagerId());
            statement.setDate(4, contract.getStartDate());
            statement.setDate(5, contract.getEndDate());
            statement.setString(6, contract.getStatus() != null ? contract.getStatus() : "DRAFT");
            statement.setString(7, contract.getNote());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    contractId = generatedKeys.getInt(1);
                    generatedKeys.close();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in insert: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return contractId;
    }
    
    @Override
    public boolean update(Contract contract) {
        boolean success = false;
        String sql = "UPDATE contracts SET customer_id = ?, manager_id = ?, " +
                     "start_date = ?, end_date = ?, status = ?, note = ? " +
                     "WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, contract.getCustomerId());
            statement.setInt(2, contract.getManagerId());
            statement.setDate(3, contract.getStartDate());
            statement.setDate(4, contract.getEndDate());
            statement.setString(5, contract.getStatus());
            statement.setString(6, contract.getNote());
            statement.setInt(7, contract.getId());
            
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in update: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    @Override
    public boolean delete(Contract contract) {
        // Không cho phép xóa contract, chỉ có thể cancel
        return false;
    }
    
    @Override
    public Map<Integer, Contract> findAllMap() {
        return Map.of();
    }
    
    @Override
    public Contract getFromResultSet(ResultSet rs) throws SQLException {
        Contract contract = Contract.builder()
                .id(rs.getInt("id"))
                .contractCode(rs.getString("contract_code"))
                .customerId(rs.getInt("customer_id"))
                .managerId(rs.getInt("manager_id"))
                .startDate(rs.getDate("start_date"))
                .endDate(rs.getDate("end_date"))
                .status(rs.getString("status"))
                .note(rs.getString("note"))
                .createdAt(rs.getTimestamp("created_at"))
                .updatedAt(rs.getTimestamp("updated_at"))
                .build();
        
        // Set display names
        try {
            contract.setCustomerName(rs.getString("customer_name"));
            contract.setManagerName(rs.getString("manager_name"));
        } catch (SQLException e) {
            // Ignore if columns don't exist
        }
        
        return contract;
    }
    
    /**
     * Find contracts with filters (for C1 - Contract List)
     */
    public List<Contract> findByFilters(String status, String keyword, Integer customerId, Integer managerId) {
        List<Contract> contracts = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT c.*, " +
            "cu.full_name AS customer_name, " +
            "m.full_name AS manager_name " +
            "FROM contracts c " +
            "LEFT JOIN users cu ON c.customer_id = cu.id " +
            "LEFT JOIN users m ON c.manager_id = m.id " +
            "WHERE 1=1"
        );
        
        List<Object> params = new ArrayList<>();
        
        if (status != null && !status.isEmpty() && !status.equals("All Status")) {
            sql.append(" AND c.status = ?");
            params.add(status);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (c.contract_code LIKE ? OR cu.full_name LIKE ? OR m.full_name LIKE ?)");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (customerId != null) {
            sql.append(" AND c.customer_id = ?");
            params.add(customerId);
        }
        
        if (managerId != null) {
            sql.append(" AND c.manager_id = ?");
            params.add(managerId);
        }
        
        sql.append(" ORDER BY c.created_at DESC");
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql.toString());
            
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Contract contract = getFromResultSet(resultSet);
                contracts.add(contract);
            }
        } catch (SQLException ex) {
            System.out.println("Error in findByFilters: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return contracts;
    }
    
    /**
     * Generate unique contract code
     * Format: CT-YYYYMMDD-XXX (VD: CT-20250115-001)
     */
    public String generateContractCode() {
        String code = null;
        String sql = "SELECT CONCAT('CT-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', " +
                     "LPAD(COALESCE(MAX(CAST(SUBSTRING(contract_code, -3) AS UNSIGNED)), 0) + 1, 3, '0')) AS new_code " +
                     "FROM contracts " +
                     "WHERE contract_code LIKE CONCAT('CT-', DATE_FORMAT(NOW(), '%Y%m%d'), '-%')";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                code = resultSet.getString("new_code");
            } else {
                // First contract of the day
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
                code = "CT-" + sdf.format(new java.util.Date()) + "-001";
            }
        } catch (SQLException ex) {
            System.out.println("Error in generateContractCode: " + ex.getMessage());
            // Fallback
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
            code = "CT-" + sdf.format(new java.util.Date()) + "-001";
        } finally {
            closeResources();
        }
        
        return code;
    }
}

