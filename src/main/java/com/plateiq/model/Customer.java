package com.plateiq.model;

/**
 * Entity class representing a Customer in the system.
 * Contains customer personal information and links to vehicles.
 *
 * @author Plate IQ Team
 * @version 1.0
 */
public class Customer {
    
    private int customerId;
    private String name;
    private String address;
    private String phone;
    private String email;
    
    /**
     * Default constructor.
     */
    public Customer() {
    }
    
    /**
     * Parameterized constructor for creating a Customer with all fields.
     * 
     * @param customerId the unique identifier for the customer
     * @param name the customer's full name
     * @param address the customer's address
     * @param phone the customer's phone number
     * @param email the customer's email address
     */
    public Customer(int customerId, String name, String address, String phone, String email) {
        this.customerId = customerId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }
    
    // Getters and Setters
    
    /**
     * Gets the customer's unique identifier.
     * 
     * @return the customer ID
     */
    public int getCustomerId() {
        return customerId;
    }
    
    /**
     * Sets the customer's unique identifier.
     * 
     * @param customerId the new customer ID
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    /**
     * Gets the customer's name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the customer's name.
     * 
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the customer's address.
     * 
     * @return the address
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Sets the customer's address.
     * 
     * @param address the new address
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Gets the customer's phone number.
     * 
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * Sets the customer's phone number.
     * 
     * @param phone the new phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**
     * Gets the customer's email address.
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the customer's email address.
     * 
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    // Utility Methods
    
    /**
     * Returns a string representation of the Customer.
     * 
     * @return a string containing customer details
     */
    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
