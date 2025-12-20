package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.MachineType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for machine_types table
 */
public class MachineTypeDAO extends DBContext {
    
    /**
     * Get all machine types for dropdown
     */
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
    
    /**
     * Find machine type by ID
     */
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
}

