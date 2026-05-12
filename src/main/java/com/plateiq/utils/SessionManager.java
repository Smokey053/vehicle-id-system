package com.plateiq.utils;

import com.plateiq.model.User;

/**
 * Utility class for managing user session state.
 * Maintains the logged-in user across different controllers and scenes.
 *
 * @author Plate IQ Team
 * @version 1.0
 */
public class SessionManager {
    
    private static User currentUser;
    
    /**
     * Private constructor to prevent instantiation.
     */
    private SessionManager() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Sets the current logged-in user.
     * 
     * @param user the logged-in user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
        LOGGER.info("User session started: " + (user != null ? user.getUsername() : "null"));
    }
    
    /**
     * Gets the current logged-in user.
     * 
     * @return the current user, or null if no user is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Gets the current user's ID.
     * 
     * @return the user ID, or -1 if no user is logged in
     */
    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
    
    /**
     * Gets the current user's role.
     * 
     * @return the user role, or null if no user is logged in
     */
    public static String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
    
    /**
     * Gets the current user's username.
     * 
     * @return the username, or null if no user is logged in
     */
    public static String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }
    
    /**
     * Checks if a user is currently logged in.
     * 
     * @return true if a user is logged in, false otherwise
     */
    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Checks if the current user has the specified role.
     * 
     * @param role the role to check
     * @return true if the current user has the role, false otherwise
     */
    public static boolean hasRole(String role) {
        return currentUser != null && currentUser.getRole() != null && 
               currentUser.getRole().equalsIgnoreCase(role);
    }
    
    /**
     * Logs out the current user.
     */
    public static void logout() {
        if (currentUser != null) {
            currentUser.logout();
        }
        currentUser = null;
        LOGGER.info("User session ended");
    }
    
    /**
     * Logs a message with the current user context.
     * 
     * @param message the message to log
     */
    public static void log(String message) {
        if (currentUser != null) {
            LOGGER.info("[" + currentUser.getUsername() + "] " + message);
        } else {
            LOGGER.info(message);
        }
    }
    
    // Logger field
    private static final java.util.logging.Logger LOGGER = 
        java.util.logging.Logger.getLogger(SessionManager.class.getName());
}
