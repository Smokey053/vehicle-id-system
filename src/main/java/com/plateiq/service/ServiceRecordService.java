package com.plateiq.service;

import com.plateiq.database.DBConnection;
import com.plateiq.model.ServiceRecord;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for Service Record CRUD operations.
 * Handles service history management with pagination support.
 *
 * @author Plate IQ Team
 * @version 1.0
 */
public class ServiceRecordService {
    
    private static final Logger LOGGER = Logger.getLogger(ServiceRecordService.class.getName());
    
    /**
     * Adds a new service record to the database.
     * 
     * @param serviceRecord the ServiceRecord object to add
     * @return true if service record was added successfully, false otherwise
     */
    public boolean addServiceRecord(ServiceRecord serviceRecord) {
        String sql = "INSERT INTO service_record (vehicle_id, service_date, service_type, description, cost) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, serviceRecord.getVehicleId());
            stmt.setDate(2, java.sql.Date.valueOf(serviceRecord.getServiceDate()));
            stmt.setString(3, serviceRecord.getServiceType());
            stmt.setString(4, serviceRecord.getDescription());
            stmt.setBigDecimal(5, serviceRecord.getCost());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Added service record for vehicle: " + serviceRecord.getVehicleId());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add service record for vehicle: " + serviceRecord.getVehicleId(), e);
            return false;
        }
    }
    
    /**
     * Updates an existing service record in the database.
     * 
     * @param serviceRecord the ServiceRecord object with updated data
     * @return true if service record was updated successfully, false otherwise
     */
    public boolean updateServiceRecord(ServiceRecord serviceRecord) {
        String sql = "UPDATE service_record SET service_date = ?, service_type = ?, description = ?, cost = ? " +
                     "WHERE service_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(serviceRecord.getServiceDate()));
            stmt.setString(2, serviceRecord.getServiceType());
            stmt.setString(3, serviceRecord.getDescription());
            stmt.setBigDecimal(4, serviceRecord.getCost());
            stmt.setInt(5, serviceRecord.getServiceId());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Updated service record: " + serviceRecord.getServiceId());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update service record: " + serviceRecord.getServiceId(), e);
            return false;
        }
    }
    
    /**
     * Deletes a service record from the database.
     * 
     * @param serviceId the ID of the service record to delete
     * @return true if service record was deleted successfully, false otherwise
     */
    public boolean deleteServiceRecord(int serviceId) {
        String sql = "DELETE FROM service_record WHERE service_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, serviceId);
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Deleted service record: " + serviceId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete service record: " + serviceId, e);
            return false;
        }
    }
    
    /**
     * Gets all service records for a specific vehicle with pagination.
     * 
     * @param vehicleId the vehicle ID
     * @param page the page number (0-indexed)
     * @param pageSize the number of items per page
     * @return List of ServiceRecord objects for the requested page
     */
    public List<ServiceRecord> getServiceRecordsByVehicle(int vehicleId, int page, int pageSize) {
        String sql = "SELECT sr.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM service_record sr " +
                     "JOIN vehicle v ON sr.vehicle_id = v.vehicle_id " +
                     "WHERE sr.vehicle_id = ? " +
                     "ORDER BY sr.service_date DESC " +
                     "LIMIT ? OFFSET ?";
        
        List<ServiceRecord> serviceRecords = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehicleId);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, page * pageSize);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    serviceRecords.add(buildServiceRecordFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get service records for vehicle: " + vehicleId, e);
        }
        
        return serviceRecords;
    }
    
    /**
     * Gets the total count of service records for a specific vehicle.
     * 
     * @param vehicleId the vehicle ID
     * @return total number of service records
     */
    public int getTotalServiceRecordCount(int vehicleId) {
        String sql = "SELECT COUNT(*) FROM service_record WHERE vehicle_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehicleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get total service record count for vehicle: " + vehicleId, e);
        }
        
        return 0;
    }

    public List<ServiceRecord> getServiceByVehicleId(int vehicleId) {
        return getServiceRecordsByVehicle(vehicleId, 0, Integer.MAX_VALUE / 4);
    }

    public List<ServiceRecord> getAllServiceRecords() {
        String sql = "SELECT sr.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM service_record sr " +
                     "JOIN vehicle v ON sr.vehicle_id = v.vehicle_id " +
                     "ORDER BY sr.service_date DESC";

        List<ServiceRecord> serviceRecords = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                serviceRecords.add(buildServiceRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get all service records", e);
        }
        return serviceRecords;
    }

    public List<ServiceRecord> searchByVehicle(String searchTerm) {
        String sql = "SELECT sr.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM service_record sr " +
                     "JOIN vehicle v ON sr.vehicle_id = v.vehicle_id " +
                     "WHERE v.registration_number ILIKE ? " +
                     "ORDER BY sr.service_date DESC";

        List<ServiceRecord> serviceRecords = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    serviceRecords.add(buildServiceRecordFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to search service records by vehicle", e);
        }
        return serviceRecords;
    }
    
    /**
     * Gets a service record by its ID.
     * 
     * @param serviceId the service record ID
     * @return ServiceRecord object if found, null otherwise
     */
    public ServiceRecord getServiceRecordById(int serviceId) {
        String sql = "SELECT sr.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM service_record sr " +
                     "JOIN vehicle v ON sr.vehicle_id = v.vehicle_id " +
                     "WHERE sr.service_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, serviceId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildServiceRecordFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get service record by ID: " + serviceId, e);
        }
        
        return null;
    }
    
    /**
     * Gets the total cost of all services for a vehicle.
     * 
     * @param vehicleId the vehicle ID
     * @return total cost as BigDecimal
     */
    public BigDecimal getTotalServiceCost(int vehicleId) {
        String sql = "SELECT COALESCE(SUM(cost), 0) FROM service_record WHERE vehicle_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehicleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get total service cost for vehicle: " + vehicleId, e);
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Builds a ServiceRecord object from a ResultSet.
     * 
     * @param rs the ResultSet
     * @return ServiceRecord object
     * @throws SQLException if there's an error reading the data
     */
    private ServiceRecord buildServiceRecordFromResultSet(ResultSet rs) throws SQLException {
        int serviceId = rs.getInt("service_id");
        int vehicleId = rs.getInt("vehicle_id");
        java.sql.Date serviceDateSql = rs.getDate("service_date");
        String serviceType = rs.getString("service_type");
        String description = rs.getString("description");
        BigDecimal cost = rs.getBigDecimal("cost");
        String vehicleRegistration = rs.getString("vehicle_registration");
        String vehicleMake = rs.getString("vehicle_make");
        String vehicleModel = rs.getString("vehicle_model");
        
        LocalDate serviceDate = serviceDateSql != null ? serviceDateSql.toLocalDate() : null;
        
        return new ServiceRecord(serviceId, vehicleId, serviceDate, serviceType, 
                                description, cost, vehicleRegistration, vehicleMake, vehicleModel);
    }
}
