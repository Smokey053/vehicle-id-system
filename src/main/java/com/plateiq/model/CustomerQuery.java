package com.plateiq.model;

import java.time.LocalDate;

public class CustomerQuery {
    
    private int queryId;
    private int customerId;
    private int vehicleId;
    private LocalDate queryDate;
    private String queryText;
    private String responseText;
    private String vehicleRegistration;
    private String vehicleMake;
    private String vehicleModel;
    private String customerName;
    
    public CustomerQuery() {
    }

    public CustomerQuery(int queryId, int customerId, int vehicleId, LocalDate queryDate, 
                        String queryText, String responseText, String vehicleRegistration, 
                        String vehicleMake, String vehicleModel, String customerName) {
        this.queryId = queryId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.queryDate = queryDate;
        this.queryText = queryText;
        this.responseText = responseText;
        this.vehicleRegistration = vehicleRegistration;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.customerName = customerName;
    }

    public CustomerQuery(int queryId, int customerId, int vehicleId, LocalDate queryDate,
                        String queryText, String responseText) {
        this(queryId, customerId, vehicleId, queryDate, queryText, responseText,
             null, null, null, null);
    }
    

    public int getQueryId() {
        return queryId;
    }
    

    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }
    

    public int getCustomerId() {
        return customerId;
    }
    

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    

    public int getVehicleId() {
        return vehicleId;
    }
    

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
    

    public LocalDate getQueryDate() {
        return queryDate;
    }
    

    public void setQueryDate(LocalDate queryDate) {
        this.queryDate = queryDate;
    }
    

    public String getQueryText() {
        return queryText;
    }
    

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }
    

    public String getResponseText() {
        return responseText;
    }
    

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }
    

    public String getVehicleRegistration() {
        return vehicleRegistration;
    }
    

    public void setVehicleRegistration(String vehicleRegistration) {
        this.vehicleRegistration = vehicleRegistration;
    }
    

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
    
    /**
     * Gets the customer's name.
     * 
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }
    
    /**
     * Sets the customer's name.
     * 
     * @param customerName the new customer name
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    // Utility Methods
    
    /**
     * Checks if the query has been responded to.
     * 
     * @return true if response text is not empty
     */
    public boolean isResponded() {
        return responseText != null && !responseText.trim().isEmpty();
    }
    
    /**
     * Returns a string representation of the CustomerQuery.
     * 
     * @return a string containing query details
     */
    @Override
    public String toString() {
        return "CustomerQuery{" +
                "queryId=" + queryId +
                ", queryDate=" + queryDate +
                ", queryText='" + queryText + '\'' +
                ", responded=" + isResponded() +
                '}';
    }
}
