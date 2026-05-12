package com.plateiq.service;

import com.plateiq.database.DBConnection;
import com.plateiq.model.PoliceReport;
import com.plateiq.model.Violation;

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
 * Service class for Police Report and Violation operations.
 * Handles reports, violations, and unpaid fine tracking.
 *
 * @author Plate IQ Team
 * @version 1.0
 */
public class PoliceService {
    
    private static final Logger LOGGER = Logger.getLogger(PoliceService.class.getName());
    
    // ==================== Police Report Methods ====================
    
    /**
     * Adds a new police report to the database.
     * 
     * @param report the PoliceReport object to add
     * @return true if report was added successfully, false otherwise
     */
    public boolean addReport(PoliceReport report) {
        String sql = "INSERT INTO police_report (vehicle_id, report_date, report_type, description, officer_name) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int vehicleId = report.getVehicleId() > 0 ? report.getVehicleId() : resolveVehicleId(report.getVehicleRegistration());
            stmt.setInt(1, vehicleId);
            stmt.setDate(2, java.sql.Date.valueOf(report.getReportDate()));
            stmt.setString(3, report.getReportType());
            stmt.setString(4, report.getDescription());
            stmt.setString(5, report.getOfficerName());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Added police report: " + report.getReportId());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add police report", e);
            return false;
        }
    }
    
    /**
     * Updates an existing police report in the database.
     * 
     * @param report the PoliceReport object with updated data
     * @return true if report was updated successfully, false otherwise
     */
    public boolean updateReport(PoliceReport report) {
        String sql = "UPDATE police_report SET report_date = ?, report_type = ?, description = ?, officer_name = ? " +
                     "WHERE report_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(report.getReportDate()));
            stmt.setString(2, report.getReportType());
            stmt.setString(3, report.getDescription());
            stmt.setString(4, report.getOfficerName());
            stmt.setInt(5, report.getReportId());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Updated police report: " + report.getReportId());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update police report: " + report.getReportId(), e);
            return false;
        }
    }
    
    /**
     * Deletes a police report from the database.
     * 
     * @param reportId the ID of the report to delete
     * @return true if report was deleted successfully, false otherwise
     */
    public boolean deleteReport(int reportId) {
        String sql = "DELETE FROM police_report WHERE report_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reportId);
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Deleted police report: " + reportId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete police report: " + reportId, e);
            return false;
        }
    }
    
    /**
     * Gets a police report by its ID.
     * 
     * @param reportId the report ID
     * @return PoliceReport object if found, null otherwise
     */
    public PoliceReport getReportById(int reportId) {
        String sql = "SELECT pr.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM police_report pr " +
                     "JOIN vehicle v ON pr.vehicle_id = v.vehicle_id " +
                     "WHERE pr.report_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reportId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildReportFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get report by ID: " + reportId, e);
        }
        
        return null;
    }
    
    /**
     * Gets all police reports for a specific vehicle.
     * 
     * @param vehicleId the vehicle ID
     * @return List of PoliceReport objects
     */
    public List<PoliceReport> getReportsByVehicle(int vehicleId) {
        String sql = "SELECT pr.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM police_report pr " +
                     "JOIN vehicle v ON pr.vehicle_id = v.vehicle_id " +
                     "WHERE pr.vehicle_id = ? " +
                     "ORDER BY pr.report_date DESC";
        
        List<PoliceReport> reports = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehicleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(buildReportFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get reports for vehicle: " + vehicleId, e);
        }
        
        return reports;
    }
    
    /**
     * Gets all police reports of a specific type.
     * 
     * @param reportType the report type (ACCIDENT, THEFT)
     * @return List of PoliceReport objects
     */
    public List<PoliceReport> getReportsByType(String reportType) {
        String sql = "SELECT pr.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM police_report pr " +
                     "JOIN vehicle v ON pr.vehicle_id = v.vehicle_id " +
                     "WHERE pr.report_type = ? " +
                     "ORDER BY pr.report_date DESC";
        
        List<PoliceReport> reports = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, reportType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(buildReportFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get reports by type: " + reportType, e);
        }
        
        return reports;
    }
    
    // ==================== Violation Methods ====================
    
    /**
     * Adds a new violation to the database.
     * 
     * @param violation the Violation object to add
     * @return true if violation was added successfully, false otherwise
     */
    public boolean addViolation(Violation violation) {
        String sql = "INSERT INTO violation (vehicle_id, violation_date, violation_type, fine_amount, status, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int vehicleId = violation.getVehicleId() > 0 ? violation.getVehicleId() : resolveVehicleId(violation.getVehicleRegistration());
            stmt.setInt(1, vehicleId);
            stmt.setDate(2, java.sql.Date.valueOf(violation.getViolationDate()));
            stmt.setString(3, violation.getViolationType());
            stmt.setBigDecimal(4, violation.getFineAmount());
            stmt.setString(5, violation.getStatus());
            stmt.setString(6, violation.getDescription());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Added violation: " + violation.getViolationId());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add violation", e);
            return false;
        }
    }
    
    /**
     * Updates an existing violation in the database.
     * 
     * @param violation the Violation object with updated data
     * @return true if violation was updated successfully, false otherwise
     */
    public boolean updateViolation(Violation violation) {
        String sql = "UPDATE violation SET violation_date = ?, violation_type = ?, fine_amount = ?, " +
                     "status = ?, description = ? WHERE violation_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(violation.getViolationDate()));
            stmt.setString(2, violation.getViolationType());
            stmt.setBigDecimal(3, violation.getFineAmount());
            stmt.setString(4, violation.getStatus());
            stmt.setString(5, violation.getDescription());
            stmt.setInt(6, violation.getViolationId());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Updated violation: " + violation.getViolationId());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update violation: " + violation.getViolationId(), e);
            return false;
        }
    }
    
    /**
     * Deletes a violation from the database.
     * 
     * @param violationId the ID of the violation to delete
     * @return true if violation was deleted successfully, false otherwise
     */
    public boolean deleteViolation(int violationId) {
        String sql = "DELETE FROM violation WHERE violation_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, violationId);
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Deleted violation: " + violationId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete violation: " + violationId, e);
            return false;
        }
    }
    
    /**
     * Gets all unpaid violations from the database view.
     * 
     * @return List of Violation objects (unpaid only)
     */
    public List<Violation> getUnpaidViolations() {
        String sql = "SELECT * FROM unpaid_violations";
        
        List<Violation> violations = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    violations.add(buildViolationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get unpaid violations", e);
        }
        
        return violations;
    }
    
    /**
     * Gets all violations for a specific vehicle.
     * 
     * @param vehicleId the vehicle ID
     * @return List of Violation objects
     */
    public List<Violation> getViolationsByVehicle(int vehicleId) {
        String sql = "SELECT v.*, vr.registration_number AS vehicle_registration, " +
                     "vr.make AS vehicle_make, vr.model AS vehicle_model " +
                     "FROM violation v " +
                     "JOIN vehicle vr ON v.vehicle_id = vr.vehicle_id " +
                     "WHERE v.vehicle_id = ? " +
                     "ORDER BY v.violation_date DESC";
        
        List<Violation> violations = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehicleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    violations.add(buildViolationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get violations for vehicle: " + vehicleId, e);
        }
        
        return violations;
    }
    
    /**
     * Gets all violations with a specific status.
     * 
     * @param status the violation status (PAID, UNPAID)
     * @return List of Violation objects
     */
    public List<Violation> getViolationsByStatus(String status) {
        String sql = "SELECT v.*, vr.registration_number AS vehicle_registration, " +
                     "vr.make AS vehicle_make, vr.model AS vehicle_model " +
                     "FROM violation v " +
                     "JOIN vehicle vr ON v.vehicle_id = vr.vehicle_id " +
                     "WHERE v.status = ? " +
                     "ORDER BY v.violation_date DESC";
        
        List<Violation> violations = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    violations.add(buildViolationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get violations by status: " + status, e);
        }
        
        return violations;
    }
    
    /**
     * Marks a violation as paid.
     * 
     * @param violationId the violation ID
     * @return true if update was successful, false otherwise
     */
    public boolean markViolationAsPaid(int violationId) {
        String sql = "UPDATE violation SET status = 'PAID' WHERE violation_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, violationId);
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Marked violation as paid: " + violationId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to mark violation as paid: " + violationId, e);
            return false;
        }
    }
    
    /**
     * Gets the total unpaid fine amount.
     * 
     * @return total unpaid fine amount as BigDecimal
     */
    public BigDecimal getTotalUnpaidFines() {
        String sql = "SELECT COALESCE(SUM(fine_amount), 0) FROM violation WHERE status = 'UNPAID'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get total unpaid fines", e);
        }
        
        return BigDecimal.ZERO;
    }

    public List<PoliceReport> getAllReports() {
        String sql = "SELECT pr.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM police_report pr " +
                     "JOIN vehicle v ON pr.vehicle_id = v.vehicle_id " +
                     "ORDER BY pr.report_date DESC";
        List<PoliceReport> reports = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                reports.add(buildReportFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get all reports", e);
        }
        return reports;
    }

    public List<Violation> getAllViolations() {
        String sql = "SELECT v.*, vr.registration_number AS vehicle_registration, " +
                     "vr.make AS vehicle_make, vr.model AS vehicle_model " +
                     "FROM violation v JOIN vehicle vr ON v.vehicle_id = vr.vehicle_id " +
                     "ORDER BY v.violation_date DESC";
        List<Violation> violations = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                violations.add(buildViolationFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get all violations", e);
        }
        return violations;
    }

    public List<PoliceReport> searchReportsByVehicle(String searchTerm) {
        String sql = "SELECT pr.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM police_report pr " +
                     "JOIN vehicle v ON pr.vehicle_id = v.vehicle_id " +
                     "WHERE v.registration_number ILIKE ? ORDER BY pr.report_date DESC";
        List<PoliceReport> reports = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(buildReportFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to search reports by vehicle", e);
        }
        return reports;
    }

    public List<Violation> searchViolationsByVehicle(String searchTerm) {
        String sql = "SELECT v.*, vr.registration_number AS vehicle_registration, " +
                     "vr.make AS vehicle_make, vr.model AS vehicle_model " +
                     "FROM violation v JOIN vehicle vr ON v.vehicle_id = vr.vehicle_id " +
                     "WHERE vr.registration_number ILIKE ? ORDER BY v.violation_date DESC";
        List<Violation> violations = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    violations.add(buildViolationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to search violations by vehicle", e);
        }
        return violations;
    }
    
    // ==================== Helper Methods ====================
    
    /**
     * Builds a PoliceReport object from a ResultSet.
     * 
     * @param rs the ResultSet
     * @return PoliceReport object
     * @throws SQLException if there's an error reading the data
     */
    private PoliceReport buildReportFromResultSet(ResultSet rs) throws SQLException {
        int reportId = rs.getInt("report_id");
        int vehicleId = rs.getInt("vehicle_id");
        java.sql.Date reportDateSql = rs.getDate("report_date");
        String reportType = rs.getString("report_type");
        String description = rs.getString("description");
        String officerName = rs.getString("officer_name");
        String vehicleRegistration = rs.getString("vehicle_registration");
        String vehicleMake = rs.getString("vehicle_make");
        String vehicleModel = rs.getString("vehicle_model");
        
        LocalDate reportDate = reportDateSql != null ? reportDateSql.toLocalDate() : null;
        
        return new PoliceReport(reportId, vehicleId, reportDate, reportType, description, officerName,
                               vehicleRegistration, vehicleMake, vehicleModel);
    }
    
    /**
     * Builds a Violation object from a ResultSet.
     * 
     * @param rs the ResultSet
     * @return Violation object
     * @throws SQLException if there's an error reading the data
     */
    private Violation buildViolationFromResultSet(ResultSet rs) throws SQLException {
        int violationId = rs.getInt("violation_id");
        int vehicleId = rs.getInt("vehicle_id");
        java.sql.Date violationDateSql = rs.getDate("violation_date");
        String violationType = rs.getString("violation_type");
        BigDecimal fineAmount = rs.getBigDecimal("fine_amount");
        String status = rs.getString("status");
        String description = rs.getString("description");
        String vehicleRegistration = rs.getString("vehicle_registration");
        String vehicleMake = rs.getString("vehicle_make");
        String vehicleModel = rs.getString("vehicle_model");
        
        LocalDate violationDate = violationDateSql != null ? violationDateSql.toLocalDate() : null;
        
        return new Violation(violationId, vehicleId, violationDate, violationType, fineAmount, status,
                            description, vehicleRegistration, vehicleMake, vehicleModel);
    }

    private int resolveVehicleId(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.isBlank()) {
            return 0;
        }
        String sql = "SELECT vehicle_id FROM vehicle WHERE registration_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, registrationNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("vehicle_id");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to resolve vehicle id for police record", e);
        }
        return 0;
    }
}
