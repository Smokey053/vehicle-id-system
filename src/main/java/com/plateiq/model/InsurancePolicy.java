package com.plateiq.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity class representing an Insurance Policy for a vehicle.
 * Contains all policy-related information including coverage details and expiry dates.
 *
 * @author Plate IQ Team
 * @version 1.0
 */
public class InsurancePolicy {
    
    private int policyId;
    private int vehicleId;
    private String insuranceCompany;
    private String policyNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String coverageDetails;
    private BigDecimal premiumAmount;
    private String status;
    private String vehicleRegistration;
    private String vehicleMake;
    private String vehicleModel;
    
    /**
     * Default constructor.
     */
    public InsurancePolicy() {
    }
    
    /**
     * Parameterized constructor for creating an InsurancePolicy with all fields.
     * 
     * @param policyId the unique identifier for the policy
     * @param vehicleId the ID of the vehicle being insured
     * @param insuranceCompany the name of the insurance company
     * @param policyNumber the policy number
     * @param startDate the policy start date
     * @param endDate the policy expiry date
     * @param coverageDetails detailed coverage information
     * @param premiumAmount the premium amount
     * @param status the policy status (ACTIVE, EXPIRED, etc.)
     * @param vehicleRegistration the vehicle's registration number
     * @param vehicleMake the vehicle's make
     * @param vehicleModel the vehicle's model
     */
    public InsurancePolicy(int policyId, int vehicleId, String insuranceCompany, 
                        String policyNumber, LocalDate startDate, LocalDate endDate, 
                        String coverageDetails, BigDecimal premiumAmount, String status,
                        String vehicleRegistration, String vehicleMake, String vehicleModel) {
        this.policyId = policyId;
        this.vehicleId = vehicleId;
        this.insuranceCompany = insuranceCompany;
        this.policyNumber = policyNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coverageDetails = coverageDetails;
        this.premiumAmount = premiumAmount;
        this.status = status;
        this.vehicleRegistration = vehicleRegistration;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
    }

    public InsurancePolicy(int policyId, String vehiclePlate, String insuranceCompany,
                          String policyNumber, LocalDate startDate, LocalDate endDate,
                          String coverageDetails) {
        this(policyId, 0, insuranceCompany, policyNumber, startDate, endDate, coverageDetails,
            BigDecimal.ZERO, "ACTIVE", vehiclePlate, null, null);
    }
    
    // Getters and Setters
    
    /**
     * Gets the policy's unique identifier.
     * 
     * @return the policy ID
     */
    public int getPolicyId() {
        return policyId;
    }
    
    /**
     * Sets the policy's unique identifier.
     * 
     * @param policyId the new policy ID
     */
    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }
    
    /**
     * Gets the vehicle ID associated with this policy.
     * 
     * @return the vehicle ID
     */
    public int getVehicleId() {
        return vehicleId;
    }
    
    /**
     * Sets the vehicle ID associated with this policy.
     * 
     * @param vehicleId the new vehicle ID
     */
    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    /**
     * Gets the insurance company name.
     * 
     * @return the insurance company
     */
    public String getInsuranceCompany() {
        return insuranceCompany;
    }
    
    /**
     * Sets the insurance company name.
     * 
     * @param insuranceCompany the new insurance company
     */
    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
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
     * Gets the policy start date.
     * 
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }
    
    /**
     * Sets the policy start date.
     * 
     * @param startDate the new start date
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    /**
     * Gets the policy expiry date.
     * 
     * @return the end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }
    
    /**
     * Sets the policy expiry date.
     * 
     * @param endDate the new end date
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    /**
     * Gets the coverage details.
     * 
     * @return the coverage details
     */
    public String getCoverageDetails() {
        return coverageDetails;
    }
    
    /**
     * Sets the coverage details.
     * 
     * @param coverageDetails the new coverage details
     */
    public void setCoverageDetails(String coverageDetails) {
        this.coverageDetails = coverageDetails;
    }
    
    /**
     * Gets the premium amount.
     * 
     * @return the premium amount
     */
    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }
    
    /**
     * Sets the premium amount.
     * 
     * @param premiumAmount the new premium amount
     */
    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }
    
    /**
     * Gets the policy status.
     * 
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Sets the policy status.
     * 
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
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
     * Checks if the policy is currently active.
     * 
     * @return true if the policy is active and not expired
     */
    public boolean isActive() {
        if ("ACTIVE".equalsIgnoreCase(status)) {
            LocalDate today = LocalDate.now();
            return (endDate == null || today.isBefore(endDate) || today.isEqual(endDate)) && 
                   (startDate == null || today.isAfter(startDate) || today.isEqual(startDate));
        }
        return false;
    }
    
    /**
     * Returns a string representation of the InsurancePolicy.
     * 
     * @return a string containing policy details
     */
    @Override
    public String toString() {
        return "InsurancePolicy{" +
                "policyId=" + policyId +
                ", policyNumber='" + policyNumber + '\'' +
                ", insuranceCompany='" + insuranceCompany + '\'' +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                '}';
    }
}
