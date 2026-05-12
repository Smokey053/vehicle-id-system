package com.plateiq.model;

public class Vehicle {
    private int vehicleId;
    private String registrationNumber;
    private String make;
    private String model;
    private int year;
    private String color;
    private int ownerId;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;

    public Vehicle() {
    }

    public Vehicle(String plateNumber, String brand, String model, int year, String ownerName, String ownerPhone) {
        this.registrationNumber = plateNumber;
        this.make = brand;
        this.model = model;
        this.year = year;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
    }

    public Vehicle(int vehicleId, String registrationNumber, String make, String model, int year, String color,
                   int ownerId, String ownerName, String ownerPhone, String ownerEmail) {
        this.vehicleId = vehicleId;
        this.registrationNumber = registrationNumber;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.ownerEmail = ownerEmail;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getPlateNumber() {
        return getRegistrationNumber();
    }

    public void setPlateNumber(String plateNumber) {
        setRegistrationNumber(plateNumber);
    }

    public String getBrand() {
        return getMake();
    }

    public void setBrand(String brand) {
        setMake(brand);
    }

    public String getVehicleRegistration() {
        return getRegistrationNumber();
    }

    public void setVehicleRegistration(String vehicleRegistration) {
        setRegistrationNumber(vehicleRegistration);
    }

    public String getVehiclePlate() {
        return getRegistrationNumber();
    }

    public void setVehiclePlate(String vehiclePlate) {
        setRegistrationNumber(vehiclePlate);
    }
}
