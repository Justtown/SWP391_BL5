
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.argomachinemanagement.src.dal;

import com.mycompany.argomachinemanagement.src.entity.Account;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author ADMIN
 */
public class AccountDAO extends DBContext implements IDao<Account> {

    //Sample
    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try {
            connection = getConnection();
            if (connection != null) {
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    accounts.add(getFromResultSet(resultSet));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in findAll: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeResources();
        }
        return accounts;
    }
    
    /**
     * Get all users with their role names
     */
    public List<Account> findAllUsersWithRole() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.password, u.full_name, u.email, u.status, r.role_name "
                   + "FROM users u "
                   + "LEFT JOIN user_role ur ON u.id = ur.user_id "
                   + "LEFT JOIN roles r ON ur.role_id = r.id "
                   + "ORDER BY u.id";
        try {
            connection = getConnection();
            if (connection != null) {
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Account account = getFromResultSetWithRole(resultSet);
                    accounts.add(account);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in findAllUsersWithRole: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeResources();
        }
        return accounts;
    }
    
    /**
     * Search users by keyword (search in username, full_name, email)
     */
    public List<Account> searchUsers(String keyword) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.password, u.full_name, u.email, u.status, r.role_name "
                   + "FROM users u "
                   + "LEFT JOIN user_role ur ON u.id = ur.user_id "
                   + "LEFT JOIN roles r ON ur.role_id = r.id "
                   + "WHERE u.username LIKE ? OR u.full_name LIKE ? OR u.email LIKE ? "
                   + "ORDER BY u.id";
        try {
            connection = getConnection();
            if (connection != null) {
                statement = connection.prepareStatement(sql);
                String searchPattern = "%" + keyword + "%";
                statement.setString(1, searchPattern);
                statement.setString(2, searchPattern);
                statement.setString(3, searchPattern);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Account account = getFromResultSetWithRole(resultSet);
                    accounts.add(account);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in searchUsers: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeResources();
        }
        return accounts;
    }
    
    /**
     * Get all users with filters (role, status, keyword)
     */
    public List<Account> findUsersWithFilters(String keyword, String roleName, Integer status) {
        List<Account> accounts = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT u.id, u.username, u.password, u.full_name, u.email, u.status, r.role_name "
            + "FROM users u "
            + "LEFT JOIN user_role ur ON u.id = ur.user_id "
            + "LEFT JOIN roles r ON ur.role_id = r.id "
            + "WHERE 1=1 "
        );
        
        int paramIndex = 1;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (u.username LIKE ? OR u.full_name LIKE ? OR u.email LIKE ?) ");
        }
        
        if (roleName != null && !roleName.trim().isEmpty() && !roleName.equals("all")) {
            sql.append("AND r.role_name = ? ");
        }
        
        if (status != null) {
            sql.append("AND u.status = ? ");
        }
        
        sql.append("ORDER BY u.id");
        
        try {
            connection = getConnection();
            if (connection != null) {
                statement = connection.prepareStatement(sql.toString());
                paramIndex = 1;
                
                if (keyword != null && !keyword.trim().isEmpty()) {
                    String searchPattern = "%" + keyword + "%";
                    statement.setString(paramIndex++, searchPattern);
                    statement.setString(paramIndex++, searchPattern);
                    statement.setString(paramIndex++, searchPattern);
                }
                
                if (roleName != null && !roleName.trim().isEmpty() && !roleName.equals("all")) {
                    statement.setString(paramIndex++, roleName);
                }
                
                if (status != null) {
                    statement.setInt(paramIndex++, status);
                }
                
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Account account = getFromResultSetWithRole(resultSet);
                    accounts.add(account);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in findUsersWithFilters: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeResources();
        }
        return accounts;
    }
    
    /**
     * Get all role names for dropdown
     */
    public List<String> getAllRoleNames() {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT DISTINCT role_name FROM roles ORDER BY role_name";
        try {
            connection = getConnection();
            if (connection != null) {
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    roles.add(resultSet.getString("role_name"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in getAllRoleNames: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeResources();
        }
        return roles;
    }


    @Override
    public boolean update(Account t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean delete(Account t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int insert(Account account) {
        int result = 0;
        PreparedStatement roleStatement = null;
        String sql = "INSERT INTO users (username, password, full_name, email, status) VALUES (?, ?, ?, ?, ?)";
        try {
            connection = getConnection();
            if (connection != null) {
                // Disable auto-commit to use transaction
                connection.setAutoCommit(false);
                
                statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, account.getUsername());
                statement.setString(2, account.getPassword()); // In production, should hash password
                statement.setString(3, account.getFullName());
                statement.setString(4, account.getEmail());
                statement.setInt(5, account.getIsActive() != null && account.getIsActive() ? 1 : 0);
                
                int rowsAffected = statement.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Get generated user ID
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        
                        // Insert into user_role table if roleId is provided
                        if (account.getRoleId() != null) {
                            String roleSql = "INSERT INTO user_role (user_id, role_id) VALUES (?, ?)";
                            roleStatement = connection.prepareStatement(roleSql);
                            roleStatement.setInt(1, userId);
                            roleStatement.setInt(2, account.getRoleId());
                            roleStatement.executeUpdate();
                        }
                        
                        // Commit transaction
                        connection.commit();
                        result = userId;
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in insert: " + ex.getMessage());
            ex.printStackTrace();
            // Rollback transaction on error
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.out.println("Error in rollback: " + rollbackEx.getMessage());
            }
            result = 0;
        } finally {
            // Close role statement if exists
            try {
                if (roleStatement != null && !roleStatement.isClosed()) {
                    roleStatement.close();
                }
            } catch (SQLException ex) {
                System.out.println("Error closing role statement: " + ex.getMessage());
            }
            // Re-enable auto-commit
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                System.out.println("Error setting auto-commit: " + ex.getMessage());
            }
            closeResources();
        }
        return result;
    }
    
    /**
     * Find account by username
     */
    public Account findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            connection = getConnection();
            if (connection != null) {
                statement = connection.prepareStatement(sql);
                statement.setString(1, username);
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return getFromResultSet(resultSet);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in findByUsername: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeResources();
        }
        return null;
    }
    
    /**
     * Get role ID by role name
     */
    public Integer getRoleIdByName(String roleName) {
        String sql = "SELECT id FROM roles WHERE role_name = ?";
        try {
            connection = getConnection();
            if (connection != null) {
                statement = connection.prepareStatement(sql);
                statement.setString(1, roleName);
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in getRoleIdByName: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeResources();
        }
        return null;
    }

    @Override
    public Account getFromResultSet(ResultSet resultSet) throws SQLException {
        return Account.builder()
            .id(resultSet.getInt("id"))
            .username(resultSet.getString("username"))
            .password(resultSet.getString("password"))
            .fullName(resultSet.getString("full_name"))
            .email(resultSet.getString("email"))
            .isActive(resultSet.getInt("status") == 1)
            .build();
    }
    
    /**
     * Get Account from ResultSet with role name (for JOIN queries)
     */
    private Account getFromResultSetWithRole(ResultSet resultSet) throws SQLException {
        Account account = Account.builder()
            .id(resultSet.getInt("id"))
            .username(resultSet.getString("username"))
            .password(resultSet.getString("password"))
            .fullName(resultSet.getString("full_name"))
            .email(resultSet.getString("email"))
            .isActive(resultSet.getInt("status") == 1)
            .build();
        // Set role name from JOIN query
        String roleName = resultSet.getString("role_name");
        account.setRoleName(roleName != null ? roleName : "No Role");
        return account;
    }

    

}
