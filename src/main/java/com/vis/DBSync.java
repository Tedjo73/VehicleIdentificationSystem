package com.vis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DBSync {

    private static final String URL = "jdbc:postgresql://localhost:5432/vehicle_identification";
    private static final String USER = "postgres";
    private static final String PASSWORD = "0000";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected to database successfully.");

            // 1. Create Insurance table if it doesn't exist
            String createInsurance = "CREATE TABLE IF NOT EXISTS insurance (" +
                    "insurance_id SERIAL PRIMARY KEY, " +
                    "vehicle_id INT REFERENCES vehicle(vehicle_id) ON DELETE CASCADE, " +
                    "provider_name VARCHAR(150), " +
                    "policy_number VARCHAR(100) UNIQUE, " +
                    "start_date DATE, " +
                    "end_date DATE" +
                    ")";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createInsurance);
                System.out.println("Ensured 'insurance' table exists.");
            }

            // 2. Create CustomerQuery table if it doesn't exist
            String createQuery = "CREATE TABLE IF NOT EXISTS customerquery (" +
                    "query_id SERIAL PRIMARY KEY, " +
                    "customer_id INT REFERENCES customer(customer_id) ON DELETE CASCADE, " +
                    "vehicle_id INT REFERENCES vehicle(vehicle_id) ON DELETE CASCADE, " +
                    "query_date DATE, " +
                    "query_text TEXT, " +
                    "response_text TEXT" +
                    ")";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createQuery);
                System.out.println("Ensured 'customerquery' table exists.");
            }

            // 3. Scan existing vehicles
            List<Integer> vehicleIds = new ArrayList<>();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT vehicle_id FROM vehicle")) {
                while (rs.next()) {
                    vehicleIds.add(rs.getInt("vehicle_id"));
                }
            }
            System.out.println("Found " + vehicleIds.size() + " vehicles in the database.");

            // 4. Add insurance for each vehicle if it doesn't have one
            String checkSql = "SELECT count(*) FROM insurance WHERE vehicle_id = ?";
            String insertSql = "INSERT INTO insurance (vehicle_id, provider_name, policy_number, start_date, end_date) VALUES (?, ?, ?, CURRENT_DATE, CURRENT_DATE + INTERVAL '1 year')";
            
            String[] providers = {"Lesotho National Insurance", "Alliance Insurance", "Metropolitan Lesotho", "LNIG"};
            Random rand = new Random();
            int added = 0;

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                
                for (int vid : vehicleIds) {
                    checkStmt.setInt(1, vid);
                    ResultSet rs = checkStmt.executeQuery();
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        // Needs insurance
                        insertStmt.setInt(1, vid);
                        insertStmt.setString(2, providers[rand.nextInt(providers.length)]);
                        insertStmt.setString(3, "POL-" + (100000 + rand.nextInt(900000)) + "-" + vid);
                        insertStmt.executeUpdate();
                        added++;
                    }
                }
            }
            System.out.println("Successfully generated and added " + added + " new insurance policies.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
