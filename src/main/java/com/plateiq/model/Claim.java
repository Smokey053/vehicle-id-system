package com.plateiq.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity class representing an Insurance Claim.
 * Contains all claim-related information including amounts and status.
 *
 * @author Plate IQ Team
 * @version 1.0
 */
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
    
    /**
     * Default constructor.
     */
    public Claim() {
    }
    
    /**
     * Parameterized constructor for creating a Claim with all fields.
     * 
     * @param claimId the unique identifier for the claim
     * @param policyId the ID of the associated insurance policy
     * @param claimDate the date the claim was filed
     * @param claimAmount the amount claimed
     * @param status the claim status (PENDING, APPROVED, REJECTED)
     * @param description detailed description of the claim
     * @param policyNumber the policy number
     * @param vehicleRegistration the vehicle's registration number
     * @param vehicleMake the vehicle's make
     * @param vehicleModel the vehicle's model
     */
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
    
    // Getters and Setters
    
    /**
     * Gets the claim's unique identifier.
     * 
     * @return the claim ID
     */
    public int getClaimId() {
        return claimId;
    }
    
    /**
     * Sets the claim's unique identifier.
     * 
     * @param claimId the new claim ID
     */
    public void setClaimId(int claimId) {
        this.claimId = claimId;
    }
    
    /**
     * Gets the policy ID associated with this claim.
     * 
     * @return the policy ID
     */
    public int getPolicyId() {
        return policyId;
    }
    
    /**
     * Sets the policy ID associated with this claim.
     * 
     * @param policyId the new policy ID
     */
    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }
    
    /**
     * Gets the claim date.
     * 
     * @return the claim date
     */
    public LocalDate getClaimDate() {
        return claimDate;
    }
    
    /**
     * Sets the claim date.
     * 
     * @param claimDate the new claim date
     */
    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }
    
    /**
     * Gets the claim amount.
     * 
     * @return the claim amount
     */
    public BigDecimal getClaimAmount() {
        return claimAmount;
    }
    
    /**
     * Sets the claim amount.
     * 
     * @param claimAmount the new claim amount
     */
    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }
    
    /**
     * Gets the claim status.
     * 
     * @return the status (PENDING, APPROVED, REJECTED)
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Sets the claim status.
     * 
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Gets the claim description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the claim description.
     * 
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the policy number.
     * 
     * @return the policy number
     */
    public String getPolicyNumber() {
        return policyNumber;
    }
    
    /**
     * Sets the policy number.
     * 
     * @param policyNumber the new policy number
     */
    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
    
    /**
     * Gets the vehicle's registration number.
     * 
     * @return the registration number
     */
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
