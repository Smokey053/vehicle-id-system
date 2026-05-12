package com.plateiq.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connection manager using singleton pattern with connection pooling readiness.
 * Loads database configuration from db.properties file.
 * 
 * @author Plate IQ Team
 * @version 1.0
 */
public class DBConnection {
    
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    
    private static final String PROPERTIES_FILE = "/db.properties";
    
    private static String dbHost;
    private static String dbPort;
    private static String dbDatabase;
    private static String dbUser;
    private static String dbPassword;
    
    private static Connection connection;
    
    // Static initialization block to load properties
    static {
        loadProperties();
    }
    
    /**
     * Loads database configuration from db.properties file.
     * Reads host, port, database name, user, and password.
     */
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
    
    /**
     * Private constructor to prevent instantiation.
     */
    private DBConnection() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Gets the database connection instance.
     * Creates a new connection if none exists or if the current connection is closed.
     * 
     * @return Connection to the PostgreSQL database
     * @throws SQLException if connection fails
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            createNewConnection();
        }
        return connection;
    }
    
    /**
     * Creates a new database connection using JDBC.
     * Uses connection pooling readiness pattern for future optimization.
     */
    private static void createNewConnection() {
        String url = String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbDatabase);
        
        try {
            // Load PostgreSQL driver (optional for modern JDBC)
            Class.forName("org.postgresql.Driver");
            
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            connection.setAutoCommit(true);
            
            LOGGER.info("Database connection established successfully");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "PostgreSQL JDBC Driver not found", e);
            throw new RuntimeException("PostgreSQL JDBC Driver not found. Please add it to your classpath.", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to establish database connection", e);
            throw new RuntimeException("Failed to connect to database at " + url, e);
        }
    }
    
    /**
     * Tests the database connection with a timeout.
     * Executes a simple query to verify connectivity.
     * 
     * @param timeoutSeconds the timeout in seconds
     * @return true if connection is valid, false otherwise
     */
    public static boolean testConnection(int timeoutSeconds) {
        try (Connection conn = getConnection()) {
            // Set socket timeout
            conn.setNetworkTimeout(null, timeoutSeconds * 1000);
            
            // Execute a simple test query
            conn.createStatement().execute("SELECT 1");
            
            LOGGER.info("Database connection test passed");
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database connection test failed", e);
            return false;
        }
    }
    
    /**
     * Closes the current database connection.
     * Should be called during application shutdown.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    LOGGER.info("Database connection closed");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing database connection", e);
            }
        }
    }
    
    /**
     * Gets the database URL for logging purposes.
     * 
     * @return the database URL (without password)
     */
    public static String getDbUrl() {
        return String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbDatabase);
    }
    
    /**
     * Gets the database user.
     * 
     * @return the database username
     */
    public static String getDbUser() {
        return dbUser;
    }
}
