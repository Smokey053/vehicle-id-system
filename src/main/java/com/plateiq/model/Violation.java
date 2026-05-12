package com.plateiq.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity class representing a Traffic Violation.
 * Contains all violation-related information including fines and status.
 *
 * @author Plate IQ Team
 * @version 1.0
 */
public class Violation {
    
    private int violationId;
    private int vehicleId;
    private LocalDate violationDate;
    private String violationType;
    private BigDecimal fineAmount;
    private String status;
    private String description;
    private String vehicleRegistration;
    private String vehicleMake;
    private String vehicleModel;
    
    /**
     * Default constructor.
     */
    public Violation() {
    }
    
    /**
     * Parameterized constructor for creating a Violation with all fields.
     * 
     * @param violationId the unique identifier for the violation
     * @param vehicleId the ID of the vehicle involved
     * @param violationDate the date the violation occurred
     * @param violationType the type of violation
     * @param fineAmount the fine amount
     * @param status the violation status (PAID, UNPAID)
     * @param description detailed description of the violation
     * @param vehicleRegistration the vehicle's registration number
     * @param vehicleMake the vehicle's make
     * @param vehicleModel the vehicle's model
     */
    public Violation(int violationId, int vehicleId, LocalDate violationDate, 
                    String violationType, BigDecimal fineAmount, String status, 
                    String description, String vehicleRegistration, 
                    String vehicleMake, String vehicleModel) {
        this.violationId = violationId;
        this.vehicleId = vehicleId;
        this.violationDate = violationDate;
        this.violationType = violationType;
        this.fineAmount = fineAmount;
        this.status = status;
        this.description = description;
        this.vehicleRegistration = vehicleRegistration;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
    }

    public Violation(int violationId, String vehiclePlate, LocalDate violationDate,
                    String violationType, double fineAmount, String status, String description) {
        this(violationId, 0, violationDate, violationType, BigDecimal.valueOf(fineAmount), status,
            description, vehiclePlate, null, null);
    }
    
    // Getters and Setters
    
    /**
     * Gets the violation's unique identifier.
     * 
     * @return the violation ID
     */
    public int getViolationId() {
        return violationId;
    }
    
    /**
     * Sets the violation's unique identifier.
     * 
     * @param violationId the new violation ID
     */
    public void setViolationId(int violationId) {
        this.violationId = violationId;
    }
    
    /**
     * Gets the vehicle ID associated with this violation.
     * 
     * @return the vehicle ID
     */
    public int getVehicleId() {
        return vehicleId;
    }
    
    /**
     * Sets the vehicle ID associated with this violation.
     * 
     * @param vehicleId the new vehicle ID
     */
    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    /**
     * Gets the violation date.
     * 
     * @return the violation date
     */
    public LocalDate getViolationDate() {
        return violationDate;
    }
    
    /**
     * Sets the violation date.
     * 
     * @param violationDate the new violation date
     */
    public void setViolationDate(LocalDate violationDate) {
        this.violationDate = violationDate;
    }
    
    /**
     * Gets the violation type.
     * 
     * @return the violation type
     */
    public String getViolationType() {
        return violationType;
    }
    
    /**
     * Sets the violation type.
     * 
     * @param violationType the new violation type
     */
    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }
    
    /**
     * Gets the fine amount.
     * 
     * @return the fine amount
     */
    public BigDecimal getFineAmount() {
        return fineAmount;
    }
    
    /**
     * Sets the fine amount.
     * 
     * @param fineAmount the new fine amount
     */
    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }
    
    /**
     * Gets the violation status.
     * 
     * @return the status (PAID, UNPAID)
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Sets the violation status.
     * 
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Gets the violation description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the violation description.
     * 
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
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
     * Checks if the violation is unpaid.
     * 
     * @return true if status is UNPAID
     */
    public boolean isUnpaid() {
        return "UNPAID".equalsIgnoreCase(status);
    }
    
    /**
     * Returns a string representation of the Violation.
     * 
     * @return a string containing violation details
     */
    @Override
    public String toString() {
        return "Violation{" +
                "violationId=" + violationId +
                ", violationType='" + violationType + '\'' +
                ", fineAmount=" + fineAmount +
                ", status='" + status + '\'' +
                ", violationDate=" + violationDate +
                '}';
    }
}
