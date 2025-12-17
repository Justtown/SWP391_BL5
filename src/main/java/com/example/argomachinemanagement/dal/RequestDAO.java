package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.Request;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class RequestDAO extends DBContext {

    /* ================= CREATE ================= */
    public void create(Request r) {
        String sql = """
            INSERT INTO requests(title, description, status, customer_id, created_at)
            VALUES (?, ?, 'PENDING', ?, NOW())
        """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, r.getTitle());
            ps.setString(2, r.getDescription());
            ps.setInt(3, r.getCustomerId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* ================= CUSTOMER ================= */
    public List<Request> getByCustomerId(int customerId) {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT * FROM requests WHERE customer_id=? ORDER BY created_at DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapWithoutUsername(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }



    /* ================= SALE ================= */
    public List<Request> getAll() {
        List<Request> list = new ArrayList<>();
        String sql = """
    SELECT r.*, u.username AS customer_username
    FROM requests r
    JOIN users u ON r.customer_id = u.id
    ORDER BY r.created_at DESC
""";


        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    /* ================= REVIEW ================= */
    public void review(int id, String status, String feedback, int salerId) {
        String sql = """
            UPDATE requests
            SET status=?, feedback=?, saler_id=?, updated_at=NOW()
            WHERE id=?
        """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, feedback);
            ps.setInt(3, salerId);
            ps.setInt(4, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* ================= MAPPER ================= */
    private Request map(ResultSet rs) throws SQLException {
        return Request.builder()
                .id(rs.getInt("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .status(rs.getString("status"))
                .feedback(rs.getString("feedback"))
                .customerId(rs.getInt("customer_id"))
                .customerUsername(rs.getString("customer_username")) // SALE ONLY
                .salerId((Integer) rs.getObject("saler_id"))
                .createdAt(rs.getTimestamp("created_at"))
                .updatedAt(rs.getTimestamp("updated_at"))
                .build();
    }

    private Request mapWithoutUsername(ResultSet rs) throws SQLException {
        return Request.builder()
                .id(rs.getInt("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .status(rs.getString("status"))
                .feedback(rs.getString("feedback"))
                .customerId(rs.getInt("customer_id"))
                .salerId((Integer) rs.getObject("saler_id"))
                .createdAt(rs.getTimestamp("created_at"))
                .updatedAt(rs.getTimestamp("updated_at"))
                .build();
    }

    public Request getById(int id) {
        String sql = """
        SELECT r.*, u.username AS customer_username
        FROM requests r
        JOIN users u ON r.customer_id = u.id
        WHERE r.id=?
    """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}