package com.mycompany.argomachinemanagement.src.dal;

import com.mycompany.argomachinemanagement.src.entity.User;
import com.mycompany.argomachinemanagement.src.util.MD5PasswordEncoderUtils;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;


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
    public List<User> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean update(User t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean delete(User t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int insert(User t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

