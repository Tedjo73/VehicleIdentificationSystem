package com.vis.dao;

import com.vis.model.Insurance;
import com.vis.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsuranceDAO {

    public List<Insurance> getAllInsurance() throws SQLException {
        List<Insurance> list = new ArrayList<>();
        String sql = "SELECT * FROM insurance ORDER BY insurance_id";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void insertInsurance(Insurance ins) throws SQLException {
        String sql = "INSERT INTO insurance (vehicle_id, provider_name, policy_number, start_date, end_date) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, ins.getVehicleId());
            ps.setString(2, ins.getProviderName());
            ps.setString(3, ins.getPolicyNumber());
            ps.setDate(4, Date.valueOf(ins.getStartDate()));
            ps.setDate(5, Date.valueOf(ins.getEndDate()));
            ps.executeUpdate();
        }
    }

    public void updateInsurance(Insurance ins) throws SQLException {
        String sql = "UPDATE insurance SET vehicle_id=?, provider_name=?, policy_number=?, start_date=?, end_date=? WHERE insurance_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, ins.getVehicleId());
            ps.setString(2, ins.getProviderName());
            ps.setString(3, ins.getPolicyNumber());
            ps.setDate(4, Date.valueOf(ins.getStartDate()));
            ps.setDate(5, Date.valueOf(ins.getEndDate()));
            ps.setInt(6, ins.getInsuranceId());
            ps.executeUpdate();
        }
    }

    public void deleteInsurance(int id) throws SQLException {
        String sql = "DELETE FROM insurance WHERE insurance_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Insurance mapRow(ResultSet rs) throws SQLException {
        return new Insurance(
            rs.getInt("insurance_id"),
            rs.getInt("vehicle_id"),
            rs.getString("provider_name"),
            rs.getString("policy_number"),
            rs.getDate("start_date") != null ? rs.getDate("start_date").toLocalDate() : null,
            rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null
        );
    }
}
