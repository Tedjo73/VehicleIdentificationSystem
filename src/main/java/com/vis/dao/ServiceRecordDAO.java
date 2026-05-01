package com.vis.dao;

import com.vis.model.ServiceRecord;
import com.vis.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceRecordDAO {

    public List<ServiceRecord> getAllRecords() throws SQLException {
        List<ServiceRecord> list = new ArrayList<>();
        String sql = "SELECT sr.*, v.registration_number FROM servicerecord sr " +
                "LEFT JOIN vehicle v ON sr.vehicle_id = v.vehicle_id ORDER BY sr.service_id";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void insertRecord(ServiceRecord r) throws SQLException {
        String sql = "INSERT INTO servicerecord (vehicle_id, service_date, service_type, description, cost) " +
                "VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, r.getVehicleId());
            ps.setDate(2, parseDate(r.getServiceDate()));
            ps.setString(3, r.getServiceType());
            ps.setString(4, r.getDescription());
            ps.setDouble(5, r.getCost());
            ps.executeUpdate();
        }
    }

    public void deleteRecord(int serviceId) throws SQLException {
        String sql = "DELETE FROM servicerecord WHERE service_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            ps.executeUpdate();
        }
    }

    private ServiceRecord mapRow(ResultSet rs) throws SQLException {
        return new ServiceRecord(
                rs.getInt("service_id"),
                rs.getInt("vehicle_id"),
                rs.getString("service_date"),
                rs.getString("service_type"),
                rs.getString("description"),
                rs.getDouble("cost"),
                rs.getString("registration_number") != null ? rs.getString("registration_number") : ""
        );
    }

    private java.sql.Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank())
            throw new IllegalArgumentException("Date cannot be empty. Use format YYYY-MM-DD.");
        try {
            return java.sql.Date.valueOf(LocalDate.parse(dateStr.trim()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: \"" + dateStr + "\". Use YYYY-MM-DD (e.g. 2025-04-26).");
        }
    }
}