package com.plateiq.service;

import com.plateiq.database.DBConnection;
import com.plateiq.model.CustomerQuery;
import com.plateiq.model.InsurancePolicy;
import com.plateiq.model.ServiceRecord;
import com.plateiq.model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Manages customer vehicle lookups and query operations.
public class CustomerService {
    
    private static final Logger LOGGER = Logger.getLogger(CustomerService.class.getName());
    private final ServiceRecordService serviceRecordService = new ServiceRecordService();
    private final InsuranceService insuranceService = new InsuranceService();
    
    // Vehicle lookup methods.
    
    // Retrieves a vehicle by its registration number.
    public Vehicle getVehicleByRegistration(String registrationNumber) {
        String sql = "SELECT v.*, c.name AS owner_name, c.phone AS owner_phone, " +
                     "c.email AS owner_email " +
                     "FROM vehicle v " +
                     "LEFT JOIN customer c ON v.owner_id = c.customer_id " +
                     "WHERE v.registration_number ILIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, registrationNumber == null ? null : registrationNumber.trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildVehicleFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get vehicle by registration: " + registrationNumber, e);
        }
        
        return null;
    }

    public Vehicle getVehicleByPlate(String registrationNumber) {
        return getVehicleByRegistration(registrationNumber);
    }
    
    // Retrieves full vehicle details including insurance information.
    public Vehicle getVehicleFullDetails(String registrationNumber) {
        String sql = "SELECT vfd.vehicle_id, vfd.registration_number, vfd.make, vfd.model, " +
                     "vfd.year, vfd.color, vfd.customer_id AS owner_id, vfd.owner_name, " +
                     "vfd.owner_phone, vfd.owner_email " +
                     "FROM vehicle_full_details vfd " +
                     "WHERE vfd.registration_number ILIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, registrationNumber == null ? null : registrationNumber.trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Vehicle vehicle = buildVehicleFromResultSet(rs);
                    // Add extended insurance mapping when available.
                    return vehicle;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get full vehicle details: " + registrationNumber, e);
        }
        
        return null;
    }
    
    // Service history methods.
    
    /** Gets service history for a vehicle. */
    public List<ServiceRecord> getServiceHistory(int vehicleId) {
        return serviceRecordService.getServiceByVehicleId(vehicleId);
    }
    
    // Query submission methods.
    
    /** Submits a new customer query. */
    public boolean submitQuery(int customerId, int vehicleId, String queryText) {
        String sql = "INSERT INTO customerquery (customer_id, vehicle_id, query_date, query_text) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            stmt.setInt(2, vehicleId);
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setString(4, queryText);
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Submitted query from customer: " + customerId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to submit query from customer: " + customerId, e);
            return false;
        }
    }

    public boolean submitQuery(CustomerQuery query) {
        return submitQuery(query.getCustomerId(), query.getVehicleId(), query.getQueryText());
    }
    
    /** Gets all queries submitted by a customer. */
    public List<CustomerQuery> getQueriesByCustomer(int customerId) {
        String sql = "SELECT cq.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM customerquery cq " +
                     "JOIN vehicle v ON cq.vehicle_id = v.vehicle_id " +
                     "WHERE cq.customer_id = ? " +
                     "ORDER BY cq.query_date DESC";
        
        List<CustomerQuery> queries = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    queries.add(buildQueryFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get queries for customer: " + customerId, e);
        }
        
        return queries;
    }
    
    /** Gets a query by its ID. */
    public CustomerQuery getQueryById(int queryId) {
        String sql = "SELECT cq.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM customerquery cq " +
                     "JOIN vehicle v ON cq.vehicle_id = v.vehicle_id " +
                     "WHERE cq.query_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, queryId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildQueryFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get query by ID: " + queryId, e);
        }
        
        return null;
    }
    
    /** Responds to a customer query. */
    public boolean respondToQuery(int queryId, String responseText) {
        String sql = "UPDATE customerquery SET response_text = ? WHERE query_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, responseText);
            stmt.setInt(2, queryId);
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Responded to query: " + queryId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to respond to query: " + queryId, e);
            return false;
        }
    }
    
    // Insurance info methods.
    
    /** Gets insurance information for a vehicle. */
    public List<InsurancePolicy> getInsuranceInfo(int vehicleId) {
        return insuranceService.getPoliciesByVehicleId(vehicleId);
    }
    
    // Helper methods.
    
    /** Builds a Vehicle object from a ResultSet. */
    private Vehicle buildVehicleFromResultSet(ResultSet rs) throws SQLException {
        int vehicleId = rs.getInt("vehicle_id");
        String registrationNumber = rs.getString("registration_number");
        String make = rs.getString("make");
        String model = rs.getString("model");
        int year = rs.getInt("year");
        String color = rs.getString("color");
        int ownerId = rs.getInt("owner_id");
        String ownerName = rs.getString("owner_name");
        String ownerPhone = rs.getString("owner_phone");
        
        return new Vehicle(vehicleId, registrationNumber, make, model, year, color,
                          ownerId, ownerName, ownerPhone, null);
    }
    
    /** Builds a CustomerQuery object from a ResultSet. */
    private CustomerQuery buildQueryFromResultSet(ResultSet rs) throws SQLException {
        int queryId = rs.getInt("query_id");
        int customerId = rs.getInt("customer_id");
        int vehicleId = rs.getInt("vehicle_id");
        java.sql.Date queryDateSql = rs.getDate("query_date");
        String queryText = rs.getString("query_text");
        String responseText = rs.getString("response_text");
        String vehicleRegistration = rs.getString("vehicle_registration");
        String vehicleMake = rs.getString("vehicle_make");
        String vehicleModel = rs.getString("vehicle_model");
        
        LocalDate queryDate = queryDateSql != null ? queryDateSql.toLocalDate() : null;
        
        return new CustomerQuery(queryId, customerId, vehicleId, queryDate, queryText, 
                                responseText, vehicleRegistration, vehicleMake, vehicleModel, null);
    }
}

