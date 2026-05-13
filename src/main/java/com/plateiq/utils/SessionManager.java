package com.plateiq.utils;

import com.plateiq.model.User;

// Manages user session state across controllers and scenes.
public class SessionManager {
    
    private static User currentUser;
    
    // Prevents instantiation of this utility class.
    private SessionManager() {
        // Prevents instantiation.
    }
    
    // Sets the current logged-in user.
    public static void setCurrentUser(User user) {
        currentUser = user;
        LOGGER.info("User session started: " + (user != null ? user.getUsername() : "null"));
    }
    
    // Retrieves the current logged-in user.
    public static User getCurrentUser() {
        return currentUser;
    }
    
    // Retrieves the current user's ID.
    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
    
    // Retrieves the current user's role.
    public static String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
    
    // Retrieves the current user's username.
    public static String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }
    
    // Checks if a user is currently logged in.
    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }
    
    // Checks if the current user has the specified role.
    public static boolean hasRole(String role) {
        return currentUser != null && currentUser.getRole() != null && 
               currentUser.getRole().equalsIgnoreCase(role);
    }
    
    // Logs out the current user.
    public static void logout() {
        if (currentUser != null) {
            currentUser.logout();
        }
        currentUser = null;
        LOGGER.info("User session ended");
    }
    
    // Logs a message with the current user context.
    public static void log(String message) {
        if (currentUser != null) {
            LOGGER.info("[" + currentUser.getUsername() + "] " + message);
        } else {
            LOGGER.info(message);
        }
    }
    
    // Logger field.
    private static final java.util.logging.Logger LOGGER = 
        java.util.logging.Logger.getLogger(SessionManager.class.getName());
}
