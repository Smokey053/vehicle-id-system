package com.plateiq.model;

import java.time.LocalDate;

/**
 * Entity class representing a Police Report.
 * Contains all report-related information including type and description.
 *
 * @author Plate IQ Team
 * @version 1.0
 */
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
    
    /**
     * Default constructor.
     */
    public PoliceReport() {
    }
    
    /**
     * Parameterized constructor for creating a PoliceReport with all fields.
     * 
     * @param reportId the unique identifier for the report
     * @param vehicleId the ID of the vehicle involved
     * @param reportDate the date the report was created
     * @param reportType the type of report (ACCIDENT, THEFT)
     * @param description detailed description of the incident
     * @param officerName the name of the reporting officer
     * @param vehicleRegistration the vehicle's registration number
     * @param vehicleMake the vehicle's make
     * @param vehicleModel the vehicle's model
     */
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
    
    // Getters and Setters
    
    /**
     * Gets the report's unique identifier.
     * 
     * @return the report ID
     */
    public int getReportId() {
        return reportId;
    }
    
    /**
     * Sets the report's unique identifier.
     * 
     * @param reportId the new report ID
     */
    public void setReportId(int reportId) {
        this.reportId = reportId;
    }
    
    /**
     * Gets the vehicle ID associated with this report.
     * 
     * @return the vehicle ID
     */
    public int getVehicleId() {
        return vehicleId;
    }
    
    /**
     * Sets the vehicle ID associated with this report.
     * 
     * @param vehicleId the new vehicle ID
     */
    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    /**
     * Gets the report date.
     * 
     * @return the report date
     */
    public LocalDate getReportDate() {
        return reportDate;
    }
    
    /**
     * Sets the report date.
     * 
     * @param reportDate the new report date
     */
    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }
    
    /**
     * Gets the report type.
     * 
     * @return the report type (ACCIDENT, THEFT)
     */
    public String getReportType() {
        return reportType;
    }
    
    /**
     * Sets the report type.
     * 
     * @param reportType the new report type
     */
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    
    /**
     * Gets the report description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the report description.
     * 
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the officer's name.
     * 
     * @return the officer name
     */
    public String getOfficerName() {
        return officerName;
    }
    
    /**
     * Sets the officer's name.
     * 
     * @param officerName the new officer name
     */
    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }
    
    /**
     * Gets the vehicle's registration number.
     * 
     * @return the registration number
     */
    public String getVehicleRegistration() {
        return vehicleRegistration;
    }

    public String getVehiclePlate() {
        return vehicleRegistration;
    }
    
    /**
     * Sets the vehicle's registration number.
     * 
     * @param vehicleRegistration the new registration number
     */
    public void setVehicleRegistration(String vehicleRegistration) {
        this.vehicleRegistration = vehicleRegistration;
    }
    
    /**
     * Gets the vehicle's make.
     * 
     * @return the make
     */
    public String getVehicleMake() {
        return vehicleMake;
    }
    
    /**
     * Sets the vehicle's make.
     * 
     * @param vehicleMake the new make
     */
    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }
    
    /**
     * Gets the vehicle's model.
     * 
     * @return the model
     */
    public String getVehicleModel() {
        return vehicleModel;
    }
    
    /**
     * Sets the vehicle's model.
     * 
     * @param vehicleModel the new model
     */
    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }
    
    // Utility Methods
    
    /**
     * Returns a string representation of the PoliceReport.
     * 
     * @return a string containing report details
     */
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
