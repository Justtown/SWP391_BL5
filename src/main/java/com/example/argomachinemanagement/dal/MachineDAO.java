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
        String sql = "SELECT a.id, a.serial_number AS machine_code, " +
                     "mm.model_name AS machine_name, mm.type_id AS machine_type_id, " +
                     "a.status, " +
                     "CASE WHEN a.status = 'ACTIVE' AND a.rental_status = 'AVAILABLE' THEN 1 ELSE 0 END AS is_rentable, " +
                     "a.location, a.purchase_date, a.note AS description, " +
                     "a.created_at, NULL AS updated_at, " +
                     "mt.type_name AS machine_type_name " +
                     "FROM machine_assets a " +
                     "LEFT JOIN machine_models mm ON a.model_id = mm.id " +
                     "LEFT JOIN machine_types mt ON mm.type_id = mt.id " +
                     "ORDER BY a.created_at DESC";
        
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
        String sql = "SELECT a.id, a.serial_number AS machine_code, " +
                     "mm.model_name AS machine_name, mm.type_id AS machine_type_id, " +
                     "a.status, " +
                     "CASE WHEN a.status = 'ACTIVE' AND a.rental_status = 'AVAILABLE' THEN 1 ELSE 0 END AS is_rentable, " +
                     "a.location, a.purchase_date, a.note AS description, " +
                     "a.created_at, NULL AS updated_at, " +
                     "mt.type_name AS machine_type_name " +
                     "FROM machine_assets a " +
                     "LEFT JOIN machine_models mm ON a.model_id = mm.id " +
                     "LEFT JOIN machine_types mt ON mm.type_id = mt.id " +
                     "WHERE a.id = ?";
        
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
            "SELECT a.id, a.serial_number AS machine_code, " +
            "mm.model_name AS machine_name, mm.type_id AS machine_type_id, " +
            "a.status, " +
            "CASE WHEN a.status = 'ACTIVE' AND a.rental_status = 'AVAILABLE' THEN 1 ELSE 0 END AS is_rentable, " +
            "a.location, a.purchase_date, a.note AS description, " +
            "a.created_at, NULL AS updated_at, " +
            "mt.type_name AS machine_type_name " +
            "FROM machine_assets a " +
            "LEFT JOIN machine_models mm ON a.model_id = mm.id " +
            "LEFT JOIN machine_types mt ON mm.type_id = mt.id " +
            "WHERE 1=1"
        );
        
        List<Object> params = new ArrayList<>();
        
        if (status != null && !status.isEmpty() && !status.equals("All Status")) {
            sql.append(" AND a.status = ?");
            params.add(status);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (a.serial_number LIKE ? OR mm.model_name LIKE ? OR a.location LIKE ?)");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (typeId != null) {
            sql.append(" AND mm.type_id = ?");
            params.add(typeId);
        }
        
        sql.append(" ORDER BY a.created_at DESC");
        
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
        // Note: Machine entity không đủ thông tin để tạo machine_asset (thiếu model_id)
        // Method này sẽ không hoạt động đúng với schema mới
        // Nên dùng MachineAssetDAO.insert() thay vì method này
        int machineId = 0;
        String sql = "INSERT INTO machine_assets (serial_number, model_id, status, rental_status, location, purchase_date, note) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, machine.getMachineCode()); // serial_number
            // Note: machine.getMachineTypeId() không phải model_id, cần tìm model_id từ type_id
            // Tạm thời set NULL, cần sửa logic sau
            statement.setNull(2, java.sql.Types.INTEGER); // model_id - cần sửa
            statement.setString(3, machine.getStatus() != null ? machine.getStatus() : "ACTIVE");
            String rentalStatus = (machine.getIsRentable() != null && machine.getIsRentable()) ? "AVAILABLE" : "RENTED";
            statement.setString(4, rentalStatus);
            statement.setString(5, machine.getLocation());
            statement.setDate(6, machine.getPurchaseDate());
            statement.setString(7, machine.getDescription());
            
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
        // Note: Method này không đầy đủ với schema mới (thiếu model_id)
        // Nên dùng MachineAssetDAO.update() thay vì method này
        boolean success = false;
        String sql = "UPDATE machine_assets SET serial_number = ?, status = ?, " +
                     "rental_status = ?, location = ?, purchase_date = ?, note = ? " +
                     "WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, machine.getMachineCode()); // serial_number
            statement.setString(2, machine.getStatus());
            String rentalStatus = (machine.getIsRentable() != null && machine.getIsRentable()) ? "AVAILABLE" : "RENTED";
            statement.setString(3, rentalStatus);
            statement.setString(4, machine.getLocation());
            statement.setDate(5, machine.getPurchaseDate());
            statement.setString(6, machine.getDescription());
            statement.setInt(7, machine.getId());
            
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
        String sql = "UPDATE machine_assets SET status = 'INACTIVE' WHERE id = ?";
        
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
        String sql = "UPDATE machine_assets SET status = ? WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, status);
            statement.setInt(2, id);
            
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
        String sql = "SELECT COUNT(*) FROM machine_assets WHERE serial_number = ?";
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
        SELECT a.id,
               a.serial_number AS machine_code,
               mm.model_name AS machine_name,
               a.status,
               mt.type_name AS machine_type_name
        FROM machine_assets a
        JOIN machine_models mm ON a.model_id = mm.id
        JOIN machine_types mt ON mm.type_id = mt.id
        WHERE a.status = 'ACTIVE'
          AND a.rental_status = 'AVAILABLE'
        ORDER BY a.created_at DESC
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
        SELECT a.id, a.serial_number AS machine_code,
               mm.model_name AS machine_name, mm.type_id AS machine_type_id,
               a.status,
               CASE WHEN a.status = 'ACTIVE' AND a.rental_status = 'AVAILABLE' THEN 1 ELSE 0 END AS is_rentable,
               a.location, a.purchase_date, a.note AS description,
               a.created_at, NULL AS updated_at,
               mt.type_name AS machine_type_name
        FROM machine_assets a
        JOIN machine_models mm ON a.model_id = mm.id
        JOIN machine_types mt ON mm.type_id = mt.id
        WHERE a.id = ?
          AND a.status = 'ACTIVE'
          AND a.rental_status = 'AVAILABLE'
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

