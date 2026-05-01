package com.vis.dao;

import com.vis.model.PoliceReport;
import com.vis.model.Violation;
import com.vis.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PoliceReportDAO {

    public List<PoliceReport> getAllReports() throws SQLException {
        List<PoliceReport> list = new ArrayList<>();
        String sql = "SELECT pr.*, v.registration_number FROM policereport pr " +
                "LEFT JOIN vehicle v ON pr.vehicle_id = v.vehicle_id ORDER BY pr.report_id";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapReport(rs));
        }
        return list;
    }

    public void insertReport(PoliceReport r) throws SQLException {
        String sql = "INSERT INTO policereport (vehicle_id, report_date, report_type, description, officer_name) " +
                "VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, r.getVehicleId());
            ps.setDate(2, parseDate(r.getReportDate()));
            ps.setString(3, r.getReportType());
            ps.setString(4, r.getDescription());
            ps.setString(5, r.getOfficerName());
            ps.executeUpdate();
        }
    }

    public void deleteReport(int reportId) throws SQLException {
        String sql = "DELETE FROM policereport WHERE report_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, reportId);
            ps.executeUpdate();
        }
    }

    public List<Violation> getAllViolations() throws SQLException {
        List<Violation> list = new ArrayList<>();
        String sql = "SELECT vl.*, v.registration_number FROM violation vl " +
                "LEFT JOIN vehicle v ON vl.vehicle_id = v.vehicle_id ORDER BY vl.violation_id";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapViolation(rs));
        }
        return list;
    }

    public void insertViolation(Violation v) throws SQLException {
        String sql = "INSERT INTO violation (vehicle_id, violation_date, violation_type, fine_amount, status) " +
                "VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, v.getVehicleId());
            ps.setDate(2, parseDate(v.getViolationDate()));
            ps.setString(3, v.getViolationType());
            ps.setDouble(4, v.getFineAmount());
            ps.setString(5, v.getStatus());
            ps.executeUpdate();
        }
    }

    public void updateViolationStatus(int violationId, String status) throws SQLException {
        String sql = "UPDATE violation SET status=? WHERE violation_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, violationId);
            ps.executeUpdate();
        }
    }

    private PoliceReport mapReport(ResultSet rs) throws SQLException {
        return new PoliceReport(
                rs.getInt("report_id"),
                rs.getInt("vehicle_id"),
                rs.getString("report_date"),
                rs.getString("report_type"),
                rs.getString("description"),
                rs.getString("officer_name"),
                rs.getString("registration_number") != null ? rs.getString("registration_number") : ""
        );
    }

    private Violation mapViolation(ResultSet rs) throws SQLException {
        return new Violation(
                rs.getInt("violation_id"),
                rs.getInt("vehicle_id"),
                rs.getString("violation_date"),
                rs.getString("violation_type"),
                rs.getDouble("fine_amount"),
                rs.getString("status"),
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