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
        // Thử insert với các trường mới trước, nếu lỗi thì fallback về cấu trúc cũ
        String sql = "INSERT INTO contracts (contract_code, customer_id, manager_id, " +
                     "start_date, end_date, status, note, " +
                     "customer_name, customer_phone, customer_address, machine_type_id, quantity, total_cost) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
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
            // Các trường mới
            statement.setString(8, contract.getCustomerName());
            statement.setString(9, contract.getCustomerPhone());
            statement.setString(10, contract.getCustomerAddress());
            if (contract.getMachineTypeId() != null) {
                statement.setInt(11, contract.getMachineTypeId());
            } else {
                statement.setNull(11, java.sql.Types.INTEGER);
            }
            if (contract.getQuantity() != null) {
                statement.setInt(12, contract.getQuantity());
            } else {
                statement.setNull(12, java.sql.Types.INTEGER);
            }
            if (contract.getTotalCost() != null) {
                statement.setDouble(13, contract.getTotalCost());
            } else {
                statement.setNull(13, java.sql.Types.DOUBLE);
            }
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    contractId = generatedKeys.getInt(1);
                    generatedKeys.close();
                }
            }
        } catch (SQLException ex) {
            // Nếu lỗi do các cột mới chưa tồn tại, thử insert với cấu trúc cũ
            if (ex.getMessage().contains("Unknown column") || ex.getMessage().contains("doesn't exist")) {
                System.out.println("New columns not found, using old structure: " + ex.getMessage());
                try {
                    closeResources();
                    // Insert với cấu trúc cũ (không có các trường mới)
                    String sqlOld = "INSERT INTO contracts (contract_code, customer_id, manager_id, " +
                                 "start_date, end_date, status, note) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    connection = getConnection();
                    statement = connection.prepareStatement(sqlOld, Statement.RETURN_GENERATED_KEYS);
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
                } catch (SQLException ex2) {
                    System.out.println("Error in insert (fallback): " + ex2.getMessage());
                }
            } else {
                System.out.println("Error in insert: " + ex.getMessage());
            }
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
        
        // Set display names và các trường mới
        try {
            // Nếu có customer_name từ JOIN với users thì dùng, nếu không thì dùng từ cột customer_name trong contracts
            String customerNameFromJoin = rs.getString("customer_name");
            if (customerNameFromJoin == null) {
                try {
                    contract.setCustomerName(rs.getString("c.customer_name"));
                } catch (SQLException e) {
                    // Ignore
                }
            } else {
                contract.setCustomerName(customerNameFromJoin);
            }
            contract.setManagerName(rs.getString("manager_name"));
            
            // Các trường mới từ contracts table
            try {
                contract.setCustomerPhone(rs.getString("customer_phone"));
                contract.setCustomerAddress(rs.getString("customer_address"));
                contract.setMachineTypeId(rs.getInt("machine_type_id"));
                if (rs.wasNull()) contract.setMachineTypeId(null);
                contract.setQuantity(rs.getInt("quantity"));
                if (rs.wasNull()) contract.setQuantity(null);
                contract.setTotalCost(rs.getDouble("total_cost"));
                if (rs.wasNull()) contract.setTotalCost(null);
            } catch (SQLException e) {
                // Ignore if columns don't exist (backward compatibility)
            }
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
            "COALESCE(c.customer_name, cu.full_name) AS customer_name, " +
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
            // Tìm kiếm theo contract_code, customer_name (từ contracts table hoặc users table), manager name
            sql.append(" AND (c.contract_code LIKE ? OR " +
                       "COALESCE(c.customer_name, cu.full_name) LIKE ? OR " +
                       "c.customer_phone LIKE ? OR " +
                       "m.full_name LIKE ?)");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
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
     * Lấy mã hợp đồng tiếp theo dạng CT-001, CT-002...
     */
    public String getNextContractCode() {
        String sql = "SELECT contract_code FROM contracts ORDER BY id DESC LIMIT 1";
        String prefix = "CT-";
        String defaultCode = prefix + "001";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String lastCode = resultSet.getString("contract_code");
                if (lastCode != null && lastCode.startsWith(prefix)) {
                    try {
                        int num = Integer.parseInt(lastCode.substring(prefix.length()));
                        return prefix + String.format("%03d", num + 1);
                    } catch (NumberFormatException ignored) {
                        // Fallback to default if parse failed
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in getNextContractCode: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return defaultCode;
    }
}

