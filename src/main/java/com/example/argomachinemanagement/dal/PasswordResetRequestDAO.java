package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.PasswordResetRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PasswordResetRequestDAO extends DBContext {
    
    /**
     * Tạo request mới
     */
    public int createRequest(PasswordResetRequest request) {
        int requestId = 0;
        String sql = "INSERT INTO password_reset_requests (user_id, email, status, password_changed) VALUES (?, ?, ?, ?)";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, request.getUserId());
            statement.setString(2, request.getEmail());
            statement.setString(3, request.getStatus() != null ? request.getStatus() : "pending");
            statement.setBoolean(4, request.getPasswordChanged() != null ? request.getPasswordChanged() : false);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    requestId = generatedKeys.getInt(1);
                    generatedKeys.close();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PasswordResetRequestDAO.class.getName()).log(Level.SEVERE, "Error creating password reset request", ex);
            System.err.println("Error in createRequest: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return requestId;
    }
    
    /**
     * Lấy tất cả requests pending
     */
    public List<PasswordResetRequest> getAllPendingRequests() {
        List<PasswordResetRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM password_reset_requests WHERE status = 'pending' ORDER BY request_time DESC";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                requests.add(getFromResultSet(resultSet));
            }
        } catch (SQLException ex) {
            Logger.getLogger(PasswordResetRequestDAO.class.getName()).log(Level.SEVERE, "Error getting pending requests", ex);
            System.err.println("Error in getAllPendingRequests: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return requests;
    }
    
    /**
     * Lấy tất cả requests với search và filter
     */
    public List<PasswordResetRequest> getAllRequests(String keyword, String statusFilter, int offset, int limit) {
        List<PasswordResetRequest> requests = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM password_reset_requests WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // Filter by status
        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("all")) {
            sql.append(" AND status = ?");
            params.add(statusFilter);
        }
        
        // Search by email or user_id
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (email LIKE ? OR user_id = ?)");
            String searchKeyword = "%" + keyword.trim() + "%";
            params.add(searchKeyword);
            try {
                params.add(Integer.parseInt(keyword.trim()));
            } catch (NumberFormatException e) {
                params.add(-1); // Invalid ID
            }
        }
        
        sql.append(" ORDER BY request_time DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                requests.add(getFromResultSet(resultSet));
            }
        } catch (SQLException ex) {
            Logger.getLogger(PasswordResetRequestDAO.class.getName()).log(Level.SEVERE, "Error getting requests", ex);
            System.err.println("Error in getAllRequests: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return requests;
    }
    
    /**
     * Đếm tổng số requests với search và filter
     */
    public int countRequests(String keyword, String statusFilter) {
        int count = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM password_reset_requests WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // Filter by status
        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("all")) {
            sql.append(" AND status = ?");
            params.add(statusFilter);
        }
        
        // Search by email or user_id
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (email LIKE ? OR user_id = ?)");
            String searchKeyword = "%" + keyword.trim() + "%";
            params.add(searchKeyword);
            try {
                params.add(Integer.parseInt(keyword.trim()));
            } catch (NumberFormatException e) {
                params.add(-1);
            }
        }
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PasswordResetRequestDAO.class.getName()).log(Level.SEVERE, "Error counting requests", ex);
            System.err.println("Error in countRequests: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return count;
    }
    
    /**
     * Lấy request theo ID
     */
    public PasswordResetRequest findById(Integer id) {
        PasswordResetRequest request = null;
        String sql = "SELECT * FROM password_reset_requests WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                request = getFromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PasswordResetRequestDAO.class.getName()).log(Level.SEVERE, "Error finding request by id", ex);
            System.err.println("Error in findById: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return request;
    }
    
    /**
     * Kiểm tra user có request approved nhưng chưa đổi mật khẩu chưa
     */
    public PasswordResetRequest findUnchangedApprovedRequest(Integer userId) {
        PasswordResetRequest request = null;
        String sql = "SELECT * FROM password_reset_requests " +
                     "WHERE user_id = ? " +
                     "AND LOWER(status) = 'approved' " +
                     "AND password_changed = 0 " +
                     "ORDER BY request_time DESC LIMIT 1";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                request = getFromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PasswordResetRequestDAO.class.getName()).log(Level.SEVERE, "Error finding unchanged approved request", ex);
            System.err.println("Error in findUnchangedApprovedRequest: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return request;
    }
    
    /**
     * Cập nhật status và new_password của request
     */
    public boolean updateRequestStatus(Integer requestId, String status, String newPassword) {
        boolean success = false;
        String sql = "UPDATE password_reset_requests SET status = ?, new_password = ? WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, status);
            statement.setString(2, newPassword);
            statement.setInt(3, requestId);
            
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            Logger.getLogger(PasswordResetRequestDAO.class.getName()).log(Level.SEVERE, "Error updating request status", ex);
            System.err.println("Error in updateRequestStatus: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    /**
     * Đánh dấu user đã đổi mật khẩu
     */
    public boolean markPasswordAsChanged(Integer userId) {
        boolean success = false;
        String sql = "UPDATE password_reset_requests SET password_changed = TRUE WHERE user_id = ? AND status = 'approved' AND password_changed = FALSE";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            Logger.getLogger(PasswordResetRequestDAO.class.getName()).log(Level.SEVERE, "Error marking password as changed", ex);
            System.err.println("Error in markPasswordAsChanged: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    /**
     * Cập nhật password của user (đã hash)
     */
    public boolean updateUserPassword(Integer userId, String hashedPassword) {
        boolean success = false;
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, hashedPassword);
            statement.setInt(2, userId);
            
            int rowsAffected = statement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            Logger.getLogger(PasswordResetRequestDAO.class.getName()).log(Level.SEVERE, "Error updating user password", ex);
            System.err.println("Error in updateUserPassword: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return success;
    }
    
    /**
     * Chuyển đổi ResultSet thành PasswordResetRequest
     */
    private PasswordResetRequest getFromResultSet(ResultSet rs) throws SQLException {
        return PasswordResetRequest.builder()
                .id(rs.getInt("id"))
                .userId(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .requestTime(rs.getTimestamp("request_time"))
                .status(rs.getString("status"))
                .newPassword(rs.getString("new_password"))
                .passwordChanged(rs.getBoolean("password_changed"))
                .build();
    }
}

