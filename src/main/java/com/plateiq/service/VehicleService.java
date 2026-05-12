package com.plateiq.service;

import com.plateiq.database.DBConnection;
import com.plateiq.model.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for Vehicle CRUD operations.
 * Handles vehicle registration, updates, deletion, and search operations.
 *
 * @author Plate IQ Team
 * @version 1.0
 */
public class VehicleService {
    
    private static final Logger LOGGER = Logger.getLogger(VehicleService.class.getName());
    
    /**
     * Adds a new vehicle to the database.
     * 
     * @param vehicle the Vehicle object to add
     * @return true if vehicle was added successfully, false otherwise
     */
    public boolean addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicle (registration_number, make, model, year, color, owner_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicle.getRegistrationNumber());
            stmt.setString(2, vehicle.getMake());
            stmt.setString(3, vehicle.getModel());
            stmt.setInt(4, vehicle.getYear());
            stmt.setString(5, vehicle.getColor());
            stmt.setInt(6, vehicle.getOwnerId());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Added new vehicle: " + vehicle.getRegistrationNumber());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add vehicle: " + vehicle.getRegistrationNumber(), e);
            return false;
        }
    }
    
    /**
     * Updates an existing vehicle in the database.
     * 
     * @param vehicle the Vehicle object with updated data
     * @return true if vehicle was updated successfully, false otherwise
     */
    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicle SET make = ?, model = ?, year = ?, color = ?, owner_id = ? " +
                     "WHERE vehicle_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicle.getMake());
            stmt.setString(2, vehicle.getModel());
            stmt.setInt(3, vehicle.getYear());
            stmt.setString(4, vehicle.getColor());
            stmt.setInt(5, vehicle.getOwnerId());
            stmt.setInt(6, vehicle.getVehicleId());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Updated vehicle: " + vehicle.getVehicleId());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update vehicle: " + vehicle.getVehicleId(), e);
            return false;
        }
    }
    
    /**
     * Deletes a vehicle from the database.
     * 
     * @param vehicleId the ID of the vehicle to delete
     * @return true if vehicle was deleted successfully, false otherwise
     */
    public boolean deleteVehicle(int vehicleId) {
        String sql = "DELETE FROM vehicle WHERE vehicle_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehicleId);
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Deleted vehicle: " + vehicleId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete vehicle: " + vehicleId, e);
            return false;
        }
    }

    public boolean deleteVehicle(String registrationNumber) {
        Vehicle vehicle = getVehicleByRegistration(registrationNumber);
        return vehicle != null && deleteVehicle(vehicle.getVehicleId());
    }
    
    /**
     * Gets a vehicle by its registration number.
     * 
     * @param registrationNumber the registration number to search for
     * @return Vehicle object if found, null otherwise
     */
    public Vehicle getVehicleByRegistration(String registrationNumber) {
        String sql = "SELECT v.*, c.name AS owner_name, c.phone AS owner_phone " +
                     "FROM vehicle v " +
                     "LEFT JOIN customer c ON v.owner_id = c.customer_id " +
                     "WHERE v.registration_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, registrationNumber);
            
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
    
    /**
     * Gets all vehicles with pagination support.
     * 
     * @param page the page number (0-indexed)
     * @param pageSize the number of items per page
     * @return List of Vehicle objects for the requested page
     */
    public List<Vehicle> getAllVehicles(int page, int pageSize) {
        String sql = "SELECT v.*, c.name AS owner_name, c.phone AS owner_phone " +
                     "FROM vehicle v " +
                     "LEFT JOIN customer c ON v.owner_id = c.customer_id " +
                     "ORDER BY v.vehicle_id " +
                     "LIMIT ? OFFSET ?";
        
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pageSize);
            stmt.setInt(2, page * pageSize);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(buildVehicleFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get all vehicles", e);
        }
        
        return vehicles;
    }

    public List<Vehicle> getAllVehicles() {
        return getAllVehicles(0, Integer.MAX_VALUE / 4);
    }
    
    /**
     * Gets the total count of vehicles in the database.
     * 
     * @return total number of vehicles
     */
    public int getTotalVehicleCount() {
        String sql = "SELECT COUNT(*) FROM vehicle";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get total vehicle count", e);
        }
        
        return 0;
    }
    
    /**
     * Searches vehicles by registration number or owner name.
     * 
     * @param searchTerm the search term
     * @param page the page number
     * @param pageSize the number of items per page
     * @return List of matching Vehicle objects
     */
    public List<Vehicle> searchVehicles(String searchTerm, int page, int pageSize) {
        String sql = "SELECT v.*, c.name AS owner_name, c.phone AS owner_phone " +
                     "FROM vehicle v " +
                     "LEFT JOIN customer c ON v.owner_id = c.customer_id " +
                     "WHERE v.registration_number ILIKE ? OR c.name ILIKE ? " +
                     "ORDER BY v.vehicle_id " +
                     "LIMIT ? OFFSET ?";
        
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setInt(3, pageSize);
            stmt.setInt(4, page * pageSize);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(buildVehicleFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to search vehicles: " + searchTerm, e);
        }
        
        return vehicles;
    }

    public List<Vehicle> searchVehicles(String searchTerm) {
        return searchVehicles(searchTerm, 0, Integer.MAX_VALUE / 4);
    }
    
    /**
     * Gets the total count of vehicles matching a search term.
     * 
     * @param searchTerm the search term
     * @return total count of matching vehicles
     */
    public int getSearchResultCount(String searchTerm) {
        String sql = "SELECT COUNT(*) FROM vehicle v " +
                     "LEFT JOIN customer c ON v.owner_id = c.customer_id " +
                     "WHERE v.registration_number ILIKE ? OR c.name ILIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get search result count: " + searchTerm, e);
        }
        
        return 0;
    }
    
    /**
     * Builds a Vehicle object from a ResultSet.
     * 
     * @param rs the ResultSet
     * @return Vehicle object
     * @throws SQLException if there's an error reading the data
     */
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
}
