package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.Profile;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileDAO extends DBContext {
    
    /**
     * Lấy profile theo user_id kèm thông tin role
     */
    public Profile getProfileByUserId(Integer userId) {
        Profile profile = null;
        String sql = "SELECT p.*, u.username, r.role_name " +
                     "FROM profiles p " +
                     "JOIN users u ON p.user_id = u.id " +
                     "LEFT JOIN user_role ur ON u.id = ur.user_id " +
                     "LEFT JOIN roles r ON ur.role_id = r.id " +
                     "WHERE p.user_id = ? AND u.status = 1";
        
        try {
            if (connection == null || connection.isClosed()) {
                connection = getConnection();
            }
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                profile = getFromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProfileDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeResources();
        }
        
        return profile;
    }
    
    /**
     * Tạo profile mới
     */
    public boolean createProfile(Profile profile) {
        boolean success = false;
        String sql = "INSERT INTO profiles (user_id, name, email, phone, address, avatar, birthdate) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try {
            if (connection == null || connection.isClosed()) {
                connection = getConnection();
            }
            statement = connection.prepareStatement(sql);
            statement.setInt(1, profile.getUserId());
            statement.setString(2, profile.getName());
            statement.setString(3, profile.getEmail());
            statement.setString(4, profile.getPhone());
            statement.setString(5, profile.getAddress());
            statement.setString(6, profile.getAvatar());
            if (profile.getBirthdate() != null) {
                statement.setDate(7, profile.getBirthdate());
            } else {
                statement.setDate(7, null);
            }
            
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            Logger.getLogger(ProfileDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    /**
     * Cập nhật profile
     */
    public boolean updateProfile(Profile profile) {
        boolean success = false;
        String sql = "UPDATE profiles SET " +
                     "name = ?, " +
                     "email = ?, " +
                     "phone = ?, " +
                     "address = ?, " +
                     "avatar = ?, " +
                     "birthdate = ?, " +
                     "updated_at = CURRENT_TIMESTAMP " +
                     "WHERE user_id = ?";
        
        try {
            if (connection == null || connection.isClosed()) {
                connection = getConnection();
            }
            statement = connection.prepareStatement(sql);
            statement.setString(1, profile.getName());
            statement.setString(2, profile.getEmail());
            statement.setString(3, profile.getPhone());
            statement.setString(4, profile.getAddress());
            statement.setString(5, profile.getAvatar());
            if (profile.getBirthdate() != null) {
                statement.setDate(6, profile.getBirthdate());
            } else {
                statement.setDate(6, null);
            }
            statement.setInt(7, profile.getUserId());
            
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            Logger.getLogger(ProfileDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    /**
     * Tạo hoặc cập nhật profile (upsert)
     */
    public boolean saveProfile(Profile profile) {
        // Kiểm tra xem profile đã tồn tại chưa
        Profile existing = getProfileByUserId(profile.getUserId());
        
        if (existing != null) {
            // Cập nhật
            return updateProfile(profile);
        } else {
            // Tạo mới
            return createProfile(profile);
        }
    }
    
    /**
     * Chuyển đổi ResultSet thành Profile object
     */
    private Profile getFromResultSet(ResultSet rs) throws SQLException {
        return Profile.builder()
                .id(rs.getInt("id"))
                .userId(rs.getInt("user_id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .phone(rs.getString("phone"))
                .address(rs.getString("address"))
                .avatar(rs.getString("avatar"))
                .birthdate(rs.getDate("birthdate"))
                .createdAt(rs.getTimestamp("created_at"))
                .updatedAt(rs.getTimestamp("updated_at"))
                .username(rs.getString("username"))
                .roleName(rs.getString("role_name"))
                .build();
    }
}

