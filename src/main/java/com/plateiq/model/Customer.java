package com.plateiq.model;

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
    

    public int getCustomerId() {
        return customerId;
    }
    

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    

    public String getName() {
        return name;
    }
    

    public void setName(String name) {
        this.name = name;
    }
    

    public String getAddress() {
        return address;
    }
    

    public void setAddress(String address) {
        this.address = address;
    }
    

    public String getPhone() {
        return phone;
    }
    

    public void setPhone(String phone) {
        this.phone = phone;
    }
    

    public String getEmail() {
        return email;
    }
    

    public void setEmail(String email) {
        this.email = email;
    }
    

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
