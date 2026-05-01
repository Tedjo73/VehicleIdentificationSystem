package com.vis.model;

import javafx.beans.property.*;

public class PoliceReport {

    private final IntegerProperty reportId = new SimpleIntegerProperty();
    private final IntegerProperty vehicleId = new SimpleIntegerProperty();
    private final StringProperty reportDate = new SimpleStringProperty();
    private final StringProperty reportType = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty officerName = new SimpleStringProperty();
    private final StringProperty regNumber = new SimpleStringProperty(); // joined

    public PoliceReport() {}

    public PoliceReport(int reportId, int vehicleId, String reportDate, String reportType,
                        String description, String officerName, String regNumber) {
        this.reportId.set(reportId);
        this.vehicleId.set(vehicleId);
        this.reportDate.set(reportDate);
        this.reportType.set(reportType);
        this.description.set(description);
        this.officerName.set(officerName);
        this.regNumber.set(regNumber);
    }

    public int getReportId() { return reportId.get(); }
    public IntegerProperty reportIdProperty() { return reportId; }

    public int getVehicleId() { return vehicleId.get(); }
    public IntegerProperty vehicleIdProperty() { return vehicleId; }

    public String getReportDate() { return reportDate.get(); }
    public StringProperty reportDateProperty() { return reportDate; }
    public void setReportDate(String v) { reportDate.set(v); }

    public String getReportType() { return reportType.get(); }
    public StringProperty reportTypeProperty() { return reportType; }
    public void setReportType(String v) { reportType.set(v); }

    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }
    public void setDescription(String v) { description.set(v); }

    public String getOfficerName() { return officerName.get(); }
    public StringProperty officerNameProperty() { return officerName; }
    public void setOfficerName(String v) { officerName.set(v); }

    public String getRegNumber() { return regNumber.get(); }
    public StringProperty regNumberProperty() { return regNumber; }
    public void setRegNumber(String v) { regNumber.set(v); }
}
