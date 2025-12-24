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
                     "cu.phone_number AS customer_phone, " +
                     "cu.address AS customer_address, " +
                     "m.full_name AS manager_name " +
                     "FROM contracts c " +
                     "LEFT JOIN users cu ON c.customer_id = cu.id " +
                     "LEFT JOIN users m ON c.manager_id = m.id " +
                     "WHERE c.id = ?";
        
        try {
            System.out.println("[ContractDAO] Finding contract by id: " + id);
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                contract = getFromResultSet(resultSet);
                System.out.println("[ContractDAO] Contract found: " + (contract != null ? contract.getContractCode() : "null"));
            } else {
                System.out.println("[ContractDAO] No contract found with id: " + id);
            }
        } catch (SQLException ex) {
            System.out.println("[ContractDAO] Error in findById: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println("[ContractDAO] Unexpected error in findById: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeResources();
        }
        
        return contract;
    }
    
    @Override
    public int insert(Contract contract) {
        int contractId = 0;
        String sql = "INSERT INTO contracts (contract_code, customer_id, manager_id, sale_id, " +
                     "start_date, end_date, status, note) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, contract.getContractCode());
            statement.setInt(2, contract.getCustomerId());
            statement.setInt(3, contract.getManagerId());
            // Sale ID - có thể null
            if (contract.getSaleId() != null) {
                statement.setInt(4, contract.getSaleId());
            } else {
                statement.setNull(4, Types.INTEGER);
            }
            statement.setDate(5, contract.getStartDate());
            statement.setDate(6, contract.getEndDate());
            statement.setString(7, contract.getStatus() != null ? contract.getStatus() : "DRAFT");
            statement.setString(8, contract.getNote());
            
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
                .build();
        
        // Try to get sale_id if it exists
        try {
            int saleId = rs.getInt("sale_id");
            if (!rs.wasNull()) {
                contract.setSaleId(saleId);
            }
        } catch (SQLException e) {
            // Column doesn't exist, set to null
            contract.setSaleId(null);
        }
        
        // Try to get updated_at if it exists
        try {
            contract.setUpdatedAt(rs.getTimestamp("updated_at"));
        } catch (SQLException e) {
            // Column doesn't exist, set to null
            contract.setUpdatedAt(null);
        }
        
        // Set display names and customer info
        try {
            contract.setCustomerName(rs.getString("customer_name"));
            contract.setCustomerPhone(rs.getString("customer_phone"));
            contract.setCustomerAddress(rs.getString("customer_address"));
            contract.setManagerName(rs.getString("manager_name"));
        } catch (SQLException e) {
            // Ignore if columns don't exist
            System.out.println("Warning: Could not set customer/manager info: " + e.getMessage());
        }
        
        return contract;
    }
    
    /**
     * Find contracts with filters (for C1 - Contract List)
     */
    public List<Contract> findByFilters(String status, String keyword, Integer customerId, Integer managerId, Integer saleId) {
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

        if (status != null && !status.isEmpty() && !status.equals("All Status") && !status.equals("All")) {
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

        if (saleId != null) {
            sql.append(" AND c.sale_id = ?");
            params.add(saleId);
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
     * Generate contract code: CT-YYYY-NNN
     */
    public String generateContractCode() {
        String sql = "SELECT MAX(CAST(SUBSTRING(contract_code, 9) AS UNSIGNED)) as max_num " +
                     "FROM contracts WHERE contract_code LIKE CONCAT('CT-', YEAR(CURRENT_DATE), '-%')";
        int nextNum = 1;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int maxNum = resultSet.getInt("max_num");
                if (!resultSet.wasNull()) {
                    nextNum = maxNum + 1;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in generateContractCode: " + ex.getMessage());
        } finally {
            closeResources();
        }

        java.util.Calendar cal = java.util.Calendar.getInstance();
        int year = cal.get(java.util.Calendar.YEAR);
        return String.format("CT-%d-%03d", year, nextNum);
    }

    /**
     * Update status của contract
     */
    public boolean updateStatus(Integer contractId, String newStatus) {
        boolean success = false;
        String sql = "UPDATE contracts SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, newStatus);
            statement.setInt(2, contractId);

            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in updateStatus: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return success;
    }

    /**
     * Đếm contracts theo status
     */
    public int countByStatus(String status) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM contracts WHERE status = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, status);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error in countByStatus: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return count;
    }

    /**
     * Lấy contracts của một customer
     */
    public List<Contract> findByCustomerId(Integer customerId) {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT c.*, " +
                     "cu.full_name AS customer_name, " +
                     "m.full_name AS manager_name " +
                     "FROM contracts c " +
                     "LEFT JOIN users cu ON c.customer_id = cu.id " +
                     "LEFT JOIN users m ON c.manager_id = m.id " +
                     "WHERE c.customer_id = ? " +
                     "ORDER BY c.created_at DESC";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, customerId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Contract contract = getFromResultSet(resultSet);
                contracts.add(contract);
            }
        } catch (SQLException ex) {
            System.out.println("Error in findByCustomerId: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return contracts;
    }

    /**
     * Lấy contracts của một manager
     */
    public List<Contract> findByManagerId(Integer managerId) {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT c.*, " +
                     "cu.full_name AS customer_name, " +
                     "m.full_name AS manager_name " +
                     "FROM contracts c " +
                     "LEFT JOIN users cu ON c.customer_id = cu.id " +
                     "LEFT JOIN users m ON c.manager_id = m.id " +
                     "WHERE c.manager_id = ? " +
                     "ORDER BY c.created_at DESC";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, managerId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Contract contract = getFromResultSet(resultSet);
                contracts.add(contract);
            }
        } catch (SQLException ex) {
            System.out.println("Error in findByManagerId: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return contracts;
    }

    /**
     * Kiểm tra contract code đã tồn tại chưa
     */
    public boolean isContractCodeExists(String contractCode) {
        boolean exists = false;
        String sql = "SELECT COUNT(*) FROM contracts WHERE contract_code = ?";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, contractCode);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error in isContractCodeExists: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return exists;
    }
}

