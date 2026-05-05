package com.vis.controller;

import com.vis.dao.CustomerDAO;
import com.vis.dao.PoliceReportDAO;
import com.vis.dao.ServiceRecordDAO;
import com.vis.dao.VehicleDAO;
import com.vis.model.ServiceRecord;
import com.vis.model.Vehicle;
import com.vis.model.Violation;
import com.vis.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Button;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ProgressIndicator progressSpinner;
    @FXML private Button liveBtn;

    // Stat badges
    @FXML private Label statVehicles;
    @FXML private Label statServices;
    @FXML private Label statCustomers;
    @FXML private Label statViolations;
    @FXML private Label statInsurance;

    // Dashboard charts
    @FXML private PieChart  dashVehicleChart;
    @FXML private BarChart<String, Number> dashWorkshopChart;
    @FXML private PieChart  dashCustomerChart;
    @FXML private PieChart  dashPoliceChart;

    private final VehicleDAO      vehicleDAO  = new VehicleDAO();
    private final ServiceRecordDAO serviceDAO  = new ServiceRecordDAO();
    private final CustomerDAO      customerDAO = new CustomerDAO();
    private final PoliceReportDAO  policeDAO   = new PoliceReportDAO();

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome to Vehicle Identification System");
        setupAnimations();
        loadStats();
    }

    private void setupAnimations() {
        if (liveBtn != null) {
            FadeTransition ft = new FadeTransition(Duration.millis(1000), liveBtn);
            ft.setFromValue(1.0);
            ft.setToValue(0.3);
            ft.setCycleCount(FadeTransition.INDEFINITE);
            ft.setAutoReverse(true);
            ft.play();
        }
    }

    private void loadStats() {
        try {
            // ── Vehicles ────────────────────────────────────────────
            List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
            statVehicles.setText(String.valueOf(vehicles.size()));

            Map<String, Long> byMake = vehicles.stream()
                .collect(Collectors.groupingBy(
                    v -> v.getMake() == null || v.getMake().isBlank() ? "Unknown" : v.getMake(),
                    Collectors.counting()));
            byMake.forEach((make, cnt) ->
                dashVehicleChart.getData().add(new PieChart.Data(make, cnt)));

            // ── Workshop ─────────────────────────────────────────────
            List<ServiceRecord> services = serviceDAO.getAllRecords();
            statServices.setText(String.valueOf(services.size()));

            XYChart.Series<String, Number> wSeries = new XYChart.Series<>();
            Map<String, Double> byCost = services.stream()
                .collect(Collectors.groupingBy(
                    r -> r.getServiceType() == null || r.getServiceType().isBlank() ? "Other" : r.getServiceType(),
                    Collectors.summingDouble(ServiceRecord::getCost)));
            byCost.forEach((type, cost) ->
                wSeries.getData().add(new XYChart.Data<>(type, cost)));
            dashWorkshopChart.getData().add(wSeries);

            // ── Customers ────────────────────────────────────────────
            var customers = customerDAO.getAllCustomers();
            statCustomers.setText(String.valueOf(customers.size()));

            Map<String, Long> byDomain = customers.stream()
                .collect(Collectors.groupingBy(c -> {
                    String email = c.getEmail();
                    if (email != null && email.contains("@"))
                        return email.substring(email.indexOf("@") + 1);
                    return "Other";
                }, Collectors.counting()));
            byDomain.forEach((domain, cnt) ->
                dashCustomerChart.getData().add(new PieChart.Data(domain, cnt)));

            // ── Police violations ────────────────────────────────────
            List<Violation> violations = policeDAO.getAllViolations();
            statViolations.setText(String.valueOf(violations.size()));

            Map<String, Long> byStatus = violations.stream()
                .collect(Collectors.groupingBy(
                    v -> v.getStatus() == null || v.getStatus().isBlank() ? "Unknown" : v.getStatus(),
                    Collectors.counting()));
            byStatus.forEach((status, cnt) ->
                dashPoliceChart.getData().add(new PieChart.Data(status, cnt)));

            // ── Insurance ────────────────────────────────────────────
            statInsurance.setText(String.valueOf(new com.vis.dao.InsuranceDAO().getAllInsurance().size()));
            
            if (progressSpinner != null) progressSpinner.setVisible(false);

        } catch (Exception e) {
            // Silent fail — charts just stay empty if DB is unreachable
            e.printStackTrace();
        }
    }

    @FXML private void goToWorkshop()  throws Exception { SceneManager.switchTo("Workshop.fxml"); }
    @FXML private void goToCustomer()  throws Exception { SceneManager.switchTo("Customer.fxml"); }
    @FXML private void goToPolice()    throws Exception { SceneManager.switchTo("Police.fxml"); }
    @FXML private void goToVehicles()  throws Exception { SceneManager.switchTo("Vehicle.fxml"); }
    @FXML private void goToInsurance() throws Exception { SceneManager.switchTo("Insurance.fxml"); }
    @FXML private void handleLogout()  throws Exception { SceneManager.switchTo("Login.fxml"); }
    @FXML private void handleExit()    { Platform.exit(); }
}
