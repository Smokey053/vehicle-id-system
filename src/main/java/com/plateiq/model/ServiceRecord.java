package com.plateiq.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ServiceRecord {
    
    private int serviceId;
    private int vehicleId;
    private LocalDate serviceDate;
    private String serviceType;
    private String description;
    private BigDecimal cost;
    private String vehicleRegistration;
    private String vehicleMake;
    private String vehicleModel;
    
    public ServiceRecord() {
    }

    public ServiceRecord(int serviceId, int vehicleId, LocalDate serviceDate, 
                        String serviceType, String description, BigDecimal cost,
                        String vehicleRegistration, String vehicleMake, String vehicleModel) {
        this.serviceId = serviceId;
        this.vehicleId = vehicleId;
        this.serviceDate = serviceDate;
        this.serviceType = serviceType;
        this.description = description;
        this.cost = cost;
        this.vehicleRegistration = vehicleRegistration;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
    }

    public ServiceRecord(int serviceId, int vehicleId, LocalDate serviceDate,
                        String serviceType, String description, double cost) {
        this(serviceId, vehicleId, serviceDate, serviceType, description, BigDecimal.valueOf(cost), null, null, null);
    }
    

    public int getServiceId() {
        return serviceId;
    }
    

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }
    

    public int getVehicleId() {
        return vehicleId;
    }
    

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
    

    public LocalDate getServiceDate() {
        return serviceDate;
    }
    

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }
    

    public String getServiceType() {
        return serviceType;
    }
    

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    

    public String getDescription() {
        return description;
    }
    

    public void setDescription(String description) {
        this.description = description;
    }
    

    public BigDecimal getCost() {
        return cost;
    }
    

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
    

    public String getVehicleRegistration() {
        return vehicleRegistration;
    }

    public String getVehiclePlate() {
        return vehicleRegistration;
    }
    

    public void setVehicleRegistration(String vehicleRegistration) {
        this.vehicleRegistration = vehicleRegistration;
    }
    

    public String getVehicleMake() {
        return vehicleMake;
    }
    
    // Sets the vehicle's make.
    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }
    
    // Gets the vehicle's model.
    public String getVehicleModel() {
        return vehicleModel;
    }
    
    // Sets the vehicle's model.
    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }
    
    // Utility methods.
    
    // Returns a string representation of the ServiceRecord.
    @Override
    public String toString() {
        return "ServiceRecord{" +
                "serviceId=" + serviceId +
                ", vehicleId=" + vehicleId +
                ", serviceDate=" + serviceDate +
                ", serviceType='" + serviceType + '\'' +
                ", cost=" + cost +
                '}';
    }
}
