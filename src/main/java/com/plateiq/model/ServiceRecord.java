package com.plateiq.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity class representing a Service Record for a vehicle.
 * Contains all service-related information including costs and descriptions.
 *
 * @author Plate IQ Team
 * @version 1.0
 */
public class ServiceRecord {
    
    private int serviceId;
    private int vehicleId;
    private LocalDate serviceDate;
    private String serviceType;
    private String description;
    private BigDecimal cost;
    private String vehicleRegistration;
    private String vehicleMake;
    private String vehicleModel;
    
    /**
     * Default constructor.
     */
    public ServiceRecord() {
    }
    
    /**
     * Parameterized constructor for creating a ServiceRecord with all fields.
     * 
     * @param serviceId the unique identifier for the service record
     * @param vehicleId the ID of the vehicle being serviced
     * @param serviceDate the date the service was performed
     * @param serviceType the type of service performed
     * @param description detailed description of the service
     * @param cost the cost of the service
     * @param vehicleRegistration the vehicle's registration number
     * @param vehicleMake the vehicle's make
     * @param vehicleModel the vehicle's model
     */
    public ServiceRecord(int serviceId, int vehicleId, LocalDate serviceDate, 
                        String serviceType, String description, BigDecimal cost,
                        String vehicleRegistration, String vehicleMake, String vehicleModel) {
        this.serviceId = serviceId;
        this.vehicleId = vehicleId;
        this.serviceDate = serviceDate;
        this.serviceType = serviceType;
        this.description = description;
        this.cost = cost;
        this.vehicleRegistration = vehicleRegistration;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
    }

    public ServiceRecord(int serviceId, int vehicleId, LocalDate serviceDate,
                        String serviceType, String description, double cost) {
        this(serviceId, vehicleId, serviceDate, serviceType, description, BigDecimal.valueOf(cost), null, null, null);
    }
    
    // Getters and Setters
    
    /**
     * Gets the service record's unique identifier.
     * 
     * @return the service ID
     */
    public int getServiceId() {
        return serviceId;
    }
    
    /**
     * Sets the service record's unique identifier.
     * 
     * @param serviceId the new service ID
     */
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }
    
    /**
     * Gets the vehicle ID associated with this service.
     * 
     * @return the vehicle ID
     */
    public int getVehicleId() {
        return vehicleId;
    }
    
    /**
     * Sets the vehicle ID associated with this service.
     * 
     * @param vehicleId the new vehicle ID
     */
    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }
    
    /**
     * Gets the service date.
     * 
     * @return the service date
     */
    public LocalDate getServiceDate() {
        return serviceDate;
    }
    
    /**
     * Sets the service date.
     * 
     * @param serviceDate the new service date
     */
    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }
    
    /**
     * Gets the type of service performed.
     * 
     * @return the service type
     */
    public String getServiceType() {
        return serviceType;
    }
    
    /**
     * Sets the type of service performed.
     * 
     * @param serviceType the new service type
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    
    /**
     * Gets the detailed description of the service.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the detailed description of the service.
     * 
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the cost of the service.
     * 
     * @return the cost as BigDecimal
     */
    public BigDecimal getCost() {
        return cost;
    }
    
    /**
     * Sets the cost of the service.
     * 
     * @param cost the new cost
     */
    public void setCost(BigDecimal cost) {
        this.cost = cost;
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
     * Returns a string representation of the ServiceRecord.
     * 
     * @return a string containing service details
     */
    @Override
    public String toString() {
        return "ServiceRecord{" +
                "serviceId=" + serviceId +
                ", vehicleId=" + vehicleId +
                ", serviceDate=" + serviceDate +
                ", serviceType='" + serviceType + '\'' +
                ", cost=" + cost +
                '}';
    }
}
