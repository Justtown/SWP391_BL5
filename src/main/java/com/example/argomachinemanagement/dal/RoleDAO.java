package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.User;
import com.example.argomachinemanagement.entity.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO extends DBContext {
    public int count(String keyword) {
        int total = 0;
        try {
            String sql = "SELECT COUNT(*) " + "FROM roles r " + "LEFT JOIN user_role ur ON r.id = ur.role_id " + "LEFT JOIN users u ON u.id = ur.user_id " + "WHERE r.role_name LIKE ? OR u.username LIKE ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + keyword + "%");
            statement.setString(2, "%" + keyword + "%");
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                total = resultSet.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStmt();
        }
        return total;
    }

    public List<Role> findByPage(String keyword, int offset, int limit) {
        List<Role> list = new ArrayList<>();

        try {
            String sql = "SELECT r.id, r.role_name, r.description, r.status, u.username "
                    + "FROM roles r "
                    + "LEFT JOIN user_role ur ON r.id = ur.role_id "
                    + "LEFT JOIN users u ON u.id = ur.user_id "
                    + "WHERE r.role_name LIKE ? "
                    + "ORDER BY r.id LIMIT ?, ?";

            Connection conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setString(1, "%" + keyword + "%");
            statement.setInt(2, offset);
            statement.setInt(3, limit);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Role r = new Role();
                r.setRoleId(resultSet.getInt("id"));
                r.setRoleName(resultSet.getString("role_name"));
                r.setDescription(resultSet.getString("description"));
                r.setStatus(resultSet.getBoolean("status"));
                r.setUsername(resultSet.getString("username"));
                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStmt();
        }

        return list;
    }

    public Role findById(int id) {
        Role r = null;

        try {
            String sql = "SELECT r.id, r.role_name, r.description, r.status, u.username "
                    + "FROM roles r "
                    + "LEFT JOIN user_role ur ON r.id = ur.role_id "
                    + "LEFT JOIN users u ON u.id = ur.user_id "
                    + "WHERE r.id = ?";

            Connection conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                r = new Role();
                r.setRoleId(resultSet.getInt("id"));
                r.setRoleName(resultSet.getString("role_name"));
                r.setDescription(resultSet.getString("description"));
                r.setStatus(resultSet.getBoolean("status"));
                r.setUsername(resultSet.getString("username")); // ★ now works
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStmt();
        }

        return r;
    }


    public void update(Role role) {
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = "UPDATE roles SET role_name = ?, description = ?, status = ? WHERE id = ?";
            statement = conn.prepareStatement(sql);
            statement.setString(1, role.getRoleName());
            statement.setString(2, role.getDescription());
            statement.setBoolean(3, role.isStatus());
            statement.setInt(4, role.getRoleId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStmt();
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

    private void closeStmt() {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        } catch (Exception ignored) {}
    }
}
