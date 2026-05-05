package com.vis.dao;

import com.vis.model.CustomerQuery;
import com.vis.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerQueryDAO {

    public List<CustomerQuery> getAllQueries() throws SQLException {
        List<CustomerQuery> list = new ArrayList<>();
        String sql = "SELECT * FROM customerquery ORDER BY query_id";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void insertQuery(CustomerQuery cq) throws SQLException {
        String sql = "INSERT INTO customerquery (customer_id, vehicle_id, query_date, query_text, response_text) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, cq.getCustomerId());
            ps.setInt(2, cq.getVehicleId());
            ps.setDate(3, Date.valueOf(cq.getQueryDate()));
            ps.setString(4, cq.getQueryText());
            ps.setString(5, cq.getResponseText());
            ps.executeUpdate();
        }
    }

    public void updateQuery(CustomerQuery cq) throws SQLException {
        String sql = "UPDATE customerquery SET customer_id=?, vehicle_id=?, query_date=?, query_text=?, response_text=? WHERE query_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, cq.getCustomerId());
            ps.setInt(2, cq.getVehicleId());
            ps.setDate(3, Date.valueOf(cq.getQueryDate()));
            ps.setString(4, cq.getQueryText());
            ps.setString(5, cq.getResponseText());
            ps.setInt(6, cq.getQueryId());
            ps.executeUpdate();
        }
    }

    public void deleteQuery(int id) throws SQLException {
        String sql = "DELETE FROM customerquery WHERE query_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private CustomerQuery mapRow(ResultSet rs) throws SQLException {
        return new CustomerQuery(
            rs.getInt("query_id"),
            rs.getInt("customer_id"),
            rs.getInt("vehicle_id"),
            rs.getDate("query_date") != null ? rs.getDate("query_date").toLocalDate() : null,
            rs.getString("query_text"),
            rs.getString("response_text")
        );
    }
}
