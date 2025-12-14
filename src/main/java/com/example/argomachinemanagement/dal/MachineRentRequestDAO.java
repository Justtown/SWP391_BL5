package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.MachineRentRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MachineRentRequestDAO extends DBContext {

    public void create(MachineRentRequest r) {
        String sql = """
        INSERT INTO machine_rent_requests
        (machine_id, customer_id, start_date, end_date, note, status)
        VALUES (?,?,?,?,?,?)
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, r.getMachineId());
            ps.setInt(2, r.getCustomerId());
            ps.setDate(3, r.getStartDate());
            ps.setDate(4, r.getEndDate());
            ps.setString(5, r.getNote());
            ps.setString(6, "PENDING");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MachineRentRequest> findAll() {
        List<MachineRentRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM machine_rent_requests ORDER BY created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(MachineRentRequest.builder()
                        .id(rs.getInt("id"))
                        .machineId(rs.getInt("machine_id"))
                        .customerId(rs.getInt("customer_id"))
                        .status(rs.getString("status"))
                        .startDate(rs.getDate("start_date"))
                        .endDate(rs.getDate("end_date"))
                        .note(rs.getString("note"))
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateStatus(int id, String status, int reviewerId) {
        String sql = """
            UPDATE machine_rent_requests
            SET status=?, reviewed_by=?, reviewed_at=NOW()
            WHERE id=?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reviewerId);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
