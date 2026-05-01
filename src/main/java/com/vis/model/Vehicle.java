package com.vis.model;

import javafx.beans.property.*;

public class Vehicle {

    private final IntegerProperty vehicleId = new SimpleIntegerProperty();
    private final StringProperty registrationNumber = new SimpleStringProperty();
    private final StringProperty make = new SimpleStringProperty();
    private final StringProperty model = new SimpleStringProperty();
    private final IntegerProperty year = new SimpleIntegerProperty();
    private final IntegerProperty ownerId = new SimpleIntegerProperty();
    private final StringProperty ownerName = new SimpleStringProperty(); // joined from Customer

    public Vehicle() {}

    public Vehicle(int vehicleId, String registrationNumber, String make,
                   String model, int year, int ownerId, String ownerName) {
        this.vehicleId.set(vehicleId);
        this.registrationNumber.set(registrationNumber);
        this.make.set(make);
        this.model.set(model);
        this.year.set(year);
        this.ownerId.set(ownerId);
        this.ownerName.set(ownerName);
    }

    public int getVehicleId() { return vehicleId.get(); }
    public IntegerProperty vehicleIdProperty() { return vehicleId; }
    public void setVehicleId(int v) { vehicleId.set(v); }

    public String getRegistrationNumber() { return registrationNumber.get(); }
    public StringProperty registrationNumberProperty() { return registrationNumber; }
    public void setRegistrationNumber(String v) { registrationNumber.set(v); }

    public String getMake() { return make.get(); }
    public StringProperty makeProperty() { return make; }
    public void setMake(String v) { make.set(v); }

    public String getModel() { return model.get(); }
    public StringProperty modelProperty() { return model; }
    public void setModel(String v) { model.set(v); }

    public int getYear() { return year.get(); }
    public IntegerProperty yearProperty() { return year; }
    public void setYear(int v) { year.set(v); }

    public int getOwnerId() { return ownerId.get(); }
    public IntegerProperty ownerIdProperty() { return ownerId; }
    public void setOwnerId(int v) { ownerId.set(v); }

    public String getOwnerName() { return ownerName.get(); }
    public StringProperty ownerNameProperty() { return ownerName; }
    public void setOwnerName(String v) { ownerName.set(v); }
}
