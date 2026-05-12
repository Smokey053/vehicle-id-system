package com.plateiq.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    
    public Violation() {
    }

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
    

    public int getViolationId() {
        return violationId;
    }
    

    public void setViolationId(int violationId) {
        this.violationId = violationId;
    }
    

    public int getVehicleId() {
        return vehicleId;
    }
    

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
    

    public LocalDate getViolationDate() {
        return violationDate;
    }
    

    public void setViolationDate(LocalDate violationDate) {
        this.violationDate = violationDate;
    }
    

    public String getViolationType() {
        return violationType;
    }
    

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }
    

    public BigDecimal getFineAmount() {
        return fineAmount;
    }
    

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }
    

    public String getStatus() {
        return status;
    }
    

    public void setStatus(String status) {
        this.status = status;
    }
    

    public String getDescription() {
        return description;
    }
    

    public void setDescription(String description) {
        this.description = description;
    }
    

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
