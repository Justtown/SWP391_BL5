package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO extends DBContext {

    public int count(String keyword) {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM roles WHERE role_name LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + (keyword == null ? "" : keyword) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) total = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("RoleDAO.count error:");
            e.printStackTrace();
        }
        System.out.println("RoleDAO.count -> total = " + total);
        return total;
    }

    public List<Role> findByPage(String keyword, int offset, int limit) {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT id, role_name, description, status FROM roles WHERE role_name LIKE ? ORDER BY id LIMIT ?, ?";
        System.out.println("RoleDAO.findByPage SQL: " + sql + " | params: [" + keyword + "," + offset + "," + limit + "]");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + (keyword == null ? "" : keyword) + "%");
            ps.setInt(2, offset);
            ps.setInt(3, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Role r = new Role();
                    r.setRoleId(rs.getInt("id"));
                    r.setRoleName(rs.getString("role_name"));
                    r.setDescription(rs.getString("description"));
                    r.setStatus(rs.getBoolean("status"));
                    list.add(r);
                }
            }
        } catch (Exception e) {
            System.out.println("RoleDAO.findByPage error:");
            e.printStackTrace();
        }
        System.out.println("RoleDAO.findByPage -> returned " + list.size() + " rows");
        return list;
    }

    public Role findById(int id) {
        Role r = null;
        String sql = "SELECT id, role_name, description, status FROM roles WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    r = new Role();
                    r.setRoleId(rs.getInt("id"));
                    r.setRoleName(rs.getString("role_name"));
                    r.setDescription(rs.getString("description"));
                    r.setStatus(rs.getBoolean("status"));
                }
            }
        } catch (Exception e) {
            System.out.println("RoleDAO.findById error:");
            e.printStackTrace();
        }
        System.out.println("RoleDAO.findById(" + id + ") -> " + (r == null ? "null" : "found"));
        return r;
    }

    public void update(Role role) {
        String sql = "UPDATE roles SET role_name = ?, description = ?, status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.getRoleName());
            ps.setString(2, role.getDescription());
            ps.setBoolean(3, role.isStatus());
            ps.setInt(4, role.getRoleId());
            int updated = ps.executeUpdate();
            System.out.println("RoleDAO.update -> updated rows = " + updated);
        } catch (Exception e) {
            System.out.println("RoleDAO.update error:");
            e.printStackTrace();
        }
    }
}
