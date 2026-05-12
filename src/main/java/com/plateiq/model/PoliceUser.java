package com.plateiq.model;

/**
 * Police user class for police officers and staff.
 * Inherits from User and provides police-specific dashboard.
 *
 * @author Plate IQ
 * @version 1.0
 */
public class PoliceUser extends User {

    /**
     * Default constructor.
     */
    public PoliceUser() {
        super();
    }

    /**
     * Parameterized constructor for PoliceUser.
     *
     * @param id       the unique identifier of the user
     * @param username the username for login
     * @param password the hashed password
     * @param role     the role of the user (must be POLICE)
     * @param status   the status of the user (ACTIVE, INACTIVE)
     */
    public PoliceUser(int id, String username, String password, String role, String status) {
        super(id, username, password, role, status);
    }

    @Override
    public void login() {
        System.out.println("Police user " + username + " logged in successfully.");
        // Add specific police login logic if needed
    }

    @Override
    public void logout() {
        System.out.println("Police user " + username + " logged out successfully.");
        // Add specific police logout logic if needed
    }

    @Override
    public String displayDashboard() {
        return "/fxml/dashboard.fxml";
    }
}
