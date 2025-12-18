package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.Machine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MachineDAO extends DBContext implements I_DAO<Machine> {
    
    @Override
    public List<Machine> findAll() {
        List<Machine> machines = new ArrayList<>();
        String sql = "SELECT m.*, mt.type_name AS machine_type_name " +
                     "FROM machines m " +
                     "LEFT JOIN machine_types mt ON m.machine_type_id = mt.id " +
                     "ORDER BY m.created_at DESC";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Machine machine = getFromResultSet(resultSet);
                machines.add(machine);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineDAO.findAll: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return machines;
    }
    
    @Override
    public Machine findById(Integer id) {
        Machine machine = null;
        String sql = "SELECT m.*, mt.type_name AS machine_type_name " +
                     "FROM machines m " +
                     "LEFT JOIN machine_types mt ON m.machine_type_id = mt.id " +
                     "WHERE m.id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                machine = getFromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineDAO.findById: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return machine;
    }
    
    /**
     * Find machines with filters (status, keyword, typeId)
     */
    public List<Machine> findByFilters(String status, String keyword, Integer typeId) {
        List<Machine> machines = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT m.*, mt.type_name AS machine_type_name " +
            "FROM machines m " +
            "LEFT JOIN machine_types mt ON m.machine_type_id = mt.id " +
            "WHERE 1=1"
        );
        
        List<Object> params = new ArrayList<>();
        
        if (status != null && !status.isEmpty() && !status.equals("All Status")) {
            sql.append(" AND m.status = ?");
            params.add(status);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (m.machine_code LIKE ? OR m.machine_name LIKE ? OR m.location LIKE ?)");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (typeId != null) {
            sql.append(" AND m.machine_type_id = ?");
            params.add(typeId);
        }
        
        sql.append(" ORDER BY m.created_at DESC");
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql.toString());
            
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Machine machine = getFromResultSet(resultSet);
                machines.add(machine);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineDAO.findByFilters: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return machines;
    }
    
    @Override
    public int insert(Machine machine) {
        int machineId = 0;
        String sql = "INSERT INTO machines (machine_code, machine_name, machine_type_id, status, " +
                     "is_rentable, location, purchase_date, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, machine.getMachineCode());
            statement.setString(2, machine.getMachineName());
            statement.setInt(3, machine.getMachineTypeId());
            statement.setString(4, machine.getStatus() != null ? machine.getStatus() : "ACTIVE");
            statement.setBoolean(5, machine.getIsRentable() != null ? machine.getIsRentable() : true);
            statement.setString(6, machine.getLocation());
            statement.setDate(7, machine.getPurchaseDate());
            statement.setString(8, machine.getDescription());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    machineId = generatedKeys.getInt(1);
                    generatedKeys.close();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineDAO.insert: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return machineId;
    }
    
    @Override
    public boolean update(Machine machine) {
        boolean success = false;
        String sql = "UPDATE machines SET machine_code = ?, machine_name = ?, machine_type_id = ?, " +
                     "status = ?, is_rentable = ?, location = ?, purchase_date = ?, description = ? " +
                     "WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, machine.getMachineCode());
            statement.setString(2, machine.getMachineName());
            statement.setInt(3, machine.getMachineTypeId());
            statement.setString(4, machine.getStatus());
            statement.setBoolean(5, machine.getIsRentable() != null ? machine.getIsRentable() : true);
            statement.setString(6, machine.getLocation());
            statement.setDate(7, machine.getPurchaseDate());
            statement.setString(8, machine.getDescription());
            statement.setInt(9, machine.getId());
            
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in MachineDAO.update: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    /**
     * Deactivate a machine (set status to INACTIVE)
     */
    public boolean deactivate(Integer id) {
        boolean success = false;
        String sql = "UPDATE machines SET status = 'INACTIVE', is_rentable = 0 WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in MachineDAO.deactivate: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    /**
     * Update machine status
     */
    public boolean updateStatus(Integer id, String status) {
        boolean success = false;
        String sql = "UPDATE machines SET status = ?, is_rentable = ? WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, status);
            // If status is ACTIVE, set is_rentable to true, otherwise false
            statement.setBoolean(2, "ACTIVE".equals(status));
            statement.setInt(3, id);
            
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in MachineDAO.updateStatus: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    /**
     * Check if machine code already exists
     */
    public boolean isMachineCodeExists(String machineCode, Integer excludeId) {
        boolean exists = false;
        String sql = "SELECT COUNT(*) FROM machines WHERE machine_code = ?";
        if (excludeId != null) {
            sql += " AND id != ?";
        }
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, machineCode);
            if (excludeId != null) {
                statement.setInt(2, excludeId);
            }
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineDAO.isMachineCodeExists: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return exists;
    }
    
    @Override
    public boolean delete(Machine machine) {
        // Not implementing hard delete, use deactivate instead
        return false;
    }
    
    @Override
    public Map<Integer, Machine> findAllMap() {
        return Map.of();
    }
    
    @Override
    public Machine getFromResultSet(ResultSet rs) throws SQLException {
        Machine machine = Machine.builder()
                .id(rs.getInt("id"))
                .machineCode(rs.getString("machine_code"))
                .machineName(rs.getString("machine_name"))
                .machineTypeId(rs.getInt("machine_type_id"))
                .status(rs.getString("status"))
                .isRentable(rs.getBoolean("is_rentable"))
                .location(rs.getString("location"))
                .purchaseDate(rs.getDate("purchase_date"))
                .description(rs.getString("description"))
                .createdAt(rs.getTimestamp("created_at"))
                .updatedAt(rs.getTimestamp("updated_at"))
                .build();
        
        // Set display field
        try {
            machine.setMachineTypeName(rs.getString("machine_type_name"));
        } catch (SQLException e) {
            // Column might not exist in some queries
        }
        
        return machine;
    }


    public List<Machine> findAllForCustomer() {
        List<Machine> machines = new ArrayList<>();

        String sql = """
        SELECT m.id,
               m.machine_code,
               m.machine_name,
               m.status,
               mt.type_name AS machine_type_name
        FROM machines m
        JOIN machine_types mt ON m.machine_type_id = mt.id
        WHERE m.status = 'ACTIVE'
          AND m.is_rentable = 1
        ORDER BY m.created_at DESC
    """;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Machine machine = Machine.builder()
                        .id(resultSet.getInt("id"))
                        .machineCode(resultSet.getString("machine_code"))
                        .machineName(resultSet.getString("machine_name"))
                        .status(resultSet.getString("status"))
                        .machineTypeName(resultSet.getString("machine_type_name"))
                        .build();

                machines.add(machine);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineDAO.findAllForCustomer: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return machines;
    }

    public Machine findDetailForCustomer(Integer id) {
        Machine machine = null;

        String sql = """
        SELECT m.*,
               mt.type_name AS machine_type_name
        FROM machines m
        JOIN machine_types mt ON m.machine_type_id = mt.id
        WHERE m.id = ?
          AND m.status = 'ACTIVE'
          AND m.is_rentable = 1
    """;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                machine = getFromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineDAO.findDetailForCustomer: " + ex.getMessage());
        } finally {
            closeResources();
        }

        return machine;
    }

}

