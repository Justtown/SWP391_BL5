
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.argomachinemanagement.src.dal;


import com.mycompany.argomachinemanagement.src.entity.Account;
import com.mycompany.argomachinemanagement.src.dto.UserDTO;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author ADMIN
 */
public class AccountDAO extends DBContext implements I_DAO<Account> {

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
    public List<UserDTO> findAllUsersWithRole() {
        List<UserDTO> users = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.full_name, u.email, u.status, r.role_name "
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
                    UserDTO user = UserDTO.builder()
                        .id(resultSet.getInt("id"))
                        .username(resultSet.getString("username"))
                        .fullName(resultSet.getString("full_name"))
                        .email(resultSet.getString("email"))
                        .roleName(resultSet.getString("role_name") != null ? resultSet.getString("role_name") : "No Role")
                        .status(resultSet.getInt("status"))
                        .build();
                    users.add(user);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in findAllUsersWithRole: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeResources();
        }
        return users;
    }
    
    /**
     * Search users by keyword (search in username, full_name, email)
     */
    public List<UserDTO> searchUsers(String keyword) {
        List<UserDTO> users = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.full_name, u.email, u.status, r.role_name "
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
                    UserDTO user = UserDTO.builder()
                        .id(resultSet.getInt("id"))
                        .username(resultSet.getString("username"))
                        .fullName(resultSet.getString("full_name"))
                        .email(resultSet.getString("email"))
                        .roleName(resultSet.getString("role_name") != null ? resultSet.getString("role_name") : "No Role")
                        .status(resultSet.getInt("status"))
                        .build();
                    users.add(user);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in searchUsers: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeResources();
        }
        return users;
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
    public int insert(Account t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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

    

}
