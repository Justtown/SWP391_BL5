package com.mycompany.argomachinemanagement.src.dal;

import com.mycompany.argomachinemanagement.src.entity.Account;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO extends DBContext {

    private Account mapRow(ResultSet rs) throws SQLException {
        Account.AccountBuilder builder = Account.builder()
                .id(rs.getInt("id"))
                .username(rs.getString("username"))
                .password(rs.getString("password"))
                .fullName(rs.getString("full_name"))
                .email(rs.getString("email"))
                .phone(rs.getString("phone"))
                .address(rs.getString("address"))
                .avatar(rs.getString("avatar"));

        int genderVal = rs.getInt("gender");
        if (!rs.wasNull()) {
            builder.gender(genderVal == 1);
        }

        int activeVal = rs.getInt("is_active");
        if (!rs.wasNull()) {
            builder.isActive(activeVal == 1);
        }

        Date bd = rs.getDate("birthdate");
        if (bd != null) {
            builder.birthdate(bd.toLocalDate());
        }

        int roleId = rs.getInt("role_id");
        if (!rs.wasNull()) {
            builder.roleId(roleId);
        }

        String roleName = rs.getString("role_name");
        if (roleName != null) {
            builder.roleName(roleName);
        }

        return builder.build();
    }

    public Account findById(Integer id) {
        String sql = "SELECT u.*, r.id AS role_id, r.role_name " +
                "FROM users u " +
                "LEFT JOIN user_role ur ON u.id = ur.user_id " +
                "LEFT JOIN roles r ON ur.role_id = r.id " +
                "WHERE u.id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateProfile(Account account) {
        String sql = "UPDATE users " +
                "SET full_name = ?, email = ?, phone = ?, address = ?, avatar = ?, birthdate = ? " +
                "WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, account.getFullName());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.setString(4, account.getAddress());
            ps.setString(5, account.getAvatar());

            if (account.getBirthdate() != null) {
                ps.setDate(6, Date.valueOf(account.getBirthdate()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }

            ps.setInt(7, account.getId());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
