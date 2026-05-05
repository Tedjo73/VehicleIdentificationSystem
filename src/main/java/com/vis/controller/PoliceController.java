package com.vis.controller;

import com.vis.dao.PoliceReportDAO;
import com.vis.dao.VehicleDAO;
import com.vis.model.PoliceReport;
import com.vis.model.Vehicle;
import com.vis.model.Violation;
import com.vis.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PoliceController {

    @FXML private TableView<PoliceReport> reportTable;
    @FXML private TableColumn<PoliceReport, Integer> colRepId;
    @FXML private TableColumn<PoliceReport, String>  colRepReg;
    @FXML private TableColumn<PoliceReport, String>  colRepDate;
    @FXML private TableColumn<PoliceReport, String>  colRepType;
    @FXML private TableColumn<PoliceReport, String>  colRepDesc;
    @FXML private TableColumn<PoliceReport, String>  colRepOfficer;



    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;

    @FXML private BarChart<String, Number> reportChart;
    @FXML private BarChart<String, Number> violationChart;

    private final PoliceReportDAO dao = new PoliceReportDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private List<Vehicle> vehicles;

    @FXML private TableView<Violation> violationTable;
    @FXML private TableColumn<Violation, Integer> colVioId;
    @FXML private TableColumn<Violation, String>  colVioReg;
    @FXML private TableColumn<Violation, String>  colVioDate;
    @FXML private TableColumn<Violation, String>  colVioType;
    @FXML private TableColumn<Violation, Double>  colVioFine;
    @FXML private TableColumn<Violation, String>  colVioStatus;

    @FXML
    public void initialize() {
        colRepId.setCellValueFactory(new PropertyValueFactory<>("reportId"));
        colRepReg.setCellValueFactory(new PropertyValueFactory<>("regNumber"));
        colRepDate.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        colRepType.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        colRepDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colRepOfficer.setCellValueFactory(new PropertyValueFactory<>("officerName"));

        colVioId.setCellValueFactory(new PropertyValueFactory<>("violationId"));
        colVioReg.setCellValueFactory(new PropertyValueFactory<>("regNumber"));
        colVioDate.setCellValueFactory(new PropertyValueFactory<>("violationDate"));
        colVioType.setCellValueFactory(new PropertyValueFactory<>("violationType"));
        colVioFine.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        colVioStatus.setCellValueFactory(new PropertyValueFactory<>("status"));



        loadVehicles();
        loadReports();
        loadViolations();
    }

    private void loadVehicles() {
        try {
            vehicles = vehicleDAO.getAllVehicles();
        } catch (SQLException e) {
            showStatus("Error loading vehicles: " + e.getMessage(), true);
        }
    }

    private void loadReports() {
        try {
            progressBar.setProgress(-1);
            List<PoliceReport> reports = dao.getAllReports().stream()
                .filter(r -> r.getRegNumber() != null && !r.getRegNumber().isBlank())
                .collect(java.util.stream.Collectors.toList());
            reportTable.setItems(FXCollections.observableArrayList(reports));
            updateReportChart(reports);
            progressBar.setProgress(1.0);
        } catch (SQLException e) { showStatus("Error: " + e.getMessage(), true); }
    }

    private void loadViolations() {
        try {
            List<Violation> violations = dao.getAllViolations().stream()
                .filter(v -> v.getRegNumber() != null && !v.getRegNumber().isBlank())
                .collect(java.util.stream.Collectors.toList());
            violationTable.setItems(FXCollections.observableArrayList(violations));
            updateViolationChart(violations);
        } catch (SQLException e) { showStatus("Error: " + e.getMessage(), true); }
    }

    private void updateReportChart(List<PoliceReport> reports) {
        if (reportChart == null) return;
        reportChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reports");
        Map<String, Long> counts = reports.stream().collect(
            Collectors.groupingBy(r -> r.getReportType() == null ? "Unknown" : r.getReportType(), Collectors.counting()));
        counts.forEach((type, count) -> series.getData().add(new XYChart.Data<>(type, count)));
        reportChart.getData().add(series);
    }

    private void updateViolationChart(List<Violation> violations) {
        if (violationChart == null) return;
        violationChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Violations");
        Map<String, Long> counts = violations.stream().collect(
            Collectors.groupingBy(v -> v.getStatus() == null ? "Unknown" : v.getStatus(), Collectors.counting()));
        counts.forEach((status, count) -> series.getData().add(new XYChart.Data<>(status, count)));
        violationChart.getData().add(series);
    }

    @FXML private void handleAddReport() {
        Dialog<PoliceReport> dialog = new Dialog<>();
        dialog.setTitle("Add Police Report");
        dialog.setHeaderText(null);

        ComboBox<String> vCombo = new ComboBox<>();
        if (vehicles != null) {
            ObservableList<String> items = FXCollections.observableArrayList();
            vehicles.forEach(v -> items.add(v.getVehicleId() + " - " + v.getRegistrationNumber()));
            vCombo.setItems(items);
        }
        
        TextField dField = new TextField(); dField.setPromptText("YYYY-MM-DD");
        ComboBox<String> tCombo = new ComboBox<>(FXCollections.observableArrayList("Accident", "Theft", "Assault", "Other"));
        TextField oField = new TextField();
        TextArea dArea = new TextArea(); dArea.setPrefHeight(55);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Vehicle:"), 0, 0); grid.add(vCombo, 1, 0);
        grid.add(new Label("Date:"), 0, 1); grid.add(dField, 1, 1);
        grid.add(new Label("Type:"), 0, 2); grid.add(tCombo, 1, 2);
        grid.add(new Label("Officer:"), 0, 3); grid.add(oField, 1, 3);
        grid.add(new Label("Description:"), 0, 4); grid.add(dArea, 1, 4);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                try {
                    int vid = Integer.parseInt(vCombo.getValue().split(" - ")[0]);
                    return new PoliceReport(0, vid, dField.getText().trim(),
                        tCombo.getValue(), dArea.getText().trim(), oField.getText().trim(), "");
                } catch(Exception e) { return null; }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(r -> {
            try {
                dao.insertReport(r);
                showStatus("Report added.", false);
                loadReports();
            } catch (Exception e) { showStatus("Error: " + e.getMessage(), true); }
        });
    }

    @FXML private void handleAddViolation() {
        Dialog<Violation> dialog = new Dialog<>();
        dialog.setTitle("Add Violation");
        dialog.setHeaderText(null);

        ComboBox<String> vCombo = new ComboBox<>();
        if (vehicles != null) {
            ObservableList<String> items = FXCollections.observableArrayList();
            vehicles.forEach(v -> items.add(v.getVehicleId() + " - " + v.getRegistrationNumber()));
            vCombo.setItems(items);
        }
        
        TextField dField = new TextField(); dField.setPromptText("YYYY-MM-DD");
        TextField tField = new TextField();
        TextField fField = new TextField();
        ComboBox<String> sCombo = new ComboBox<>(FXCollections.observableArrayList("Paid", "Unpaid"));
        sCombo.setValue("Unpaid");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Vehicle:"), 0, 0); grid.add(vCombo, 1, 0);
        grid.add(new Label("Date:"), 0, 1); grid.add(dField, 1, 1);
        grid.add(new Label("Type:"), 0, 2); grid.add(tField, 1, 2);
        grid.add(new Label("Fine (M):"), 0, 3); grid.add(fField, 1, 3);
        grid.add(new Label("Status:"), 0, 4); grid.add(sCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                try {
                    int vid = Integer.parseInt(vCombo.getValue().split(" - ")[0]);
                    return new Violation(0, vid, dField.getText().trim(),
                        tField.getText().trim(), Double.parseDouble(fField.getText().trim()),
                        sCombo.getValue(), "");
                } catch(Exception e) { return null; }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(v -> {
            try {
                dao.insertViolation(v);
                showStatus("Violation added.", false);
                loadViolations();
            } catch (Exception e) { showStatus("Error: " + e.getMessage(), true); }
        });
    }

    @FXML private void handleMarkPaid() {
        Violation sel = violationTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showStatus("Select a violation.", true); return; }
        try {
            dao.updateViolationStatus(sel.getViolationId(), "Paid");
            showStatus("Marked as Paid.", false); loadViolations();
        } catch (SQLException e) { showStatus("Error: " + e.getMessage(), true); }
    }

    private void showStatus(String m, boolean err) {
        statusLabel.setText(m);
        statusLabel.setStyle(err ? "-fx-text-fill:#e53935;" : "-fx-text-fill:#43a047;");
    }

    @FXML private void goToDashboard() throws Exception { SceneManager.switchTo("Dashboard.fxml"); }
    @FXML private void goToVehicles()  throws Exception { SceneManager.switchTo("Vehicle.fxml"); }
    @FXML private void goToWorkshop()  throws Exception { SceneManager.switchTo("Workshop.fxml"); }
    @FXML private void goToCustomers() throws Exception { SceneManager.switchTo("Customer.fxml"); }
    @FXML private void goToInsurance() throws Exception { SceneManager.switchTo("Insurance.fxml"); }
    @FXML private void goToPolice()    throws Exception { /* already here */ }
}
