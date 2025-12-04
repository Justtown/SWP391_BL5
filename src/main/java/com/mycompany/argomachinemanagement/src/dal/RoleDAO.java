package com.mycompany.argomachinemanagement.src.dal;
import com.mycompany.argomachinemanagement.src.entity.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO extends DBContext {

    public List<Role> findAll(String keyword) {
        List<Role> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM role WHERE role_name LIKE ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + keyword + "%");
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Role r = new Role();
                r.setRoleId(resultSet.getInt("role_id"));
                r.setRoleName(resultSet.getString("role_name"));
                r.setDescription(resultSet.getString("description"));
                r.setStatus(resultSet.getBoolean("status"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return list;
    }

    public Role findById(int id) {
        Role r = null;
        try {
            String sql = "SELECT * FROM role WHERE role_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                r = new Role();
                r.setRoleId(resultSet.getInt("role_id"));
                r.setRoleName(resultSet.getString("role_name"));
                r.setDescription(resultSet.getString("description"));
                r.setStatus(resultSet.getBoolean("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return r;
    }

    public void update(Role role) {
        try {
            String sql = "UPDATE role SET role_name=?, description=?, status=? WHERE role_id=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, role.getRoleName());
            statement.setString(2, role.getDescription());
            statement.setBoolean(3, role.isStatus());
            statement.setInt(4, role.getRoleId());
            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }
}