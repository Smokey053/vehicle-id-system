package com.plateiq.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    
    public InsurancePolicy() {
    }

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
    

    public int getPolicyId() {
        return policyId;
    }
    

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }
    

    public int getVehicleId() {
        return vehicleId;
    }
    

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
    

    public String getInsuranceCompany() {
        return insuranceCompany;
    }
    

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }
    

    public String getPolicyNumber() {
        return policyNumber;
    }
    

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
    

    public LocalDate getStartDate() {
        return startDate;
    }
    

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    

    public LocalDate getEndDate() {
        return endDate;
    }
    

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    

    public String getCoverageDetails() {
        return coverageDetails;
    }
    

    public void setCoverageDetails(String coverageDetails) {
        this.coverageDetails = coverageDetails;
    }
    
    // Gets the premium amount.
    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }
    
    // Sets the premium amount.
    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }
    
    // Gets the policy status.
    public String getStatus() {
        return status;
    }
    
    // Sets the policy status.
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Gets the vehicle's registration number.
    public String getVehicleRegistration() {
        return vehicleRegistration;
    }

    public String getVehiclePlate() {
        return vehicleRegistration;
    }
    
    // Sets the vehicle's registration number.
    public void setVehicleRegistration(String vehicleRegistration) {
        this.vehicleRegistration = vehicleRegistration;
    }
    
    // Gets the vehicle's make.
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
    
    // Checks if the policy is currently active.
    public boolean isActive() {
        if ("ACTIVE".equalsIgnoreCase(status)) {
            LocalDate today = LocalDate.now();
            return (endDate == null || today.isBefore(endDate) || today.isEqual(endDate)) && 
                   (startDate == null || today.isAfter(startDate) || today.isEqual(startDate));
        }
        return false;
    }
    
    // Returns a string representation of the InsurancePolicy.
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
