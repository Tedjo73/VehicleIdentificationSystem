package com.vis.model;

import java.time.LocalDate;

public class Insurance {
    private int insuranceId;
    private int vehicleId;
    private String providerName;
    private String policyNumber;
    private LocalDate startDate;
    private LocalDate endDate;

    public Insurance() {}

    public Insurance(int insuranceId, int vehicleId, String providerName, String policyNumber, LocalDate startDate, LocalDate endDate) {
        this.insuranceId = insuranceId;
        this.vehicleId = vehicleId;
        this.providerName = providerName;
        this.policyNumber = policyNumber;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getInsuranceId() { return insuranceId; }
    public void setInsuranceId(int insuranceId) { this.insuranceId = insuranceId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
