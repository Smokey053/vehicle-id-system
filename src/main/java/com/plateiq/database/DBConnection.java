package com.plateiq.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
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
    private static final String[] DATABASE_CANDIDATES = {
        "plateiq-db",
        "VehicleIdentificationSystem",
        "vehicleidentificationsystem"
    };
    
    static {
        loadProperties();
    }
    private static void loadProperties() {
        Properties props = new Properties();
        try (InputStream inputStream = DBConnection.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                throw new RuntimeException("Unable to find db.properties file in classpath");
            }
            ensureDriverLoaded();
            props.load(inputStream);
            
            dbHost = props.getProperty("db.host");
            dbPort = props.getProperty("db.port");
            dbDatabase = props.getProperty("db.database");
            dbUser = props.getProperty("db.user");
            dbPassword = props.getProperty("db.password");
            dbDatabase = resolveWorkingDatabase(dbHost, dbPort, dbDatabase, dbUser, dbPassword);
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
        ensureDriverLoaded();
        return DriverManager.getConnection(url, dbUser, dbPassword);
    }
    

    public static boolean testConnection(int timeoutSeconds) {
        try (Connection conn = getConnection()) {
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

    private static String resolveWorkingDatabase(String host, String port, String configuredDb, String user, String pass) {
        Set<String> orderedCandidates = new LinkedHashSet<>();
        if (configuredDb != null && !configuredDb.isBlank()) {
            orderedCandidates.add(configuredDb.trim());
        }
        for (String candidate : DATABASE_CANDIDATES) {
            orderedCandidates.add(candidate);
        }

        List<String> reachable = new ArrayList<>();
        for (String candidate : orderedCandidates) {
            String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, candidate);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                reachable.add(candidate);
                if (hasCoreTables(conn) && hasAnyData(conn)) {
                    LOGGER.info("Selected database with existing data: " + candidate);
                    return candidate;
                }
            } catch (SQLException ignored) {
            }
        }

        if (!reachable.isEmpty()) {
            String fallback = reachable.get(0);
            LOGGER.warning("No candidate database had assignment data; using reachable database: " + fallback);
            return fallback;
        }

        if (configuredDb != null && !configuredDb.isBlank()) {
            return configuredDb.trim();
        }
        return DATABASE_CANDIDATES[0];
    }

    private static boolean hasCoreTables(Connection conn) {
        String sql = "SELECT COUNT(*) FROM information_schema.tables " +
                     "WHERE table_schema = 'public' AND table_name IN ('users', 'vehicle', 'servicerecord')";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) >= 2;
            }
        } catch (SQLException ignored) {
        }
        return false;
    }

    private static boolean hasAnyData(Connection conn) {
        String[] checks = {
            "SELECT COUNT(*) FROM users",
            "SELECT COUNT(*) FROM vehicle"
        };
        for (String sql : checks) {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            } catch (SQLException ignored) {
            }
        }
        return false;
    }

    private static void ensureDriverLoaded() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "PostgreSQL JDBC Driver not found", e);
            throw new RuntimeException("PostgreSQL JDBC Driver not found. Please add it to your classpath.", e);
        }
    }
}
