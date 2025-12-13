package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends DBContext {

    public void create(Order o) {
        String sql = "INSERT INTO service_orders " +
                "(contract_code, customer_name, customer_phone, customer_address, " +
                "machine_id, service_description, start_date, end_date, status, total_cost, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, o.getContractCode());
            ps.setString(2, o.getCustomerName());
            ps.setString(3, o.getCustomerPhone());
            ps.setString(4, o.getCustomerAddress());

            if (o.getMachineId() != null) {
                ps.setInt(5, o.getMachineId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            ps.setString(6, o.getServiceDescription());
            ps.setDate(7, o.getStartDate());
            ps.setDate(8, o.getEndDate());
            ps.setString(9, o.getStatus());
            if (o.getTotalCost() != null) {
                ps.setDouble(10, o.getTotalCost());
            } else {
                ps.setNull(10, java.sql.Types.DOUBLE);
            }
            if (o.getCreatedBy() != null) {
                ps.setInt(11, o.getCreatedBy());
            } else {
                ps.setNull(11, java.sql.Types.INTEGER);
            }

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Order> findAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM service_orders ORDER BY created_at DESC";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("id"));
                o.setContractCode(rs.getString("contract_code"));
                o.setCustomerName(rs.getString("customer_name"));
                o.setCustomerPhone(rs.getString("customer_phone"));
                o.setCustomerAddress(rs.getString("customer_address"));
                o.setMachineId((Integer) rs.getObject("machine_id"));
                o.setServiceDescription(rs.getString("service_description"));
                o.setStartDate(rs.getDate("start_date"));
                o.setEndDate(rs.getDate("end_date"));
                o.setStatus(rs.getString("status"));
                o.setTotalCost((Double) rs.getObject("total_cost"));
                o.setCreatedBy((Integer) rs.getObject("created_by"));
                list.add(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}