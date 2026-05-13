package com.plateiq.model;

import java.time.LocalDate;

public class PoliceReport {
    
    private int reportId;
    private int vehicleId;
    private LocalDate reportDate;
    private String reportType;
    private String description;
    private String officerName;
    private String vehicleRegistration;
    private String vehicleMake;
    private String vehicleModel;
    
    public PoliceReport() {
    }

    public PoliceReport(int reportId, int vehicleId, LocalDate reportDate, 
                       String reportType, String description, String officerName,
                       String vehicleRegistration, String vehicleMake, String vehicleModel) {
        this.reportId = reportId;
        this.vehicleId = vehicleId;
        this.reportDate = reportDate;
        this.reportType = reportType;
        this.description = description;
        this.officerName = officerName;
        this.vehicleRegistration = vehicleRegistration;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
    }

    public PoliceReport(int reportId, String vehiclePlate, LocalDate reportDate,
                       String reportType, String description, String officerName) {
        this(reportId, 0, reportDate, reportType, description, officerName, vehiclePlate, null, null);
    }
    

    public int getReportId() {
        return reportId;
    }
    

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }
    

    public int getVehicleId() {
        return vehicleId;
    }
    

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
    

    public LocalDate getReportDate() {
        return reportDate;
    }
    

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }
    

    public String getReportType() {
        return reportType;
    }
    

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    

    public String getDescription() {
        return description;
    }
    

    public void setDescription(String description) {
        this.description = description;
    }
    

    public String getOfficerName() {
        return officerName;
    }
    

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
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
    
    /** Sets the vehicle's make. */
    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }
    
    /** Gets the vehicle's model. */
    public String getVehicleModel() {
        return vehicleModel;
    }
    
    /** Sets the vehicle's model. */
    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }
    
    // Utility methods.
    
    /** Returns a string representation of the PoliceReport. */
    @Override
    public String toString() {
        return "PoliceReport{" +
                "reportId=" + reportId +
                ", reportType='" + reportType + '\'' +
                ", reportDate=" + reportDate +
                ", description='" + description + '\'' +
                '}';
    }
}
