package com.vis.model;

import javafx.beans.property.*;

public class Violation {

    private final IntegerProperty violationId = new SimpleIntegerProperty();
    private final IntegerProperty vehicleId = new SimpleIntegerProperty();
    private final StringProperty violationDate = new SimpleStringProperty();
    private final StringProperty violationType = new SimpleStringProperty();
    private final DoubleProperty fineAmount = new SimpleDoubleProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty regNumber = new SimpleStringProperty(); // joined

    public Violation() {}

    public Violation(int violationId, int vehicleId, String violationDate,
                     String violationType, double fineAmount, String status, String regNumber) {
        this.violationId.set(violationId);
        this.vehicleId.set(vehicleId);
        this.violationDate.set(violationDate);
        this.violationType.set(violationType);
        this.fineAmount.set(fineAmount);
        this.status.set(status);
        this.regNumber.set(regNumber);
    }

    public int getViolationId() { return violationId.get(); }
    public IntegerProperty violationIdProperty() { return violationId; }

    public int getVehicleId() { return vehicleId.get(); }
    public IntegerProperty vehicleIdProperty() { return vehicleId; }

    public String getViolationDate() { return violationDate.get(); }
    public StringProperty violationDateProperty() { return violationDate; }
    public void setViolationDate(String v) { violationDate.set(v); }

    public String getViolationType() { return violationType.get(); }
    public StringProperty violationTypeProperty() { return violationType; }
    public void setViolationType(String v) { violationType.set(v); }

    public double getFineAmount() { return fineAmount.get(); }
    public DoubleProperty fineAmountProperty() { return fineAmount; }
    public void setFineAmount(double v) { fineAmount.set(v); }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
    public void setStatus(String v) { status.set(v); }

    public String getRegNumber() { return regNumber.get(); }
    public StringProperty regNumberProperty() { return regNumber; }
    public void setRegNumber(String v) { regNumber.set(v); }
}
