package com.plateiq.service;

import com.plateiq.database.DBConnection;
import com.plateiq.model.User;
import com.plateiq.model.AdminUser;
import com.plateiq.model.CustomerUser;
import com.plateiq.model.InsuranceUser;
import com.plateiq.model.PoliceUser;
import com.plateiq.model.WorkshopUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

// Authenticates users and manages user records.
public class UserService {
    
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    
    // Authenticates a user by username and password.
    public User authenticate(String username, String password) {
        String sql = "SELECT u.user_id, u.username, u.password, u.role, u.status, " +
                     "c.customer_id, c.name, c.address, c.phone, c.email " +
                     "FROM users u " +
                     "LEFT JOIN customer c ON u.user_id = c.customer_id " +
                     "WHERE u.username = ? AND u.password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String userUsername = rs.getString("username");
                    String userPassword = rs.getString("password");
                    String role = rs.getString("role");
                    String status = rs.getString("status");
                    
                    // Create the user subtype for the role.
                    User user = createUserByRole(userId, userUsername, userPassword, role, status, rs);
                    
                    LOGGER.info("User authenticated: " + userUsername + " with role: " + role);
                    return user;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Authentication failed for username: " + username, e);
        }
        
        return null;
    }
    
    // Creates the appropriate User subclass based on the role.
    private User createUserByRole(int userId, String username, String password, 
                                  String role, String status, ResultSet rs) {
        try {
            switch (role.toUpperCase()) {
                case "ADMIN":
                    return new AdminUser(userId, username, password, role, status);
                case "CUSTOMER":
                    int customerId = rs.getInt("customer_id");
                    String name = rs.getString("name");
                    String address = rs.getString("address");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    return new CustomerUser(userId, username, password, role, status,
                                          customerId, name, address, phone, email);
                case "WORKSHOP":
                    return new WorkshopUser(userId, username, password, role, status);
                case "INSURANCE":
                    return new InsuranceUser(userId, username, password, role, status);
                case "POLICE":
                    return new PoliceUser(userId, username, password, role, status);
                default:
                    LOGGER.warning("Unknown role: " + role);
                    return new AdminUser(userId, username, password, role, status);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating user object", e);
            return null;
        }
    }
    
    // Updates a user's status in the database.
    public boolean updateUserStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Updated user " + userId + " status to: " + status);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update user status for user ID: " + userId, e);
            return false;
        }
    }
    
    // Retrieves a user by their ID.
    public User getUserById(int userId) {
        String sql = "SELECT u.user_id, u.username, u.password, u.role, u.status, " +
                     "c.customer_id, c.name, c.address, c.phone, c.email " +
                     "FROM users u " +
                     "LEFT JOIN customer c ON u.user_id = c.customer_id " +
                     "WHERE u.user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createUserByRole(userId, rs.getString("username"), 
                                         rs.getString("password"), 
                                         rs.getString("role"), 
                                         rs.getString("status"), rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get user by ID: " + userId, e);
        }
        
        return null;
    }
    
    // Retrieves a user by their username.
    public User getUserByUsername(String username) {
        String sql = "SELECT u.user_id, u.username, u.password, u.role, u.status, " +
                     "c.customer_id, c.name, c.address, c.phone, c.email " +
                     "FROM users u " +
                     "LEFT JOIN customer c ON u.user_id = c.customer_id " +
                     "WHERE u.username = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createUserByRole(rs.getInt("user_id"), rs.getString("username"), 
                                         rs.getString("password"), 
                                         rs.getString("role"), 
                                         rs.getString("status"), rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get user by username: " + username, e);
        }
        
        return null;
    }
}
