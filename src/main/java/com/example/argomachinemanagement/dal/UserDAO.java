package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.User;
import com.example.argomachinemanagement.utils.MD5PasswordEncoderUtils;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class UserDAO extends DBContext implements I_DAO<User> {

   
    public User login(String username, String password) {
        User user = null;
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND status = 1";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, MD5PasswordEncoderUtils.encodeMD5(password));
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                user = getFromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            System.out.println("Error in login: " + ex.getMessage());
        } finally {
            closeResources();
        }
       
        return user;
    }


    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name " +
                     "FROM users u " +
                     "LEFT JOIN user_role ur ON u.id = ur.user_id " +
                     "LEFT JOIN roles r ON ur.role_id = r.id " +
                     "ORDER BY u.id";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                User user = getFromResultSet(resultSet);
                // Set roleName if available
                try {
                    String roleName = resultSet.getString("role_name");
                    user.setRoleName(roleName);
                } catch (SQLException e) {
                    user.setRoleName(null);
                }
                users.add(user);
            }
        } catch (SQLException ex) {
            System.out.println("Error in findAll: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return users;
    }

    @Override
    public Map<Integer, User> findAllMap() {
        return Map.of();
    }

    /**
     * Xóa role cũ của user
     */
    private void removeUserRole(int userId) {
        String sql = "DELETE FROM user_role WHERE user_id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error in removeUserRole: " + ex.getMessage());
        } finally {
            closeResources();
        }
    }
    
    @Override
    public boolean update(User user) {
        boolean success = false;
        
        if (user.getId() == null) {
            return false;
        }
        
        String sql = "UPDATE users SET full_name = ?, email = ?, status = ?, " +
                     "phone_number = ?, address = ?, birthdate = ? WHERE id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getEmail());
            statement.setInt(3, user.getStatus() != null ? user.getStatus() : 1);
            statement.setString(4, user.getPhoneNumber());
            statement.setString(5, user.getAddress());
            if (user.getBirthdate() != null) {
                statement.setDate(6, user.getBirthdate());
            } else {
                statement.setDate(6, null);
            }
            statement.setInt(7, user.getId());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Cập nhật role nếu có
                if (user.getRoleName() != null && !user.getRoleName().isEmpty()) {
                    // Xóa role cũ
                    removeUserRole(user.getId());
                    
                    // Thêm role mới
                    Integer roleId = getRoleIdByRoleName(user.getRoleName());
                    if (roleId != null) {
                        assignRoleToUser(user.getId(), roleId);
                    }
                }
                success = true;
            }
        } catch (SQLException ex) {
            System.out.println("Error in update: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return success;
    }

    @Override
    public boolean delete(User user) {
        return false;
    }

    /**
     * Lấy danh sách tất cả roles
     */
    public List<String> getAllRoles() {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT role_name FROM roles ORDER BY id";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                roles.add(resultSet.getString("role_name"));
            }
        } catch (SQLException ex) {
            System.out.println("Error in getAllRoles: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return roles;
    }
    
    /**
     * Lấy role_id từ role_name
     */
    public Integer getRoleIdByRoleName(String roleName) {
        Integer roleId = null;
        String sql = "SELECT id FROM roles WHERE role_name = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, roleName);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                roleId = resultSet.getInt("id");
            }
        } catch (SQLException ex) {
            System.out.println("Error in getRoleIdByRoleName: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return roleId;
    }
    
    /**
     * Tạo username từ email (lấy phần trước @)
     */
    private String generateUsernameFromEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "user" + System.currentTimeMillis();
        }
        String username = email.split("@")[0];
        // Kiểm tra username đã tồn tại chưa
        int counter = 1;
        String finalUsername = username;
        while (isUsernameExists(finalUsername)) {
            finalUsername = username + counter;
            counter++;
        }
        return finalUsername;
    }
    
    /**
     * Kiểm tra username đã tồn tại chưa
     */
    private boolean isUsernameExists(String username) {
        boolean exists = false;
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error in isUsernameExists: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return exists;
    }
    
    /**
     * Gán role cho user
     */
    private void assignRoleToUser(int userId, int roleId) {
        String sql = "INSERT INTO user_role (user_id, role_id) VALUES (?, ?)";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, roleId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error in assignRoleToUser: " + ex.getMessage());
        } finally {
            closeResources();
        }
    }
    
    @Override
    public int insert(User user) {
        int userId = 0;
        
        // Tạo username nếu chưa có
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            user.setUsername(generateUsernameFromEmail(user.getEmail()));
        }
        
        // Hash password
        String hashedPassword = MD5PasswordEncoderUtils.encodeMD5(user.getPassword());
        
        String sql = "INSERT INTO users (username, password, full_name, email, status, phone_number, address, birthdate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUsername());
            statement.setString(2, hashedPassword);
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getEmail());
            statement.setInt(5, user.getStatus() != null ? user.getStatus() : 1);
            statement.setString(6, user.getPhoneNumber());
            statement.setString(7, user.getAddress());
            if (user.getBirthdate() != null) {
                statement.setDate(8, user.getBirthdate());
            } else {
                statement.setDate(8, null);
            }
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Lấy generated key (user id)
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                    generatedKeys.close();
                    
                    // Gán role cho user nếu có roleName
                    if (user.getRoleName() != null && !user.getRoleName().isEmpty()) {
                        Integer roleId = getRoleIdByRoleName(user.getRoleName());
                        if (roleId != null) {
                            assignRoleToUser(userId, roleId);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in insert: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return userId;
    }
    
    public User findByUsername(String username) {
        User user = null;
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                user = getFromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            System.out.println("Error in findByUsername: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return user;
    }
    
    public User findByEmail(String email) {
        User user = null;
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                user = getFromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            System.out.println("Error in findByEmail: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return user;
    }

    @Override
    public User getFromResultSet(ResultSet resultSet) throws SQLException {
        User user = User.builder()
                .id(resultSet.getInt("id"))
                .username(resultSet.getString("username"))
                .password(resultSet.getString("password"))
                .fullName(resultSet.getString("full_name"))
                .email(resultSet.getString("email"))
                .status(resultSet.getInt("status"))
                .createdAt(resultSet.getTimestamp("created_at"))
                .phoneNumber(resultSet.getString("phone_number"))
                .address(resultSet.getString("address"))
                .birthdate(resultSet.getDate("birthdate"))
                .build();
        
        // Set roleName if available
        try {
            String roleName = resultSet.getString("role_name");
            user.setRoleName(roleName);
        } catch (SQLException e) {
            // role_name column might not exist in some queries
            user.setRoleName(null);
        }
        
        return user;
    }

    @Override
    public User findById(Integer id) {
        User user = null;
        String sql = "SELECT u.*, r.role_name " +
                     "FROM users u " +
                     "LEFT JOIN user_role ur ON u.id = ur.user_id " +
                     "LEFT JOIN roles r ON ur.role_id = r.id " +
                     "WHERE u.id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                user = getFromResultSet(resultSet);
            }
        } catch (SQLException ex) {
            System.out.println("Error in findById: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return user;
    }

}

