package com.vis.dao;

import com.vis.model.Vehicle;
import com.vis.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    public List<Vehicle> getAllVehicles() throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT v.*, c.name AS owner_name FROM vehicle v " +
                     "LEFT JOIN customer c ON v.owner_id = c.customer_id ORDER BY v.vehicle_id";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Vehicle getByRegistration(String regNumber) throws SQLException {
        String sql = "SELECT v.*, c.name AS owner_name FROM vehicle v " +
                     "LEFT JOIN customer c ON v.owner_id = c.customer_id " +
                     "WHERE v.registration_number = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, regNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public void insertVehicle(Vehicle v) throws SQLException {
        String sql = "INSERT INTO vehicle (registration_number, make, model, year, owner_id) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, v.getRegistrationNumber());
            ps.setString(2, v.getMake());
            ps.setString(3, v.getModel());
            ps.setInt(4, v.getYear());
            ps.setInt(5, v.getOwnerId());
            ps.executeUpdate();
        }
    }

    public void updateVehicle(Vehicle v) throws SQLException {
        String sql = "UPDATE vehicle SET registration_number=?, make=?, model=?, year=?, owner_id=? " +
                     "WHERE vehicle_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, v.getRegistrationNumber());
            ps.setString(2, v.getMake());
            ps.setString(3, v.getModel());
            ps.setInt(4, v.getYear());
            ps.setInt(5, v.getOwnerId());
            ps.setInt(6, v.getVehicleId());
            ps.executeUpdate();
        }
    }

    public void deleteVehicle(int vehicleId) throws SQLException {
        String sql = "DELETE FROM vehicle WHERE vehicle_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.executeUpdate();
        }
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        return new Vehicle(
            rs.getInt("vehicle_id"),
            rs.getString("registration_number"),
            rs.getString("make"),
            rs.getString("model"),
            rs.getInt("year"),
            rs.getInt("owner_id"),
            rs.getString("owner_name") != null ? rs.getString("owner_name") : "Unknown"
        );
    }
}
