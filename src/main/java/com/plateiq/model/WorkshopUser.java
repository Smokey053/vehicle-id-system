package com.plateiq.model;

/**
 * Workshop user class for workshop mechanics and staff.
 * Inherits from User and provides workshop-specific dashboard.
 *
 * @author Plate IQ
 * @version 1.0
 */
public class WorkshopUser extends User {

    /**
     * Default constructor.
     */
    public WorkshopUser() {
        super();
    }

    /**
     * Parameterized constructor for WorkshopUser.
     *
     * @param id       the unique identifier of the user
     * @param username the username for login
     * @param password the hashed password
     * @param role     the role of the user (must be WORKSHOP)
     * @param status   the status of the user (ACTIVE, INACTIVE)
     */
    public WorkshopUser(int id, String username, String password, String role, String status) {
        super(id, username, password, role, status);
    }

    @Override
    public void login() {
        System.out.println("Workshop user " + username + " logged in successfully.");
        // Add specific workshop login logic if needed
    }

    @Override
    public void logout() {
        System.out.println("Workshop user " + username + " logged out successfully.");
        // Add specific workshop logout logic if needed
    }

    @Override
    public String displayDashboard() {
        return "/fxml/dashboard.fxml";
    }
}
