package com.vis.controller;

import com.vis.dao.CustomerDAO;
import com.vis.dao.VehicleDAO;
import com.vis.model.Customer;
import com.vis.model.Vehicle;
import com.vis.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.PieChart;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VehicleController {

    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, Integer> colId;
    @FXML private TableColumn<Vehicle, String>  colReg;
    @FXML private TableColumn<Vehicle, String>  colMake;
    @FXML private TableColumn<Vehicle, String>  colModel;
    @FXML private TableColumn<Vehicle, Integer> colYear;
    @FXML private TableColumn<Vehicle, String>  colOwner;

    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;
    @FXML private PieChart vehicleChart;

    private final VehicleDAO vehicleDAO   = new VehicleDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private ObservableList<Vehicle> allData = FXCollections.observableArrayList();
    private List<Customer> customers;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colReg.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        colMake.setCellValueFactory(new PropertyValueFactory<>("make"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("ownerName"));

        loadOwners();
        loadData();
    }

    private void loadOwners() {
        try {
            customers = customerDAO.getAllCustomers();
        } catch (SQLException e) {
            showStatus("Error loading customers: " + e.getMessage(), true);
        }
    }

    private void loadData() {
        try {
            progressBar.setProgress(-1);
            allData = FXCollections.observableArrayList(
                vehicleDAO.getAllVehicles().stream()
                    .filter(v -> v.getRegistrationNumber() != null && !v.getRegistrationNumber().isBlank())
                    .collect(java.util.stream.Collectors.toList()));
            vehicleTable.setItems(allData);
            updateChart();
            progressBar.setProgress(1.0);
        } catch (SQLException e) {
            showStatus("Error loading vehicles: " + e.getMessage(), true);
            progressBar.setProgress(0);
        }
    }

    private void updateChart() {
        if (vehicleChart == null) return;
        vehicleChart.getData().clear();
        Map<String, Long> counts = allData.stream()
            .collect(Collectors.groupingBy(v -> v.getMake() == null || v.getMake().isBlank() ? "Unknown" : v.getMake(), Collectors.counting()));
        counts.forEach((make, count) -> vehicleChart.getData().add(new PieChart.Data(make, count)));
    }



    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            loadData();
            return;
        }
        ObservableList<Vehicle> filtered = FXCollections.observableArrayList();
        allData.forEach(v -> {
            if (v.getRegistrationNumber().toLowerCase().contains(query) ||
                v.getMake().toLowerCase().contains(query) ||
                v.getOwnerName().toLowerCase().contains(query)) {
                filtered.add(v);
            }
        });
        vehicleTable.setItems(filtered);
        showStatus("Found " + filtered.size() + " result(s).", false);
    }

    private Dialog<Vehicle> createVehicleDialog(String title, Vehicle v) {
        Dialog<Vehicle> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        TextField rField = new TextField();
        ComboBox<String> mkCombo = new ComboBox<>();
        mkCombo.getItems().addAll("Toyota", "Nissan", "Hyundai", "Ford", "Volkswagen", "BMW", "Mercedes-Benz", "Honda", "Other");
        mkCombo.setEditable(true);
        TextField mdField = new TextField();
        TextField yField = new TextField();
        ComboBox<String> oCombo = new ComboBox<>();

        if (customers != null) {
            ObservableList<String> names = FXCollections.observableArrayList();
            customers.forEach(c -> names.add(c.getId() + " - " + c.getName()));
            oCombo.setItems(names);
        }

        if (v != null) {
            rField.setText(v.getRegistrationNumber());
            mkCombo.setValue(v.getMake());
            mdField.setText(v.getModel());
            yField.setText(String.valueOf(v.getYear()));
            oCombo.getItems().stream()
                .filter(s -> s.startsWith(v.getOwnerId() + " - "))
                .findFirst().ifPresent(oCombo::setValue);
        }

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Reg Number:"), 0, 0); grid.add(rField, 1, 0);
        grid.add(new Label("Make:"), 0, 1); grid.add(mkCombo, 1, 1);
        grid.add(new Label("Model:"), 0, 2); grid.add(mdField, 1, 2);
        grid.add(new Label("Year:"), 0, 3); grid.add(yField, 1, 3);
        grid.add(new Label("Owner:"), 0, 4); grid.add(oCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                try {
                    int oId = oCombo.getValue() != null ? Integer.parseInt(oCombo.getValue().split(" - ")[0]) : 0;
                    return new Vehicle(v == null ? 0 : v.getVehicleId(), rField.getText().trim(),
                        mkCombo.getValue() != null ? mkCombo.getValue().trim() : "", mdField.getText().trim(),
                        Integer.parseInt(yField.getText().trim()), oId, "");
                } catch(Exception e) { return null; }
            }
            return null;
        });
        return dialog;
    }

    @FXML
    private void handleAdd() {
        createVehicleDialog("Add Vehicle", null).showAndWait().ifPresent(v -> {
            if (v.getRegistrationNumber().isBlank() || v.getMake().isBlank()) {
                showStatus("Registration and Make are required.", true);
                return;
            }
            try {
                vehicleDAO.insertVehicle(v);
                showStatus("Vehicle added successfully.", false);
                loadData();
            } catch (Exception e) {
                showStatus("Error: " + e.getMessage(), true);
            }
        });
    }

    @FXML
    private void handleUpdate() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showStatus("Select a vehicle to update.", true); return; }
        
        createVehicleDialog("Edit Vehicle", selected).showAndWait().ifPresent(v -> {
            if (v.getRegistrationNumber().isBlank() || v.getMake().isBlank()) {
                showStatus("Registration and Make are required.", true);
                return;
            }
            try {
                vehicleDAO.updateVehicle(v);
                showStatus("Vehicle updated.", false);
                loadData();
            } catch (Exception e) {
                showStatus("Error: " + e.getMessage(), true);
            }
        });
    }

    @FXML
    private void handleDelete() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showStatus("Select a vehicle to delete.", true); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete vehicle " + selected.getRegistrationNumber() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    vehicleDAO.deleteVehicle(selected.getVehicleId());
                    showStatus("Vehicle deleted.", false);
                    loadData();
                } catch (SQLException e) {
                    showStatus("Error: " + e.getMessage(), true);
                }
            }
        });
    }

    private void showStatus(String msg, boolean error) {
        statusLabel.setText(msg);
        statusLabel.setStyle(error ? "-fx-text-fill: #e53935;" : "-fx-text-fill: #43a047;");
    }

    @FXML private void goToDashboard() throws Exception { SceneManager.switchTo("Dashboard.fxml"); }
    @FXML private void goToVehicles()  throws Exception { /* already here */ }
    @FXML private void goToWorkshop()  throws Exception { SceneManager.switchTo("Workshop.fxml"); }
    @FXML private void goToCustomers() throws Exception { SceneManager.switchTo("Customer.fxml"); }
    @FXML private void goToInsurance() throws Exception { SceneManager.switchTo("Insurance.fxml"); }
    @FXML private void goToPolice()    throws Exception { SceneManager.switchTo("Police.fxml"); }
}
