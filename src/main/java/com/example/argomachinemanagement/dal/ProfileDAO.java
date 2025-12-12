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
        // Query từ bảng users thay vì profiles
        String sql = "SELECT u.id, u.id as user_id, u.full_name as name, u.email, " +
                     "u.phone_number as phone, u.address, u.avatar, u.birthdate, " +
                     "u.created_at, u.created_at as updated_at, " +
                     "u.username, r.role_name " +
                     "FROM users u " +
                     "LEFT JOIN user_role ur ON u.id = ur.user_id " +
                     "LEFT JOIN roles r ON ur.role_id = r.id " +
                     "WHERE u.id = ?";
        
        try {
            if (connection == null || connection.isClosed()) {
                connection = getConnection();
            }
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                profile = getFromResultSet(resultSet);
                System.out.println("Profile found for userId: " + userId);
            } else {
                System.out.println("No profile found for userId: " + userId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProfileDAO.class.getName()).log(Level.SEVERE, "Error getting profile by userId: " + userId, ex);
            System.err.println("SQL Error in getProfileByUserId: " + ex.getMessage());
            System.err.println("SQL State: " + ex.getSQLState());
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

    public boolean isEmailExist(String email, Integer userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND id != ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // Nếu có bản ghi trùng thì trả về true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPhoneExist(String phone, Integer userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE phone_number = ? AND id != ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // Nếu có bản ghi trùng thì trả về true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật profile vào bảng users
     */
    public boolean saveProfile(Profile profile) {
        boolean success = false;
        // Update trực tiếp vào bảng users
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
            
            // Xử lý phone: nếu null hoặc empty thì set null trong DB
            if (profile.getPhone() != null && !profile.getPhone().trim().isEmpty()) {
                statement.setString(3, profile.getPhone());
            } else {
                statement.setString(3, null);
            }
            
            // Xử lý address: nếu null hoặc empty thì set null trong DB
            if (profile.getAddress() != null && !profile.getAddress().trim().isEmpty()) {
                statement.setString(4, profile.getAddress());
            } else {
                statement.setString(4, null);
            }
            
            // Xử lý avatar: nếu null hoặc empty thì set null trong DB
            if (profile.getAvatar() != null && !profile.getAvatar().trim().isEmpty()) {
                statement.setString(5, profile.getAvatar());
            } else {
                statement.setString(5, null);
            }
            
            if (profile.getBirthdate() != null) {
                statement.setDate(6, profile.getBirthdate());
            } else {
                statement.setDate(6, null);
            }
            
            statement.setInt(7, profile.getUserId());
            
            System.out.println("Executing UPDATE users for userId: " + profile.getUserId());
            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            success = rowsAffected > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(ProfileDAO.class.getName()).log(Level.SEVERE, "Error saving profile for userId: " + profile.getUserId(), ex);
            System.err.println("SQL Error in saveProfile: " + ex.getMessage());
            System.err.println("SQL State: " + ex.getSQLState());
            ex.printStackTrace();
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    /**
     * Chuyển đổi ResultSet thành Profile object (từ bảng users)
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

