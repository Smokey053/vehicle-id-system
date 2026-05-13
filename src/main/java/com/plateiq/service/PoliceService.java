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

// Manages police reports and traffic violations.
public class PoliceService {
    
    private static final Logger LOGGER = Logger.getLogger(PoliceService.class.getName());

    private String normalizeViolationStatus(String status) {
        if (status == null || status.isBlank()) {
            return "UNPAID";
        }
        return status.trim().toUpperCase();
    }
    
    // Police report methods.
    
    // Adds a new police report to the database.
    public boolean addReport(PoliceReport report) {
        String sql = "INSERT INTO policereport (vehicle_id, report_date, report_type, description, officer_name) " +
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
    
    // Updates an existing police report in the database.
    public boolean updateReport(PoliceReport report) {
        String sql = "UPDATE policereport SET report_date = ?, report_type = ?, description = ?, officer_name = ? " +
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
    
    // Deletes a police report from the database.
    public boolean deleteReport(int reportId) {
        String sql = "DELETE FROM policereport WHERE report_id = ?";
        
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
    
    // Retrieves a police report by its ID.
    public PoliceReport getReportById(int reportId) {
        String sql = "SELECT pr.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM policereport pr " +
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
    
    // Retrieves all police reports for a specific vehicle.
    public List<PoliceReport> getReportsByVehicle(int vehicleId) {
        String sql = "SELECT pr.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM policereport pr " +
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
    
    // Retrieves all police reports of a specific type.
    public List<PoliceReport> getReportsByType(String reportType) {
        String sql = "SELECT pr.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM policereport pr " +
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
    
    // Violation methods.
    
    // Adds a new violation to the database.
    public boolean addViolation(Violation violation) {
        String sql = "INSERT INTO violation (vehicle_id, violation_date, violation_type, fine_amount, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int vehicleId = violation.getVehicleId() > 0 ? violation.getVehicleId() : resolveVehicleId(violation.getVehicleRegistration());
            stmt.setInt(1, vehicleId);
            stmt.setDate(2, java.sql.Date.valueOf(violation.getViolationDate()));
            stmt.setString(3, violation.getViolationType());
            stmt.setBigDecimal(4, violation.getFineAmount());
            stmt.setString(5, normalizeViolationStatus(violation.getStatus()));
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Added violation: " + violation.getViolationId());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add violation", e);
            return false;
        }
    }
    
    // Updates an existing violation in the database.
    public boolean updateViolation(Violation violation) {
        String sql = "UPDATE violation SET violation_date = ?, violation_type = ?, fine_amount = ?, " +
                     "status = ? WHERE violation_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(violation.getViolationDate()));
            stmt.setString(2, violation.getViolationType());
            stmt.setBigDecimal(3, violation.getFineAmount());
            stmt.setString(4, normalizeViolationStatus(violation.getStatus()));
            stmt.setInt(5, violation.getViolationId());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Updated violation: " + violation.getViolationId());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update violation: " + violation.getViolationId(), e);
            return false;
        }
    }
    
    // Deletes a violation from the database.
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
    
    // Retrieves all unpaid violations from the database view.
    public List<Violation> getUnpaidViolations() {
        String sql = "SELECT uv.*, v.vehicle_id, NULL::text AS description, " +
                     "uv.registration_number AS vehicle_registration, " +
                     "uv.make AS vehicle_make, uv.model AS vehicle_model " +
                     "FROM unpaid_violations uv " +
                     "JOIN vehicle v ON v.registration_number = uv.registration_number";
        
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
    
    // Gets all violations for a specific vehicle.
    public List<Violation> getViolationsByVehicle(int vehicleId) {
        String sql = "SELECT v.*, NULL::text AS description, vr.registration_number AS vehicle_registration, " +
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
    
    // Gets all violations with a specific status.
    public List<Violation> getViolationsByStatus(String status) {
        String sql = "SELECT v.*, NULL::text AS description, vr.registration_number AS vehicle_registration, " +
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
    
    // Marks a violation as paid.
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
    
    // Gets the total unpaid fine amount.
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
                     "FROM policereport pr " +
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
        String sql = "SELECT v.*, NULL::text AS description, vr.registration_number AS vehicle_registration, " +
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
                     "FROM policereport pr " +
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
        String sql = "SELECT v.*, NULL::text AS description, vr.registration_number AS vehicle_registration, " +
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
    
    // Helper methods.
    
    // Builds a PoliceReport object from a ResultSet.
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
    
    // Builds a Violation object from a ResultSet.
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
        String sql = "SELECT vehicle_id FROM vehicle WHERE registration_number ILIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, registrationNumber.trim());
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

