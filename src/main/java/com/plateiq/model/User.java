package com.plateiq.model;

import java.time.LocalDate;

/**
 * Abstract base class representing a system user.
 * Provides common fields and abstract methods for all user roles.
 *
 * @author Plate IQ
 * @version 1.0
 */
public abstract class User {

    protected int id;
    protected String username;
    protected String password;
    protected String role;
    protected String status;

    /**
     * Default constructor.
     */
    public User() {
    }

    /**
     * Parameterized constructor for creating a User with all fields.
     *
     * @param id       the unique identifier of the user
     * @param username the username for login
     * @param password the hashed password
     * @param role     the role of the user (ADMIN, CUSTOMER, WORKSHOP, INSURANCE, POLICE)
     * @param status   the status of the user (ACTIVE, INACTIVE)
     */
    public User(int id, String username, String password, String role, String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    // ==================== Abstract Methods ====================

    /**
     * Abstract method to perform login action.
     */
    public abstract void login();

    /**
     * Abstract method to perform logout action.
     */
    public abstract void logout();

    /**
     * Abstract method to display the role-specific dashboard.
     *
     * @return the FXML path for the role's dashboard
     */
    public abstract String displayDashboard();

    // ==================== Concrete Methods ====================

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the user ID.
     *
     * @param id the user ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the role.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role.
     *
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns a string representation of the user.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}