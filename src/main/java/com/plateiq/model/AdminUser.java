package com.plateiq.model;

/**
 * Admin user class for system administrators.
 * Inherits from User and provides admin-specific dashboard.
 *
 * @author Plate IQ
 * @version 1.0
 */
public class AdminUser extends User {

    /**
     * Default constructor.
     */
    public AdminUser() {
    }

    /**
     * Parameterized constructor for AdminUser.
     *
     * @param id       the unique identifier of the admin
     * @param username the username for login
     * @param password the hashed password
     * @param role     the role of the user (must be ADMIN)
     * @param status   the status of the user (ACTIVE, INACTIVE)
     */
    public AdminUser(int id, String username, String password, String role, String status) {
        super(id, username, password, role, status);
    }

    @Override
    public void login() {
        System.out.println("Admin " + username + " logged in successfully.");
        // Add specific admin login logic if needed
    }

    @Override
    public void logout() {
        System.out.println("Admin " + username + " logged out successfully.");
        // Add specific admin logout logic if needed
    }

    @Override
    public String displayDashboard() {
        return "/fxml/dashboard.fxml";
    }
}