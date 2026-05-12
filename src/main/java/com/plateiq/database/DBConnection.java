package com.plateiq.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    
    private static final String PROPERTIES_FILE = "/db.properties";
    
    private static String dbHost;
    private static String dbPort;
    private static String dbDatabase;
    private static String dbUser;
    private static String dbPassword;
    
    static {
        loadProperties();
    }
    private static void loadProperties() {
        Properties props = new Properties();
        try (InputStream inputStream = DBConnection.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                throw new RuntimeException("Unable to find db.properties file in classpath");
            }
            props.load(inputStream);
            
            dbHost = props.getProperty("db.host");
            dbPort = props.getProperty("db.port");
            dbDatabase = props.getProperty("db.database");
            dbUser = props.getProperty("db.user");
            dbPassword = props.getProperty("db.password");
            
            LOGGER.info("Database configuration loaded successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading db.properties file", e);
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }
    
    private DBConnection() {
    }
    

    public static Connection getConnection() throws SQLException {
        String url = String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbDatabase);
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "PostgreSQL JDBC Driver not found", e);
            throw new RuntimeException("PostgreSQL JDBC Driver not found. Please add it to your classpath.", e);
        }
        return DriverManager.getConnection(url, dbUser, dbPassword);
    }
    

    public static boolean testConnection(int timeoutSeconds) {
        try (Connection conn = getConnection()) {
            conn.setNetworkTimeout(null, timeoutSeconds * 1000);
            conn.createStatement().execute("SELECT 1");
            LOGGER.info("Database connection test passed");
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database connection test failed", e);
            return false;
        }
    }


    public static void closeConnection() {
        LOGGER.info("DBConnection.closeConnection() called; no persistent connection to close.");
    }
    

    public static String getDbUrl() {
        return String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbDatabase);
    }
    

    public static String getDbUser() {
        return dbUser;
    }
}
