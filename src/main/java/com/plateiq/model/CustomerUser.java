package com.plateiq.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Customer user class for system customers.
 * Inherits from User and provides customer-specific dashboard.
 *
 * @author Plate IQ
 * @version 1.0
 */
public class CustomerUser extends User {

    private int customerId;
    private String name;
    private String address;
    private String phone;
    private String email;

    /**
     * Default constructor.
     */
    public CustomerUser() {
        super();
    }

    /**
     * Parameterized constructor for CustomerUser.
     *
     * @param id       the unique identifier of the user
     * @param username the username for login
     * @param password the hashed password
     * @param role     the role of the user (must be CUSTOMER)
     * @param status   the status of the user (ACTIVE, INACTIVE)
     * @param customerId the customer's ID in the database
     * @param name the customer's full name
     * @param address the customer's address
     * @param phone the customer's phone number
     * @param email the customer's email address
     */
    public CustomerUser(int id, String username, String password, String role, String status,
                       int customerId, String name, String address, String phone, String email) {
        super(id, username, password, role, status);
        this.customerId = customerId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public void login() {
        System.out.println("Customer " + username + " logged in successfully.");
        // Add specific customer login logic if needed
    }

    @Override
    public void logout() {
        System.out.println("Customer " + username + " logged out successfully.");
        // Add specific customer logout logic if needed
    }

    @Override
    public String displayDashboard() {
        return "/fxml/dashboard.fxml";
    }

    // Getters and Setters

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
}
