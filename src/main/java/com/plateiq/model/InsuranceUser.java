package com.plateiq.model;

/**
 * Insurance user class for insurance agents and staff.
 * Inherits from User and provides insurance-specific dashboard.
 *
 * @author Plate IQ
 * @version 1.0
 */
public class InsuranceUser extends User {

    /**
     * Default constructor.
     */
    public InsuranceUser() {
        super();
    }

    /**
     * Parameterized constructor for InsuranceUser.
     *
     * @param id       the unique identifier of the user
     * @param username the username for login
     * @param password the hashed password
     * @param role     the role of the user (must be INSURANCE)
     * @param status   the status of the user (ACTIVE, INACTIVE)
     */
    public InsuranceUser(int id, String username, String password, String role, String status) {
        super(id, username, password, role, status);
    }

    @Override
    public void login() {
        System.out.println("Insurance user " + username + " logged in successfully.");
        // Add specific insurance login logic if needed
    }

    @Override
    public void logout() {
        System.out.println("Insurance user " + username + " logged out successfully.");
        // Add specific insurance logout logic if needed
    }

    @Override
    public String displayDashboard() {
        return "/fxml/dashboard.fxml";
    }
}
