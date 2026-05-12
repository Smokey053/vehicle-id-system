package com.plateiq.model;

import java.time.LocalDate;

/**
 * Entity class representing a Customer Query.
 * Contains query information submitted by customers regarding vehicles.
 *
 * @author Plate IQ Team
 * @version 1.0
 */
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
    
    /**
     * Default constructor.
     */
    public CustomerQuery() {
    }
    
    /**
     * Parameterized constructor for creating a CustomerQuery with all fields.
     * 
     * @param queryId the unique identifier for the query
     * @param customerId the ID of the customer who submitted the query
     * @param vehicleId the ID of the vehicle the query is about
     * @param queryDate the date the query was submitted
     * @param queryText the text of the customer's query
     * @param responseText the response text (if provided)
     * @param vehicleRegistration the vehicle's registration number
     * @param vehicleMake the vehicle's make
     * @param vehicleModel the vehicle's model
     * @param customerName the customer's name
     */
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
    
    // Getters and Setters
    
    /**
     * Gets the query's unique identifier.
     * 
     * @return the query ID
     */
    public int getQueryId() {
        return queryId;
    }
    
    /**
     * Sets the query's unique identifier.
     * 
     * @param queryId the new query ID
     */
    public void setQueryId(int queryId) {
        this.queryId = queryId;
    }
    
    /**
     * Gets the customer ID who submitted the query.
     * 
     * @return the customer ID
     */
    public int getCustomerId() {
        return customerId;
    }
    
    /**
     * Sets the customer ID who submitted the query.
     * 
     * @param customerId the new customer ID
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    /**
     * Gets the vehicle ID associated with this query.
     * 
     * @return the vehicle ID
     */
    public int getVehicleId() {
        return vehicleId;
    }
    
    /**
     * Sets the vehicle ID associated with this query.
     * 
     * @param vehicleId the new vehicle ID
     */
    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    /**
     * Gets the query date.
     * 
     * @return the query date
     */
    public LocalDate getQueryDate() {
        return queryDate;
    }
    
    /**
     * Sets the query date.
     * 
     * @param queryDate the new query date
     */
    public void setQueryDate(LocalDate queryDate) {
        this.queryDate = queryDate;
    }
    
    /**
     * Gets the query text.
     * 
     * @return the query text
     */
    public String getQueryText() {
        return queryText;
    }
    
    /**
     * Sets the query text.
     * 
     * @param queryText the new query text
     */
    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }
    
    /**
     * Gets the response text.
     * 
     * @return the response text
     */
    public String getResponseText() {
        return responseText;
    }
    
    /**
     * Sets the response text.
     * 
     * @param responseText the new response text
     */
    public void setResponseText(String responseText) {
        this.responseText = responseText;
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
