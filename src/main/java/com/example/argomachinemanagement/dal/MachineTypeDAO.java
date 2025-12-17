package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.MachineType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MachineTypeDAO extends DBContext {
    
    // Get all machine types for dropdown
    public List<MachineType> findAll() {
        List<MachineType> types = new ArrayList<>();
        String sql = "SELECT id, type_name, description FROM machine_types ORDER BY type_name";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                MachineType type = MachineType.builder()
                        .id(resultSet.getInt("id"))
                        .typeName(resultSet.getString("type_name"))
                        .description(resultSet.getString("description"))
                        .build();
                types.add(type);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineTypeDAO.findAll: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return types;
    }
    

     // Find machine type by ID

    public MachineType findById(Integer id) {
        MachineType type = null;
        String sql = "SELECT id, type_name, description FROM machine_types WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                type = MachineType.builder()
                        .id(resultSet.getInt("id"))
                        .typeName(resultSet.getString("type_name"))
                        .description(resultSet.getString("description"))
                        .build();
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineTypeDAO.findById: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return type;
    }
    

     // Insert new machine type

    public int insert(MachineType type) {
        int typeId = 0;
        String sql = "INSERT INTO machine_types (type_name, description) VALUES (?, ?)";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, type.getTypeName());
            statement.setString(2, type.getDescription());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    typeId = generatedKeys.getInt(1);
                    generatedKeys.close();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineTypeDAO.insert: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return typeId;
    }
    

     // Update machine type

    public boolean update(MachineType type) {
        boolean success = false;
        String sql = "UPDATE machine_types SET type_name = ?, description = ? WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, type.getTypeName());
            statement.setString(2, type.getDescription());
            statement.setInt(3, type.getId());
            
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in MachineTypeDAO.update: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    // Delete machine type by ID
    public boolean delete(Integer id) {
        boolean success = false;
        String sql = "DELETE FROM machine_types WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error in MachineTypeDAO.delete: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    // Check if type name already exists
    public boolean isTypeNameExists(String typeName, Integer excludeId) {
        boolean exists = false;
        String sql = "SELECT COUNT(*) FROM machine_types WHERE type_name = ?";
        if (excludeId != null) {
            sql += " AND id != ?";
        }
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, typeName);
            if (excludeId != null) {
                statement.setInt(2, excludeId);
            }
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineTypeDAO.isTypeNameExists: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return exists;
    }
    
    // Count machines by type ID
    public int countMachinesByType(Integer typeId) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM machines WHERE machine_type_id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, typeId);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineTypeDAO.countMachinesByType: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return count;
    }
    
    // Search machine types by keyword
    public List<MachineType> search(String keyword) {
        List<MachineType> types = new ArrayList<>();
        String sql = "SELECT id, type_name, description FROM machine_types " +
                     "WHERE type_name LIKE ? OR description LIKE ? ORDER BY type_name";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                MachineType type = MachineType.builder()
                        .id(resultSet.getInt("id"))
                        .typeName(resultSet.getString("type_name"))
                        .description(resultSet.getString("description"))
                        .build();
                types.add(type);
            }
        } catch (SQLException ex) {
            System.out.println("Error in MachineTypeDAO.search: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return types;
    }
}

