package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO extends DBContext {

    public List<Role> getAllRoles() {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT * FROM roles";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Role r = new Role();
                r.setRoleId(rs.getInt("id"));
                r.setRoleName(rs.getString("role_name"));
                r.setDescription(rs.getString("description"));
                r.setStatus(rs.getInt("status") == 1);
                r.setDefaultUrl(rs.getString("default_url"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Role getRoleById(int id) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Role r = new Role();
                r.setRoleId(rs.getInt("id"));
                r.setRoleName(rs.getString("role_name"));
                r.setDescription(rs.getString("description"));
                r.setStatus(rs.getInt("status") == 1);
                r.setDefaultUrl(rs.getString("default_url"));
                return r;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateRole(Role r) {
        String sql = """
            UPDATE roles
            SET description = ?, status = ?
            WHERE id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, r.getDescription());
            ps.setInt(2, r.isStatus() ? 1 : 0);
            ps.setInt(3, r.getRoleId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

