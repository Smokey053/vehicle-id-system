package com.plateiq.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Claim {
    
    private int claimId;
    private int policyId;
    private LocalDate claimDate;
    private BigDecimal claimAmount;
    private String status;
    private String description;
    private String policyNumber;
    private String vehicleRegistration;
    private String vehicleMake;
    private String vehicleModel;
    
    public Claim() {
    }

    public Claim(int claimId, int policyId, LocalDate claimDate, BigDecimal claimAmount, 
                String status, String description, String policyNumber, 
                String vehicleRegistration, String vehicleMake, String vehicleModel) {
        this.claimId = claimId;
        this.policyId = policyId;
        this.claimDate = claimDate;
        this.claimAmount = claimAmount;
        this.status = status;
        this.description = description;
        this.policyNumber = policyNumber;
        this.vehicleRegistration = vehicleRegistration;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
    }

    public Claim(int claimId, int policyId, String policyNumber, LocalDate claimDate,
                double claimAmount, String status, String description) {
        this(claimId, policyId, claimDate, BigDecimal.valueOf(claimAmount), status, description,
            policyNumber, null, null, null);
    }
    

    public int getClaimId() {
        return claimId;
    }
    

    public void setClaimId(int claimId) {
        this.claimId = claimId;
    }
    

    public int getPolicyId() {
        return policyId;
    }
    

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }
    

    public LocalDate getClaimDate() {
        return claimDate;
    }
    

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }
    

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }
    

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
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
    

    public String getPolicyNumber() {
        return policyNumber;
    }
    

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
    

    public String getVehicleRegistration() {
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
     * Returns a string representation of the Claim.
     * 
     * @return a string containing claim details
     */
    @Override
    public String toString() {
        return "Claim{" +
                "claimId=" + claimId +
                ", claimAmount=" + claimAmount +
                ", status='" + status + '\'' +
                ", claimDate=" + claimDate +
                '}';
    }
}
