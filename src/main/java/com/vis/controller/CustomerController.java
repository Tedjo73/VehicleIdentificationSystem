package com.vis.controller;

import com.vis.dao.CustomerDAO;
import com.vis.model.Customer;
import com.vis.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.PieChart;

import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomerController {

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> colId;
    @FXML private TableColumn<Customer, String>  colName;
    @FXML private TableColumn<Customer, String>  colAddress;
    @FXML private TableColumn<Customer, String>  colPhone;
    @FXML private TableColumn<Customer, String>  colEmail;

    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;
    @FXML private PieChart customerChart;

    private final CustomerDAO dao = new CustomerDAO();
    private ObservableList<Customer> allData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        loadData();
    }

    private void loadData() {
        try {
            progressBar.setProgress(-1);
            allData = FXCollections.observableArrayList(
                dao.getAllCustomers().stream()
                    .filter(c -> c.getName() != null && !c.getName().isBlank())
                    .collect(java.util.stream.Collectors.toList()));
            customerTable.setItems(allData);
            updateChart();
            progressBar.setProgress(1.0);
        } catch (SQLException e) {
            showStatus("Error: " + e.getMessage(), true);
        }
    }

    private void updateChart() {
        if (customerChart == null) return;
        customerChart.getData().clear();
        Map<String, Long> counts = allData.stream().collect(Collectors.groupingBy(c -> {
            String email = c.getEmail();
            if (email != null && email.contains("@"))
                return email.substring(email.indexOf("@") + 1);
            return "Other";
        }, Collectors.counting()));
        counts.forEach((domain, count) -> customerChart.getData().add(new PieChart.Data(domain, count)));
    }



    private Dialog<Customer> createCustomerDialog(String title, Customer c) {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        TextField nField = new TextField();
        TextField aField = new TextField();
        TextField pField = new TextField();
        TextField eField = new TextField();

        if (c != null) {
            nField.setText(c.getName());
            aField.setText(c.getAddress());
            pField.setText(c.getPhone());
            eField.setText(c.getEmail());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Name:"), 0, 0); grid.add(nField, 1, 0);
        grid.add(new Label("Address:"), 0, 1); grid.add(aField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2); grid.add(pField, 1, 2);
        grid.add(new Label("Email:"), 0, 3); grid.add(eField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                return new Customer(c == null ? 0 : c.getId(), nField.getText().trim(),
                    aField.getText().trim(), pField.getText().trim(), eField.getText().trim());
            }
            return null;
        });
        return dialog;
    }

    @FXML private void handleAdd() {
        createCustomerDialog("Add Customer", null).showAndWait().ifPresent(c -> {
            if (c.getName().isBlank()) {
                showStatus("Name is required.", true);
                return;
            }
            try {
                dao.insertCustomer(c);
                showStatus("Customer added.", false);
                loadData();
            } catch (Exception e) { showStatus("Error: " + e.getMessage(), true); }
        });
    }

    @FXML private void handleUpdate() {
        Customer sel = customerTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showStatus("Select a customer.", true); return; }
        createCustomerDialog("Edit Customer", sel).showAndWait().ifPresent(c -> {
            if (c.getName().isBlank()) {
                showStatus("Name is required.", true);
                return;
            }
            try {
                dao.updateCustomer(c);
                showStatus("Customer updated.", false);
                loadData();
            } catch (Exception e) { showStatus("Error: " + e.getMessage(), true); }
        });
    }

    @FXML private void handleDelete() {
        Customer sel = customerTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showStatus("Select a customer.", true); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete " + sel.getName() + "?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(b -> {
            if (b == ButtonType.YES) {
                try { dao.deleteCustomer(sel.getId()); showStatus("Deleted.", false); loadData(); }
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
    @FXML private void goToWorkshop()  throws Exception { SceneManager.switchTo("Workshop.fxml"); }
    @FXML private void goToCustomers() throws Exception { /* already here */ }
    @FXML private void goToPolice()    throws Exception { SceneManager.switchTo("Police.fxml"); }
}
