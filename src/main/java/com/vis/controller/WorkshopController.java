package com.vis.controller;

import com.vis.dao.ServiceRecordDAO;
import com.vis.dao.VehicleDAO;
import com.vis.model.ServiceRecord;
import com.vis.model.Vehicle;
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

public class WorkshopController {

    @FXML private TableView<ServiceRecord> serviceTable;
    @FXML private TableColumn<ServiceRecord, Integer> colId;
    @FXML private TableColumn<ServiceRecord, String>  colReg;
    @FXML private TableColumn<ServiceRecord, String>  colDate;
    @FXML private TableColumn<ServiceRecord, String>  colType;
    @FXML private TableColumn<ServiceRecord, String>  colDesc;
    @FXML private TableColumn<ServiceRecord, Double>  colCost;


    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;
    @FXML private BarChart<String, Number> workshopChart;

    private final ServiceRecordDAO dao = new ServiceRecordDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private ObservableList<ServiceRecord> allData = FXCollections.observableArrayList();
    private List<Vehicle> vehicles;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        colReg.setCellValueFactory(new PropertyValueFactory<>("regNumber"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));
        colType.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        loadVehicles();
        loadData();
    }

    private void loadVehicles() {
        try {
            vehicles = vehicleDAO.getAllVehicles();
        } catch (SQLException e) {
            showStatus("Error loading vehicles: " + e.getMessage(), true);
        }
    }

    private void loadData() {
        try {
            progressBar.setProgress(-1);
            allData = FXCollections.observableArrayList(
                dao.getAllRecords().stream()
                    .filter(r -> r.getRegNumber() != null && !r.getRegNumber().isBlank())
                    .collect(java.util.stream.Collectors.toList()));
            serviceTable.setItems(allData);
            updateChart();
            progressBar.setProgress(1.0);
        } catch (SQLException e) {
            showStatus("Error: " + e.getMessage(), true);
        }
    }

    private void updateChart() {
        if (workshopChart == null) return;
        workshopChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Cost");

        Map<String, Double> costs = allData.stream().collect(
            Collectors.groupingBy(r -> r.getServiceType() == null ? "Unknown" : r.getServiceType(),
            Collectors.summingDouble(ServiceRecord::getCost)));

        costs.forEach((type, cost) -> series.getData().add(new XYChart.Data<>(type, cost)));
        workshopChart.getData().add(series);
    }

    @FXML private void handleAdd() {
        Dialog<ServiceRecord> dialog = new Dialog<>();
        dialog.setTitle("Add Service Record");
        dialog.setHeaderText(null);

        ComboBox<String> vCombo = new ComboBox<>();
        if (vehicles != null) {
            ObservableList<String> items = FXCollections.observableArrayList();
            vehicles.forEach(v -> items.add(v.getVehicleId() + " - " + v.getRegistrationNumber()));
            vCombo.setItems(items);
        }
        
        TextField dField = new TextField(); dField.setPromptText("YYYY-MM-DD");
        TextField tField = new TextField();
        TextField cField = new TextField();
        TextArea dArea = new TextArea(); dArea.setPrefHeight(60);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Vehicle:"), 0, 0); grid.add(vCombo, 1, 0);
        grid.add(new Label("Date:"), 0, 1); grid.add(dField, 1, 1);
        grid.add(new Label("Type:"), 0, 2); grid.add(tField, 1, 2);
        grid.add(new Label("Cost:"), 0, 3); grid.add(cField, 1, 3);
        grid.add(new Label("Description:"), 0, 4); grid.add(dArea, 1, 4);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                try {
                    int vid = Integer.parseInt(vCombo.getValue().split(" - ")[0]);
                    return new ServiceRecord(0, vid, dField.getText().trim(),
                        tField.getText().trim(), dArea.getText().trim(),
                        Double.parseDouble(cField.getText().trim()), "");
                } catch(Exception e) { return null; }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(r -> {
            try {
                dao.insertRecord(r);
                showStatus("Service record added.", false);
                loadData();
            } catch (Exception e) { showStatus("Error: " + e.getMessage(), true); }
        });
    }

    @FXML private void handleDelete() {
        ServiceRecord sel = serviceTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showStatus("Select a record.", true); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete this service record?", ButtonType.YES, ButtonType.NO);
        a.showAndWait().ifPresent(b -> {
            if (b == ButtonType.YES) {
                try { dao.deleteRecord(sel.getServiceId()); showStatus("Deleted.", false); loadData(); }
                catch (SQLException e) { showStatus("Error: " + e.getMessage(), true); }
            }
        });
    }



    private void showStatus(String m, boolean err) {
        statusLabel.setText(m);
        statusLabel.setStyle(err ? "-fx-text-fill:#e53935;" : "-fx-text-fill:#43a047;");
    }

    @FXML private void goToDashboard() throws Exception { SceneManager.switchTo("Dashboard.fxml"); }
    @FXML private void goToVehicles()  throws Exception { SceneManager.switchTo("Vehicle.fxml"); }
    @FXML private void goToWorkshop()  throws Exception { /* already here */ }
    @FXML private void goToCustomers() throws Exception { SceneManager.switchTo("Customer.fxml"); }
    @FXML private void goToPolice()    throws Exception { SceneManager.switchTo("Police.fxml"); }
}
