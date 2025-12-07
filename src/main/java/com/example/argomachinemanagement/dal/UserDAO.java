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
        return 0;
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
                .build();
    }

    @Override
    public User findById(Integer id) {
        return null;
    }

}

