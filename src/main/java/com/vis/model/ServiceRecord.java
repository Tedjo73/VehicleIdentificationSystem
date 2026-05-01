package com.vis.model;

import javafx.beans.property.*;

public class ServiceRecord {

    private final IntegerProperty serviceId = new SimpleIntegerProperty();
    private final IntegerProperty vehicleId = new SimpleIntegerProperty();
    private final StringProperty serviceDate = new SimpleStringProperty();
    private final StringProperty serviceType = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final DoubleProperty cost = new SimpleDoubleProperty();
    private final StringProperty regNumber = new SimpleStringProperty(); // joined

    public ServiceRecord() {}

    public ServiceRecord(int serviceId, int vehicleId, String serviceDate,
                         String serviceType, String description, double cost, String regNumber) {
        this.serviceId.set(serviceId);
        this.vehicleId.set(vehicleId);
        this.serviceDate.set(serviceDate);
        this.serviceType.set(serviceType);
        this.description.set(description);
        this.cost.set(cost);
        this.regNumber.set(regNumber);
    }

    public int getServiceId() { return serviceId.get(); }
    public IntegerProperty serviceIdProperty() { return serviceId; }

    public int getVehicleId() { return vehicleId.get(); }
    public IntegerProperty vehicleIdProperty() { return vehicleId; }

    public String getServiceDate() { return serviceDate.get(); }
    public StringProperty serviceDateProperty() { return serviceDate; }
    public void setServiceDate(String v) { serviceDate.set(v); }

    public String getServiceType() { return serviceType.get(); }
    public StringProperty serviceTypeProperty() { return serviceType; }
    public void setServiceType(String v) { serviceType.set(v); }

    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }
    public void setDescription(String v) { description.set(v); }

    public double getCost() { return cost.get(); }
    public DoubleProperty costProperty() { return cost; }
    public void setCost(double v) { cost.set(v); }

    public String getRegNumber() { return regNumber.get(); }
    public StringProperty regNumberProperty() { return regNumber; }
    public void setRegNumber(String v) { regNumber.set(v); }
}
