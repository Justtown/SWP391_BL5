package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.Contract;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContractDAO extends DBContext implements I_DAO<Contract> {
    private static final Logger LOGGER = Logger.getLogger(ContractDAO.class.getName());
    private static final ThreadLocal<String> LAST_ERROR = new ThreadLocal<>();

    public String getLastError() {
        return LAST_ERROR.get();
    }

    private void setLastError(String msg) {
        LAST_ERROR.set(msg);
    }

    private static String normalizeStatusForDb(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "DRAFT";
        }
        String s = status.trim().toUpperCase();
        // Common mapping: many schemas use ACTIVE instead of APPROVED
        if ("APPROVED".equals(s)) {
            return "ACTIVE";
        }
        return s;
    }
    
    @Override
    public List<Contract> findAll() {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT c.*, " +
                "COALESCE(c.customer_name, cu.full_name) AS customer_name, " +
                "u_m.full_name AS manager_name, " +
                "ma.machine_code, ma.machine_name, mt.type_name AS machine_type_name " +
                "FROM contracts c " +
                "LEFT JOIN users cu ON c.customer_id = cu.id " +
                "LEFT JOIN users u_m ON c.manager_id = u_m.id " +
                "LEFT JOIN machines ma ON c.machine_id = ma.id " +
                "LEFT JOIN machine_types mt ON ma.machine_type_id = mt.id " +
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
            // Backward compatibility if DB doesn't have machine_id
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unknown column")) {
                closeResources();
                contracts.clear();
                String sqlOld = "SELECT c.*, " +
                        "COALESCE(c.customer_name, cu.full_name) AS customer_name, " +
                        "u_m.full_name AS manager_name " +
                        "FROM contracts c " +
                        "LEFT JOIN users cu ON c.customer_id = cu.id " +
                        "LEFT JOIN users u_m ON c.manager_id = u_m.id " +
                        "ORDER BY c.created_at DESC";
                try {
                    connection = getConnection();
                    statement = connection.prepareStatement(sqlOld);
                    resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        Contract contract = getFromResultSet(resultSet);
                        contracts.add(contract);
                    }
                } catch (SQLException ex2) {
                    System.out.println("Error in findAll (fallback): " + ex2.getMessage());
                } finally {
                    closeResources();
                }
                return contracts;
            }
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
                "COALESCE(c.customer_name, cu.full_name) AS customer_name, " +
                "u_m.full_name AS manager_name, " +
                "ma.machine_code, ma.machine_name, mt.type_name AS machine_type_name " +
                "FROM contracts c " +
                "LEFT JOIN users cu ON c.customer_id = cu.id " +
                "LEFT JOIN users u_m ON c.manager_id = u_m.id " +
                "LEFT JOIN machines ma ON c.machine_id = ma.id " +
                "LEFT JOIN machine_types mt ON ma.machine_type_id = mt.id " +
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
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unknown column")) {
                closeResources();
                String sqlOld = "SELECT c.*, " +
                        "COALESCE(c.customer_name, cu.full_name) AS customer_name, " +
                        "u_m.full_name AS manager_name " +
                        "FROM contracts c " +
                        "LEFT JOIN users cu ON c.customer_id = cu.id " +
                        "LEFT JOIN users u_m ON c.manager_id = u_m.id " +
                        "WHERE c.id = ?";
                try {
                    connection = getConnection();
                    statement = connection.prepareStatement(sqlOld);
                    statement.setInt(1, id);
                    resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        contract = getFromResultSet(resultSet);
                    }
                } catch (SQLException ex2) {
                    System.out.println("Error in findById (fallback): " + ex2.getMessage());
                } finally {
                    closeResources();
                }
                return contract;
            }
            System.out.println("Error in findById: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return contract;
    }
    
    @Override
    public int insert(Contract contract) {
        int contractId = 0;
        setLastError(null);

        if (contract == null) {
            setLastError("Contract is null");
            return 0;
        }

        // Ensure note is never null (many schemas require it)
        if (contract.getNote() == null) {
            String fallback = contract.getServiceDescription();
            contract.setNote(fallback != null ? fallback : "");
        }

        // Thử insert với các trường mới trước, nếu lỗi thì fallback về cấu trúc cũ
        String sql = "INSERT INTO contracts (contract_code, customer_id, manager_id, " +
                "start_date, end_date, status, note, " +
                "customer_name, customer_phone, customer_address, machine_id, machine_type_id, quantity, total_cost, service_description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Some schemas have created_at NOT NULL without default
        String sqlWithCreatedAt = "INSERT INTO contracts (contract_code, customer_id, manager_id, " +
                "start_date, end_date, status, note, " +
                "customer_name, customer_phone, customer_address, machine_id, machine_type_id, quantity, total_cost, service_description, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, contract.getContractCode());
            statement.setInt(2, contract.getCustomerId());
            statement.setInt(3, contract.getManagerId());
            statement.setDate(4, contract.getStartDate());
            statement.setDate(5, contract.getEndDate());
            // Try raw status first; retry if DB doesn't accept it
            String rawStatus = contract.getStatus() != null ? contract.getStatus() : "DRAFT";
            statement.setString(6, rawStatus);
            statement.setString(7, contract.getNote() != null ? contract.getNote() : "");
            // Các trường mới
            statement.setString(8, contract.getCustomerName());
            statement.setString(9, contract.getCustomerPhone());
            statement.setString(10, contract.getCustomerAddress());
            if (contract.getMachineId() != null) {
                statement.setInt(11, contract.getMachineId());
            } else {
                statement.setNull(11, java.sql.Types.INTEGER);
            }
            if (contract.getMachineTypeId() != null) {
                statement.setInt(12, contract.getMachineTypeId());
            } else {
                statement.setNull(12, java.sql.Types.INTEGER);
            }
            if (contract.getQuantity() != null) {
                statement.setInt(13, contract.getQuantity());
            } else {
                statement.setNull(13, java.sql.Types.INTEGER);
            }
            if (contract.getTotalCost() != null) {
                statement.setDouble(14, contract.getTotalCost());
            } else {
                statement.setNull(14, java.sql.Types.DOUBLE);
            }
            statement.setString(15, contract.getServiceDescription());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    contractId = generatedKeys.getInt(1);
                    generatedKeys.close();
                }
            }
        } catch (SQLException ex) {
            String msg = ex.getMessage();
            setLastError(msg);
            LOGGER.log(Level.SEVERE, "[ContractDAO][insert] Failed: " + msg, ex);

            // Retry once for common DB issues (status enum / created_at)
            try {
                closeResources();

                boolean retryStatus = msg != null
                        && msg.toLowerCase().contains("status")
                        && (msg.toLowerCase().contains("data truncated")
                        || msg.toLowerCase().contains("incorrect")
                        || msg.toLowerCase().contains("enum"));

                boolean needCreatedAt = msg != null
                        && msg.toLowerCase().contains("created_at")
                        && (msg.toLowerCase().contains("doesn't have a default value")
                        || msg.toLowerCase().contains("cannot be null"));

                if (retryStatus) {
                    String normalized = normalizeStatusForDb(contract.getStatus());
                    LOGGER.warning("[ContractDAO][insert][RETRY] status -> " + normalized + " (was " + contract.getStatus() + ")");
                    contract.setStatus(normalized);
                }

                if (retryStatus || needCreatedAt) {
                    connection = getConnection();
                    statement = connection.prepareStatement(needCreatedAt ? sqlWithCreatedAt : sql, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, contract.getContractCode());
                    statement.setInt(2, contract.getCustomerId());
                    statement.setInt(3, contract.getManagerId());
                    statement.setDate(4, contract.getStartDate());
                    statement.setDate(5, contract.getEndDate());
                    statement.setString(6, contract.getStatus() != null ? contract.getStatus() : "DRAFT");
                    statement.setString(7, contract.getNote() != null ? contract.getNote() : "");
                    statement.setString(8, contract.getCustomerName());
                    statement.setString(9, contract.getCustomerPhone());
                    statement.setString(10, contract.getCustomerAddress());
                    if (contract.getMachineId() != null) statement.setInt(11, contract.getMachineId()); else statement.setNull(11, java.sql.Types.INTEGER);
                    if (contract.getMachineTypeId() != null) statement.setInt(12, contract.getMachineTypeId()); else statement.setNull(12, java.sql.Types.INTEGER);
                    if (contract.getQuantity() != null) statement.setInt(13, contract.getQuantity()); else statement.setNull(13, java.sql.Types.INTEGER);
                    if (contract.getTotalCost() != null) statement.setDouble(14, contract.getTotalCost()); else statement.setNull(14, java.sql.Types.DOUBLE);
                    statement.setString(15, contract.getServiceDescription());

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        ResultSet generatedKeys = statement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            contractId = generatedKeys.getInt(1);
                            generatedKeys.close();
                            setLastError(null);
                            return contractId;
                        }
                    }
                }
            } catch (SQLException retryEx) {
                setLastError(retryEx.getMessage());
                LOGGER.log(Level.SEVERE, "[ContractDAO][insert][RETRY] Failed: " + retryEx.getMessage(), retryEx);
            }

            // Nếu lỗi do các cột mới chưa tồn tại, thử insert với cấu trúc cũ
            if (msg != null && (msg.contains("Unknown column") || msg.contains("doesn't exist"))) {
                LOGGER.warning("[ContractDAO][insert][FALLBACK] New columns not found, using old structure: " + msg);
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
                    statement.setString(7, contract.getNote() != null ? contract.getNote() : "");
                    
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        ResultSet generatedKeys = statement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            contractId = generatedKeys.getInt(1);
                            generatedKeys.close();
                            setLastError(null);
                        }
                    }
                } catch (SQLException ex2) {
                    setLastError(ex2.getMessage());
                    LOGGER.log(Level.SEVERE, "[ContractDAO][insert][FALLBACK] Failed: " + ex2.getMessage(), ex2);
                }
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
            contract.setCustomerName(rs.getString("customer_name"));
            contract.setManagerName(rs.getString("manager_name"));
            // machine display fields (from JOIN)
            try {
                contract.setMachineCode(rs.getString("machine_code"));
                contract.setMachineName(rs.getString("machine_name"));
                contract.setMachineTypeName(rs.getString("machine_type_name"));
            } catch (SQLException ignored) {
            }
            
            // Các trường mới từ contracts table
            try {
                contract.setCustomerPhone(rs.getString("customer_phone"));
                contract.setCustomerAddress(rs.getString("customer_address"));
                contract.setMachineId(rs.getInt("machine_id"));
                if (rs.wasNull()) contract.setMachineId(null);
                contract.setMachineTypeId(rs.getInt("machine_type_id"));
                if (rs.wasNull()) contract.setMachineTypeId(null);
                contract.setQuantity(rs.getInt("quantity"));
                if (rs.wasNull()) contract.setQuantity(null);
                contract.setTotalCost(rs.getDouble("total_cost"));
                if (rs.wasNull()) contract.setTotalCost(null);
                contract.setServiceDescription(rs.getString("service_description"));
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
                        "u_m.full_name AS manager_name, " +
                        "ma.machine_code, ma.machine_name, mt.type_name AS machine_type_name " +
                        "FROM contracts c " +
                        "LEFT JOIN users cu ON c.customer_id = cu.id " +
                        "LEFT JOIN users u_m ON c.manager_id = u_m.id " +
                        "LEFT JOIN machines ma ON c.machine_id = ma.id " +
                        "LEFT JOIN machine_types mt ON ma.machine_type_id = mt.id " +
                        "WHERE 1=1"
        );
        
        List<Object> params = new ArrayList<>();
        
        if (status != null && !status.isEmpty() && !status.equals("All Status")) {
            String s = status.trim().toUpperCase();
            if ("APPROVED".equals(s)) {
                sql.append(" AND (c.status = 'APPROVED' OR c.status = 'ACTIVE')");
            } else if ("PENDING".equals(s)) {
                sql.append(" AND (c.status = 'PENDING' OR c.status = 'DRAFT')");
            } else if ("REJECTED".equals(s)) {
                sql.append(" AND (c.status = 'REJECTED' OR c.status = 'CANCELLED')");
            } else {
                sql.append(" AND c.status = ?");
                params.add(s);
            }
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Tìm kiếm theo contract_code, customer_name (từ contracts table hoặc users table), manager name
            sql.append(" AND (c.contract_code LIKE ? OR " +
                    "COALESCE(c.customer_name, cu.full_name) LIKE ? OR " +
                    "c.customer_phone LIKE ? OR " +
                    "u_m.full_name LIKE ? OR " +
                    "ma.machine_code LIKE ? OR " +
                    "ma.machine_name LIKE ? OR " +
                    "c.service_description LIKE ?)");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
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
            // Backward compatibility: DB chưa có machine_id/service_description
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unknown column")) {
                closeResources();
                contracts.clear();
                StringBuilder sqlOld = new StringBuilder(
                        "SELECT c.*, " +
                                "COALESCE(c.customer_name, cu.full_name) AS customer_name, " +
                                "u_m.full_name AS manager_name " +
                                "FROM contracts c " +
                                "LEFT JOIN users cu ON c.customer_id = cu.id " +
                                "LEFT JOIN users u_m ON c.manager_id = u_m.id " +
                                "WHERE 1=1"
                );
                List<Object> paramsOld = new ArrayList<>();

                if (status != null && !status.isEmpty() && !status.equals("All Status")) {
                    String s = status.trim().toUpperCase();
                    if ("APPROVED".equals(s)) {
                        sqlOld.append(" AND (c.status = 'APPROVED' OR c.status = 'ACTIVE')");
                    } else if ("PENDING".equals(s)) {
                        sqlOld.append(" AND (c.status = 'PENDING' OR c.status = 'DRAFT')");
                    } else if ("REJECTED".equals(s)) {
                        sqlOld.append(" AND (c.status = 'REJECTED' OR c.status = 'CANCELLED')");
                    } else {
                        sqlOld.append(" AND c.status = ?");
                        paramsOld.add(s);
                    }
                }
                if (keyword != null && !keyword.trim().isEmpty()) {
                    sqlOld.append(" AND (c.contract_code LIKE ? OR " +
                            "COALESCE(c.customer_name, cu.full_name) LIKE ? OR " +
                            "c.customer_phone LIKE ? OR " +
                            "u_m.full_name LIKE ?)");
                    String searchPattern = "%" + keyword.trim() + "%";
                    paramsOld.add(searchPattern);
                    paramsOld.add(searchPattern);
                    paramsOld.add(searchPattern);
                    paramsOld.add(searchPattern);
                }
                if (customerId != null) {
                    sqlOld.append(" AND c.customer_id = ?");
                    paramsOld.add(customerId);
                }
                if (managerId != null) {
                    sqlOld.append(" AND c.manager_id = ?");
                    paramsOld.add(managerId);
                }
                sqlOld.append(" ORDER BY c.created_at DESC");

                try {
                    connection = getConnection();
                    statement = connection.prepareStatement(sqlOld.toString());
                    for (int i = 0; i < paramsOld.size(); i++) {
                        statement.setObject(i + 1, paramsOld.get(i));
                    }
                    resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        Contract contract = getFromResultSet(resultSet);
                        contracts.add(contract);
                    }
                } catch (SQLException ex2) {
                    System.out.println("Error in findByFilters (fallback): " + ex2.getMessage());
                } finally {
                    closeResources();
                }
                return contracts;
            }
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
        // Generate next HDx code based on BOTH service_orders and contracts
        String sql = "SELECT MAX(CAST(SUBSTRING(code, 3) AS UNSIGNED)) AS max_num " +
                "FROM ( " +
                "  SELECT contract_code AS code FROM service_orders WHERE contract_code LIKE 'HD%' " +
                "  UNION ALL " +
                "  SELECT contract_code AS code FROM contracts WHERE contract_code LIKE 'HD%' " +
                ") t";

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int maxNum = resultSet.getInt("max_num");
                if (!resultSet.wasNull()) {
                    return "HD" + (maxNum + 1);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in getNextContractCode: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return "HD1";
    }

    public Integer findIdByContractCode(String contractCode) {
        if (contractCode == null || contractCode.trim().isEmpty()) {
            return null;
        }
        Integer id = null;
        String sql = "SELECT id FROM contracts WHERE contract_code = ? LIMIT 1";
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, contractCode.trim());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException ex) {
            System.out.println("Error in findIdByContractCode: " + ex.getMessage());
        } finally {
            closeResources();
        }
        return id;
    }
}

