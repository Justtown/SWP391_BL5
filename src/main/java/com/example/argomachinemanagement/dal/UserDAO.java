package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.dal.DBContext;
import com.example.argomachinemanagement.dal.I_DAO;
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
        return List.of();
    }

    @Override
    public Map<Integer, User> findAllMap() {
        return Map.of();
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public boolean delete(User user) {
        return false;
    }

    @Override
    public int insert(User user) {
        String sql = "INSERT INTO users (username, password, full_name, email, status, phone_number, address, birthdate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int generatedId = 0;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUsername());
            statement.setString(2, MD5PasswordEncoderUtils.encodeMD5(user.getPassword()));
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getEmail());
            statement.setInt(5, user.getStatus() != null ? user.getStatus() : 1);
            statement.setString(6, user.getPhoneNumber());
            statement.setString(7, user.getAddress());
            statement.setDate(8, user.getBirthdate());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    generatedId = resultSet.getInt(1);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in insert: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return generatedId;
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
        return User.builder()
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
    }

    @Override
    public User findById(Integer id) {
        return null;
    }

}

