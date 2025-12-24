package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.Profile;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileDAO extends DBContext {
    
    /**
     * Lấy profile theo user_id từ bảng users kèm thông tin role
     */
    public Profile getProfileByUserId(Integer userId) {
        Profile profile = null;
        
        if (userId == null) {
            System.err.println("getProfileByUserId: userId is NULL!");
            return null;
        }
        
        // Query từ bảng users - lấy tất cả các cột bao gồm phone_number, address, birthdate
        String sql = "SELECT u.id, u.id as user_id, " +
                     "COALESCE(u.full_name, '') as name, " +
                     "COALESCE(u.email, '') as email, " +
                     "u.username, " +
                     "u.phone_number as phone, " +
                     "u.address, " +
                     "u.avatar, " +
                     "u.birthdate, " +
                     "u.created_at, " +
                     "COALESCE(u.updated_at, u.created_at) as updated_at, " +
                     "COALESCE(r.role_name, 'customer') as role_name " +
                     "FROM users u " +
                     "LEFT JOIN user_role ur ON u.id = ur.user_id " +
                     "LEFT JOIN roles r ON ur.role_id = r.id " +
                     "WHERE u.id = ?";
        
        try {
            if (connection == null || connection.isClosed()) {
                connection = getConnection();
            }
            
            System.out.println("Executing query for userId: " + userId);
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                // Tạo Profile từ tất cả các cột có sẵn
                profile = Profile.builder()
                        .id(resultSet.getInt("id"))
                        .userId(resultSet.getInt("user_id"))
                        .name(resultSet.getString("name") != null ? resultSet.getString("name") : "")
                        .email(resultSet.getString("email") != null ? resultSet.getString("email") : "")
                        .username(resultSet.getString("username") != null ? resultSet.getString("username") : "")
                        .roleName(resultSet.getString("role_name") != null ? resultSet.getString("role_name") : "customer")
                        .createdAt(resultSet.getTimestamp("created_at"))
                        .updatedAt(resultSet.getTimestamp("updated_at"))
                        // Lấy các trường phone, address, avatar, birthdate
                        .phone(resultSet.getString("phone"))
                        .address(resultSet.getString("address"))
                        .avatar(resultSet.getString("avatar"))
                        .birthdate(resultSet.getDate("birthdate"))
                        .build();
                
                System.out.println("Profile found for userId: " + userId + 
                                 " - Name: " + profile.getName() + 
                                 ", Email: " + profile.getEmail() +
                                 ", Phone: " + (profile.getPhone() != null ? profile.getPhone() : "NULL") +
                                 ", Address: " + (profile.getAddress() != null ? profile.getAddress() : "NULL") +
                                 ", Birthdate: " + (profile.getBirthdate() != null ? profile.getBirthdate().toString() : "NULL") +
                                 ", Role: " + profile.getRoleName());
            } else {
                System.out.println("No profile found for userId: " + userId + " - User does not exist in database");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProfileDAO.class.getName()).log(Level.SEVERE, "Error getting profile by userId: " + userId, ex);
            System.err.println("SQL Error in getProfileByUserId: " + ex.getMessage());
            System.err.println("SQL State: " + ex.getSQLState());
            System.err.println("SQL Query: " + sql);
            ex.printStackTrace();
        } finally {
            closeResources();
        }
        
        return profile;
    }
    
    
    /**
     * Tạo profile mới (giờ chỉ update vào users vì user đã tồn tại)
     */
    public boolean createProfile(Profile profile) {
        // User đã tồn tại, chỉ cần update
        return saveProfile(profile);
    }
    
    /**
     * Cập nhật profile (giờ update vào users)
     */
    public boolean updateProfile(Profile profile) {
        // Giống saveProfile
        return saveProfile(profile);
    }
    
    /**
     * Cập nhật profile vào bảng users
     * Update tất cả các cột: full_name, email, phone_number, address, avatar, birthdate
     */
    public boolean saveProfile(Profile profile) {
        boolean success = false;
        // Update tất cả các cột có trong bảng users
        String sql = "UPDATE users SET " +
                     "full_name = ?, " +
                     "email = ?, " +
                     "phone_number = ?, " +
                     "address = ?, " +
                     "avatar = ?, " +
                     "birthdate = ? " +
                     "WHERE id = ?";
        
        try {
            System.out.println("saveProfile called for userId: " + profile.getUserId());
            
            if (connection == null || connection.isClosed()) {
                connection = getConnection();
            }
            statement = connection.prepareStatement(sql);
            
            statement.setString(1, profile.getName());
            statement.setString(2, profile.getEmail());
            
            // Phone - có thể null
            if (profile.getPhone() != null && !profile.getPhone().trim().isEmpty()) {
                statement.setString(3, profile.getPhone().trim());
            } else {
                statement.setString(3, null);
            }
            
            // Address - có thể null
            if (profile.getAddress() != null && !profile.getAddress().trim().isEmpty()) {
                statement.setString(4, profile.getAddress().trim());
            } else {
                statement.setString(4, null);
            }
            
            // Avatar - có thể null
            if (profile.getAvatar() != null && !profile.getAvatar().trim().isEmpty()) {
                statement.setString(5, profile.getAvatar().trim());
            } else {
                statement.setString(5, null);
            }
            
            // Birthdate - có thể null
            if (profile.getBirthdate() != null) {
                statement.setDate(6, profile.getBirthdate());
            } else {
                statement.setDate(6, null);
            }
            
            statement.setInt(7, profile.getUserId());
            
            System.out.println("Executing UPDATE users for userId: " + profile.getUserId() + 
                             " - Name: " + profile.getName() + 
                             ", Email: " + profile.getEmail() +
                             ", Phone: " + (profile.getPhone() != null ? profile.getPhone() : "NULL") +
                             ", Address: " + (profile.getAddress() != null ? profile.getAddress() : "NULL") +
                             ", Birthdate: " + (profile.getBirthdate() != null ? profile.getBirthdate().toString() : "NULL"));
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            success = rowsAffected > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(ProfileDAO.class.getName()).log(Level.SEVERE, "Error saving profile for userId: " + profile.getUserId(), ex);
            System.err.println("SQL Error in saveProfile: " + ex.getMessage());
            System.err.println("SQL State: " + ex.getSQLState());
            System.err.println("SQL Query: " + sql);
            ex.printStackTrace();
        } finally {
            closeResources();
        }
        
        return success;
    }
    
}

