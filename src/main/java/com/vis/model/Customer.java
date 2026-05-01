package com.vis.model;

public class Customer extends Person {

    private String address;

    public Customer() {}

    public Customer(int id, String name, String address, String phone, String email) {
        super(id, name, phone, email);
        this.address = address;
    }

    @Override
    public String getRole() {
        return "Customer";
    }

    @Override
    public String getDisplayInfo() {
        return super.getDisplayInfo() + " | Address: " + address;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
