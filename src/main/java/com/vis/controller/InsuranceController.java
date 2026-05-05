package com.vis.controller;

import com.vis.dao.InsuranceDAO;
import com.vis.model.Insurance;
import com.vis.util.SceneManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.util.Optional;

public class InsuranceController {

    @FXML private TableView<Insurance> insuranceTable;
    @FXML private Label statusLabel;

    private InsuranceDAO dao = new InsuranceDAO();
    private com.vis.dao.VehicleDAO vehicleDAO = new com.vis.dao.VehicleDAO();
    private ObservableList<Insurance> list = FXCollections.observableArrayList();
    private java.util.List<com.vis.model.Vehicle> vehicles;

    @FXML
    public void initialize() {
        try { vehicles = vehicleDAO.getAllVehicles(); } catch(Exception e){}
        loadData();
    }

    private void loadData() {
        Platform.runLater(() -> {
            try {
                list.setAll(dao.getAllInsurance());
                insuranceTable.setItems(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleAdd() {
        showDialog(null);
    }

    @FXML
    private void handleEdit() {
        Insurance selected = insuranceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select a policy to edit.");
            return;
        }
        showDialog(selected);
    }

    @FXML
    private void handleDelete() {
        Insurance selected = insuranceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select a policy to delete.");
            return;
        }
        try {
            dao.deleteInsurance(selected.getInsuranceId());
            statusLabel.setText("Policy deleted.");
            loadData();
        } catch (Exception e) {
            statusLabel.setText("Failed to delete policy.");
        }
    }

    private void showDialog(Insurance existing) {
        Dialog<Insurance> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Policy" : "Edit Policy");

        ButtonType saveBtnType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        ComboBox<String> vehicleIdCombo = new ComboBox<>();
        if (vehicles != null) {
            ObservableList<String> vItems = FXCollections.observableArrayList();
            vehicles.forEach(v -> vItems.add(v.getVehicleId() + " - " + v.getRegistrationNumber() + " (" + v.getOwnerName() + ")"));
            vehicleIdCombo.setItems(vItems);
        }
        ComboBox<String> provider = new ComboBox<>();
        provider.getItems().addAll("Lesotho National Insurance", "Alliance Insurance", "Metropolitan Lesotho", "LNIG", "Other");
        provider.setEditable(true);
        TextField policyNum = new TextField();
        DatePicker startDate = new DatePicker();
        DatePicker endDate = new DatePicker();

        if (existing != null) {
            vehicleIdCombo.getItems().stream()
                .filter(s -> s.startsWith(existing.getVehicleId() + " - "))
                .findFirst().ifPresent(vehicleIdCombo::setValue);
            provider.setValue(existing.getProviderName());
            policyNum.setText(existing.getPolicyNumber());
            startDate.setValue(existing.getStartDate());
            endDate.setValue(existing.getEndDate());
        } else {
            startDate.setValue(LocalDate.now());
            endDate.setValue(LocalDate.now().plusYears(1));
        }

        grid.add(new Label("Vehicle:"), 0, 0);
        grid.add(vehicleIdCombo, 1, 0);
        grid.add(new Label("Provider:"), 0, 1);
        grid.add(provider, 1, 1);
        grid.add(new Label("Policy Number:"), 0, 2);
        grid.add(policyNum, 1, 2);
        grid.add(new Label("Start Date:"), 0, 3);
        grid.add(startDate, 1, 3);
        grid.add(new Label("End Date:"), 0, 4);
        grid.add(endDate, 1, 4);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(vehicleIdCombo::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtnType) {
                try {
                    int vId = vehicleIdCombo.getValue() != null ? Integer.parseInt(vehicleIdCombo.getValue().split(" - ")[0]) : 0;
                    int id = existing == null ? 0 : existing.getInsuranceId();
                    return new Insurance(id, vId, provider.getValue(), policyNum.getText(), startDate.getValue(), endDate.getValue());
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Insurance> result = dialog.showAndWait();
        result.ifPresent(ins -> {
            try {
                if (existing == null) dao.insertInsurance(ins);
                else dao.updateInsurance(ins);
                loadData();
                statusLabel.setText("Policy saved successfully.");
            } catch (Exception e) {
                statusLabel.setText("Failed to save policy.");
                e.printStackTrace();
            }
        });
    }

    @FXML private void goToDashboard() throws Exception { SceneManager.switchTo("Dashboard.fxml"); }
    @FXML private void goToVehicles() throws Exception { SceneManager.switchTo("Vehicle.fxml"); }
    @FXML private void goToWorkshop() throws Exception { SceneManager.switchTo("Workshop.fxml"); }
    @FXML private void goToCustomer() throws Exception { SceneManager.switchTo("Customer.fxml"); }
    @FXML private void goToPolice() throws Exception { SceneManager.switchTo("Police.fxml"); }
}
