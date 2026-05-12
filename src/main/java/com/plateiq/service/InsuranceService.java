package com.plateiq.service;

import com.plateiq.database.DBConnection;
import com.plateiq.model.InsurancePolicy;
import com.plateiq.model.Claim;

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
 * Service class for Insurance Policy and Claim operations.
 * Handles policy CRUD, claims management, and expiry checking.
 *
 * @author Plate IQ Team
 * @version 1.0
 */
public class InsuranceService {
    
    private static final Logger LOGGER = Logger.getLogger(InsuranceService.class.getName());
    
    // ==================== Insurance Policy Methods ====================
    
    /**
     * Adds a new insurance policy to the database.
     * 
     * @param policy the InsurancePolicy object to add
     * @return true if policy was added successfully, false otherwise
     */
    public boolean addPolicy(InsurancePolicy policy) {
        String sql = "INSERT INTO insurance_policy (vehicle_id, insurance_company, policy_number, " +
                     "start_date, end_date, coverage_details, premium_amount, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int vehicleId = policy.getVehicleId() > 0 ? policy.getVehicleId() : resolveVehicleId(policy.getVehicleRegistration());
            stmt.setInt(1, vehicleId);
            stmt.setString(2, policy.getInsuranceCompany());
            stmt.setString(3, policy.getPolicyNumber());
            stmt.setDate(4, java.sql.Date.valueOf(policy.getStartDate()));
            stmt.setDate(5, java.sql.Date.valueOf(policy.getEndDate()));
            stmt.setString(6, policy.getCoverageDetails());
            stmt.setBigDecimal(7, policy.getPremiumAmount());
            stmt.setString(8, policy.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Added insurance policy: " + policy.getPolicyNumber());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add insurance policy: " + policy.getPolicyNumber(), e);
            return false;
        }
    }
    
    /**
     * Updates an existing insurance policy in the database.
     * 
     * @param policy the InsurancePolicy object with updated data
     * @return true if policy was updated successfully, false otherwise
     */
    public boolean updatePolicy(InsurancePolicy policy) {
        String sql = "UPDATE insurance_policy SET insurance_company = ?, policy_number = ?, " +
                     "start_date = ?, end_date = ?, coverage_details = ?, premium_amount = ?, status = ? " +
                     "WHERE policy_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, policy.getInsuranceCompany());
            stmt.setString(2, policy.getPolicyNumber());
            stmt.setDate(3, java.sql.Date.valueOf(policy.getStartDate()));
            stmt.setDate(4, java.sql.Date.valueOf(policy.getEndDate()));
            stmt.setString(5, policy.getCoverageDetails());
            stmt.setBigDecimal(6, policy.getPremiumAmount());
            stmt.setString(7, policy.getStatus());
            stmt.setInt(8, policy.getPolicyId());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Updated insurance policy: " + policy.getPolicyId());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update insurance policy: " + policy.getPolicyId(), e);
            return false;
        }
    }
    
    /**
     * Deletes an insurance policy from the database.
     * 
     * @param policyId the ID of the policy to delete
     * @return true if policy was deleted successfully, false otherwise
     */
    public boolean deletePolicy(int policyId) {
        String sql = "DELETE FROM insurance_policy WHERE policy_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, policyId);
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Deleted insurance policy: " + policyId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete insurance policy: " + policyId, e);
            return false;
        }
    }
    
    /**
     * Gets an insurance policy by its ID.
     * 
     * @param policyId the policy ID
     * @return InsurancePolicy object if found, null otherwise
     */
    public InsurancePolicy getPolicyById(int policyId) {
        String sql = "SELECT ip.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM insurance_policy ip " +
                     "JOIN vehicle v ON ip.vehicle_id = v.vehicle_id " +
                     "WHERE ip.policy_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, policyId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildPolicyFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get policy by ID: " + policyId, e);
        }
        
        return null;
    }
    
    /**
     * Gets all insurance policies for a specific vehicle.
     * 
     * @param vehicleId the vehicle ID
     * @return List of InsurancePolicy objects
     */
    public List<InsurancePolicy> getPoliciesByVehicle(int vehicleId) {
        String sql = "SELECT ip.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM insurance_policy ip " +
                     "JOIN vehicle v ON ip.vehicle_id = v.vehicle_id " +
                     "WHERE ip.vehicle_id = ? " +
                     "ORDER BY ip.end_date DESC";
        
        List<InsurancePolicy> policies = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehicleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    policies.add(buildPolicyFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get policies for vehicle: " + vehicleId, e);
        }
        
        return policies;
    }

    public List<InsurancePolicy> getPoliciesByVehicleId(int vehicleId) {
        return getPoliciesByVehicle(vehicleId);
    }

    public List<InsurancePolicy> getAllPolicies() {
        String sql = "SELECT ip.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM insurance_policy ip " +
                     "JOIN vehicle v ON ip.vehicle_id = v.vehicle_id " +
                     "ORDER BY ip.end_date DESC";

        List<InsurancePolicy> policies = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                policies.add(buildPolicyFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get all policies", e);
        }
        return policies;
    }

    public List<InsurancePolicy> searchPolicies(String searchTerm) {
        String sql = "SELECT ip.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM insurance_policy ip " +
                     "JOIN vehicle v ON ip.vehicle_id = v.vehicle_id " +
                     "WHERE ip.policy_number ILIKE ? OR v.registration_number ILIKE ? " +
                     "ORDER BY ip.end_date DESC";

        List<InsurancePolicy> policies = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + searchTerm + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    policies.add(buildPolicyFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to search policies", e);
        }
        return policies;
    }
    
    /**
     * Gets all active insurance policies that are about to expire.
     * 
     * @param daysBefore the number of days before expiry to check
     * @return List of InsurancePolicy objects expiring within the specified days
     */
    public List<InsurancePolicy> getExpiringPolicies(int daysBefore) {
        String sql = "SELECT ip.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM insurance_policy ip " +
                     "JOIN vehicle v ON ip.vehicle_id = v.vehicle_id " +
                     "WHERE ip.status = 'ACTIVE' " +
                     "AND ip.end_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL ? DAY " +
                     "ORDER BY ip.end_date ASC";
        
        List<InsurancePolicy> policies = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, daysBefore);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    policies.add(buildPolicyFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get expiring policies", e);
        }
        
        return policies;
    }
    
    /**
     * Gets the expiry status of a policy.
     * 
     * @param policy the InsurancePolicy object
     * @return String status (EXPIRED, EXPIRING_SOON, ACTIVE)
     */
    public String getExpiryStatus(InsurancePolicy policy) {
        if (policy == null || policy.getEndDate() == null) {
            return "UNKNOWN";
        }
        
        LocalDate today = LocalDate.now();
        
        if (today.isAfter(policy.getEndDate())) {
            return "EXPIRED";
        }
        
        long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(today, policy.getEndDate());
        
        if (daysUntilExpiry <= 30) {
            return "EXPIRING_SOON";
        }
        
        return "ACTIVE";
    }
    
    // ==================== Claim Methods ====================
    
    /**
     * Adds a new claim to the database.
     * 
     * @param claim the Claim object to add
     * @return true if claim was added successfully, false otherwise
     */
    public boolean addClaim(Claim claim) {
        String sql = "INSERT INTO claim (policy_id, claim_date, claim_amount, status, description) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, claim.getPolicyId());
            stmt.setDate(2, java.sql.Date.valueOf(claim.getClaimDate()));
            stmt.setBigDecimal(3, claim.getClaimAmount());
            stmt.setString(4, claim.getStatus());
            stmt.setString(5, claim.getDescription());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Added claim: " + claim.getClaimId());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add claim", e);
            return false;
        }
    }
    
    /**
     * Updates an existing claim in the database.
     * 
     * @param claim the Claim object with updated data
     * @return true if claim was updated successfully, false otherwise
     */
    public boolean updateClaim(Claim claim) {
        String sql = "UPDATE claim SET claim_date = ?, claim_amount = ?, status = ?, description = ? " +
                     "WHERE claim_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(claim.getClaimDate()));
            stmt.setBigDecimal(2, claim.getClaimAmount());
            stmt.setString(3, claim.getStatus());
            stmt.setString(4, claim.getDescription());
            stmt.setInt(5, claim.getClaimId());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Updated claim: " + claim.getClaimId());
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update claim: " + claim.getClaimId(), e);
            return false;
        }
    }
    
    /**
     * Gets all claims for a specific policy.
     * 
     * @param policyId the policy ID
     * @return List of Claim objects
     */
    public List<Claim> getClaimsByPolicy(int policyId) {
        String sql = "SELECT c.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM claim c " +
                     "JOIN insurance_policy ip ON c.policy_id = ip.policy_id " +
                     "JOIN vehicle v ON ip.vehicle_id = v.vehicle_id " +
                     "WHERE c.policy_id = ? " +
                     "ORDER BY c.claim_date DESC";
        
        List<Claim> claims = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, policyId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    claims.add(buildClaimFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get claims for policy: " + policyId, e);
        }
        
        return claims;
    }
    
    /**
     * Gets all claims with a specific status.
     * 
     * @param status the claim status (PENDING, APPROVED, REJECTED)
     * @return List of Claim objects
     */
    public List<Claim> getClaimsByStatus(String status) {
        String sql = "SELECT c.*, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM claim c " +
                     "JOIN insurance_policy ip ON c.policy_id = ip.policy_id " +
                     "JOIN vehicle v ON ip.vehicle_id = v.vehicle_id " +
                     "WHERE c.status = ? " +
                     "ORDER BY c.claim_date DESC";
        
        List<Claim> claims = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    claims.add(buildClaimFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get claims by status: " + status, e);
        }
        
        return claims;
    }

    public List<Claim> getAllClaims() {
        String sql = "SELECT c.*, ip.policy_number, v.registration_number AS vehicle_registration, " +
                     "v.make AS vehicle_make, v.model AS vehicle_model " +
                     "FROM claim c " +
                     "JOIN insurance_policy ip ON c.policy_id = ip.policy_id " +
                     "JOIN vehicle v ON ip.vehicle_id = v.vehicle_id " +
                     "ORDER BY c.claim_date DESC";

        List<Claim> claims = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                claims.add(buildClaimFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to get all claims", e);
        }
        return claims;
    }
    
    // ==================== Helper Methods ====================
    
    /**
     * Builds an InsurancePolicy object from a ResultSet.
     * 
     * @param rs the ResultSet
     * @return InsurancePolicy object
     * @throws SQLException if there's an error reading the data
     */
    private InsurancePolicy buildPolicyFromResultSet(ResultSet rs) throws SQLException {
        int policyId = rs.getInt("policy_id");
        int vehicleId = rs.getInt("vehicle_id");
        String insuranceCompany = rs.getString("insurance_company");
        String policyNumber = rs.getString("policy_number");
        java.sql.Date startDateSql = rs.getDate("start_date");
        java.sql.Date endDateSql = rs.getDate("end_date");
        String coverageDetails = rs.getString("coverage_details");
        BigDecimal premiumAmount = rs.getBigDecimal("premium_amount");
        String status = rs.getString("status");
        String vehicleRegistration = rs.getString("vehicle_registration");
        String vehicleMake = rs.getString("vehicle_make");
        String vehicleModel = rs.getString("vehicle_model");
        
        LocalDate startDate = startDateSql != null ? startDateSql.toLocalDate() : null;
        LocalDate endDate = endDateSql != null ? endDateSql.toLocalDate() : null;
        
        return new InsurancePolicy(policyId, vehicleId, insuranceCompany, policyNumber,
                                  startDate, endDate, coverageDetails, premiumAmount, status,
                                  vehicleRegistration, vehicleMake, vehicleModel);
    }
    
    /**
     * Builds a Claim object from a ResultSet.
     * 
     * @param rs the ResultSet
     * @return Claim object
     * @throws SQLException if there's an error reading the data
     */
    private Claim buildClaimFromResultSet(ResultSet rs) throws SQLException {
        int claimId = rs.getInt("claim_id");
        int policyId = rs.getInt("policy_id");
        java.sql.Date claimDateSql = rs.getDate("claim_date");
        BigDecimal claimAmount = rs.getBigDecimal("claim_amount");
        String status = rs.getString("status");
        String description = rs.getString("description");
        String policyNumber = rs.getString("policy_number");
        String vehicleRegistration = rs.getString("vehicle_registration");
        String vehicleMake = rs.getString("vehicle_make");
        String vehicleModel = rs.getString("vehicle_model");
        
        LocalDate claimDate = claimDateSql != null ? claimDateSql.toLocalDate() : null;
        
        return new Claim(claimId, policyId, claimDate, claimAmount, status, description,
                        policyNumber, vehicleRegistration, vehicleMake, vehicleModel);
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
            LOGGER.log(Level.WARNING, "Failed to resolve vehicle id for policy", e);
        }
        return 0;
    }
}
