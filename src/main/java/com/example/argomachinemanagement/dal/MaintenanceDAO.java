package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.Maintenance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MaintenanceDAO extends DBContext {
    

     // Get all maintenances

    public List<Maintenance> findAll() {
        List<Maintenance> list = new ArrayList<>();
        String sql = "SELECT m.*, mc.machine_code, mc.machine_name, mt.type_name AS machine_type_name " +
                     "FROM maintenances m " +
                     "LEFT JOIN machines mc ON m.machine_id = mc.id " +
                     "LEFT JOIN machine_types mt ON mc.machine_type_id = mt.id " +
                     "ORDER BY m.maintenance_date DESC, m.created_at DESC";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                list.add(mapResultSet(resultSet));
            }
        } catch (SQLException ex) {
            System.out.println("Error in MaintenanceDAO.findAll: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return list;
    }
    
    // Find by ID
    public Maintenance findById(Integer id) {
        Maintenance m = null;
        String sql = "SELECT m.*, mc.machine_code, mc.machine_name, mt.type_name AS machine_type_name " +
                     "FROM maintenances m " +
                     "LEFT JOIN machines mc ON m.machine_id = mc.id " +
                     "LEFT JOIN machine_types mt ON mc.machine_type_id = mt.id " +
                     "WHERE m.id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                m = mapResultSet(resultSet);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MaintenanceDAO.findById: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return m;
    }
    
    // Find with filters
    public List<Maintenance> findByFilters(Integer machineId, String maintenanceType, String status) {
        List<Maintenance> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT m.*, mc.machine_code, mc.machine_name, mt.type_name AS machine_type_name " +
            "FROM maintenances m " +
            "LEFT JOIN machines mc ON m.machine_id = mc.id " +
            "LEFT JOIN machine_types mt ON mc.machine_type_id = mt.id " +
            "WHERE 1=1"
        );
        
        List<Object> params = new ArrayList<>();
        
        if (machineId != null) {
            sql.append(" AND m.machine_id = ?");
            params.add(machineId);
        }
        
        if (maintenanceType != null && !maintenanceType.isEmpty()) {
            sql.append(" AND m.maintenance_type = ?");
            params.add(maintenanceType);
        }
        
        if (status != null && !status.isEmpty()) {
            sql.append(" AND m.status = ?");
            params.add(status);
        }
        
        sql.append(" ORDER BY m.maintenance_date DESC");
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql.toString());
            
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                list.add(mapResultSet(resultSet));
            }
        } catch (SQLException ex) {
            System.out.println("Error in MaintenanceDAO.findByFilters: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return list;
    }
    
    // Insert new maintenance
    public int insert(Maintenance m) {
        int id = 0;
        String sql = "INSERT INTO maintenances (machine_id, maintenance_type, maintenance_date, " +
                     "performed_by, description, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, m.getMachineId());
            statement.setString(2, m.getMaintenanceType());
            statement.setDate(3, m.getMaintenanceDate());
            statement.setString(4, m.getPerformedBy());
            statement.setString(5, m.getDescription());
            statement.setString(6, m.getStatus() != null ? m.getStatus() : "COMPLETED");
            
            int rows = statement.executeUpdate();
            if (rows > 0) {
                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next()) {
                    id = keys.getInt(1);
                }
                keys.close();
            }
        } catch (SQLException ex) {
            System.out.println("Error in MaintenanceDAO.insert: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return id;
    }
    
    // Update maintenance
    public boolean update(Maintenance m) {
        boolean success = false;
        String sql = "UPDATE maintenances SET machine_id = ?, maintenance_type = ?, " +
                     "maintenance_date = ?, performed_by = ?, description = ?, status = ? WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, m.getMachineId());
            statement.setString(2, m.getMaintenanceType());
            statement.setDate(3, m.getMaintenanceDate());
            statement.setString(4, m.getPerformedBy());
            statement.setString(5, m.getDescription());
            statement.setString(6, m.getStatus());
            statement.setInt(7, m.getId());
            
            success = statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error in MaintenanceDAO.update: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    // Delete maintenance
    public boolean delete(Integer id) {
        boolean success = false;
        String sql = "DELETE FROM maintenances WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            success = statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error in MaintenanceDAO.delete: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    // Count all
    public int countAll() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM maintenances";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MaintenanceDAO.countAll: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return count;
    }
    
    // Count by status
    public int countByStatus(String status) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM maintenances WHERE status = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, status);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MaintenanceDAO.countByStatus: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return count;
    }
    
    // Map ResultSet to Maintenance
    private Maintenance mapResultSet(ResultSet rs) throws SQLException {
        Maintenance m = Maintenance.builder()
                .id(rs.getInt("id"))
                .machineId(rs.getInt("machine_id"))
                .maintenanceType(rs.getString("maintenance_type"))
                .maintenanceDate(rs.getDate("maintenance_date"))
                .performedBy(rs.getString("performed_by"))
                .description(rs.getString("description"))
                .status(rs.getString("status"))
                .createdAt(rs.getTimestamp("created_at"))
                .build();
        
        try {
            m.setMachineCode(rs.getString("machine_code"));
            m.setMachineName(rs.getString("machine_name"));
            m.setMachineTypeName(rs.getString("machine_type_name"));
        } catch (SQLException e) {
            // Columns might not exist
        }
        
        return m;
    }
}

