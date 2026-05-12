package com.plateiq.controller;

import com.plateiq.model.Claim;
import com.plateiq.model.InsurancePolicy;
import com.plateiq.service.InsuranceService;
import com.plateiq.utils.AccessControl;
import com.plateiq.utils.AlertUtils;
import com.plateiq.utils.SceneNavigator;
import com.plateiq.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class InsuranceController implements Initializable {

    @FXML
    private TableView<InsurancePolicy> policyTable;

    @FXML
    private TableColumn<InsurancePolicy, Integer> colPolicyId;

    @FXML
    private TableColumn<InsurancePolicy, String> colVehiclePlate;

    @FXML
    private TableColumn<InsurancePolicy, String> colCompany;

    @FXML
    private TableColumn<InsurancePolicy, String> colPolicyNumber;

    @FXML
    private TableColumn<InsurancePolicy, LocalDate> colStartDate;

    @FXML
    private TableColumn<InsurancePolicy, LocalDate> colEndDate;

    @FXML
    private TableView<Claim> claimTable;

    @FXML
    private TableColumn<Claim, Integer> colClaimId;

    @FXML
    private TableColumn<Claim, String> colPolicyNum;

    @FXML
    private TableColumn<Claim, LocalDate> colClaimDate;

    @FXML
    private TableColumn<Claim, BigDecimal> colClaimAmount;

    @FXML
    private TableColumn<Claim, String> colStatus;

    @FXML
    private ProgressBar expiryProgressBar;

    @FXML
    private Label expiryLabel;

    @FXML
    private TextField policyNumberField;

    @FXML
    private TextField insuranceCompanyField;

    @FXML
    private TextField vehiclePlateField;

    @FXML
    private TextField claimAmountField;

    @FXML
    private TextArea claimDescriptionArea;

    @FXML
    private Button addPolicyButton;

    @FXML
    private Button updateClaimStatusButton;

    @FXML
    private Button submitClaimButton;

    private InsuranceService insuranceService;
    private ObservableList<InsurancePolicy> policyList;
    private ObservableList<Claim> claimList;
    private boolean canManageInsurance;

    public InsuranceController() {
        this.insuranceService = new InsuranceService();
        this.policyList = FXCollections.observableArrayList();
        this.claimList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!AccessControl.enforceOrRedirect(policyTable, AccessControl.Module.INSURANCE)) {
            return;
        }
        colPolicyId.setCellValueFactory(new PropertyValueFactory<>("policyId"));
        colVehiclePlate.setCellValueFactory(new PropertyValueFactory<>("vehiclePlate"));
        colCompany.setCellValueFactory(new PropertyValueFactory<>("insuranceCompany"));
        colPolicyNumber.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        colClaimId.setCellValueFactory(new PropertyValueFactory<>("claimId"));
        colPolicyNum.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));
        colClaimDate.setCellValueFactory(new PropertyValueFactory<>("claimDate"));
        colClaimAmount.setCellValueFactory(new PropertyValueFactory<>("claimAmount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        canManageInsurance = AccessControl.canManageInsurance(SessionManager.getCurrentUser());
        applyFeaturePermissions();
        policyTable.setItems(policyList);
        claimTable.setItems(claimList);
        loadPolicies();
        setupExpiryProgress();
    }

    private void applyFeaturePermissions() {
        setDisabled(policyNumberField, !canManageInsurance);
        setDisabled(insuranceCompanyField, !canManageInsurance);
        setDisabled(vehiclePlateField, !canManageInsurance);
        setDisabled(claimAmountField, !canManageInsurance);
        setDisabled(claimDescriptionArea, !canManageInsurance);
        setDisabled(addPolicyButton, !canManageInsurance);
        setDisabled(updateClaimStatusButton, !canManageInsurance);
        setDisabled(submitClaimButton, !canManageInsurance);
    }

    private void setDisabled(Control control, boolean disabled) {
        if (control != null) {
            control.setDisable(disabled);
        }
    }

    private boolean requireManagePermission() {
        if (canManageInsurance) {
            return true;
        }
        AlertUtils.showWarning("Access Denied", "You have read-only access in Insurance.");
        return false;
    }

    private void setupExpiryProgress() {
        expiryProgressBar.setStyle(
            "-fx-accent: #4CAF50;" +
            "-fx-background-color: #E0E0E0;"
        );
    }

    private void loadPolicies() {
        try {
            List<InsurancePolicy> policies = insuranceService.getAllPolicies();
            policyList.clear();
            policyList.addAll(policies);
            updateExpiryProgress(policies);
            loadClaims();
        } catch (Exception e) {
            AlertUtils.showError("Error loading policies: " + e.getMessage());
        }
    }

    private void updateExpiryProgress(List<InsurancePolicy> policies) {
        if (policies.isEmpty()) {
            expiryProgressBar.setProgress(0);
            expiryLabel.setText("No active policies");
            return;
        }

        int expiringSoon = 0;
        int expired = 0;
        int valid = 0;

        LocalDate today = LocalDate.now();

        for (InsurancePolicy policy : policies) {
            if (policy.getEndDate() != null) {
                if (policy.getEndDate().isBefore(today)) {
                    expired++;
                } else if (policy.getEndDate().isBefore(today.plusDays(30))) {
                    expiringSoon++;
                } else {
                    valid++;
                }
            }
        }

        double progress = (double) valid / policies.size();
        expiryProgressBar.setProgress(progress);

        String color = progress > 0.7 ? "#4CAF50" : (progress > 0.4 ? "#FFC107" : "#F44336");
        expiryProgressBar.setStyle("-fx-accent: " + color + "; -fx-background-color: #E0E0E0;");

        expiryLabel.setText(String.format("Valid: %d | Expiring Soon: %d | Expired: %d",
            valid, expiringSoon, expired));
    }

    private void loadClaims() {
        try {
            List<Claim> claims = insuranceService.getAllClaims();
            claimList.clear();
            claimList.addAll(claims);
        } catch (Exception e) {
            AlertUtils.showError("Error loading claims: " + e.getMessage());
        }
    }

    @FXML
    private void addPolicy() {
        if (!requireManagePermission()) return;
        String policyNumber = policyNumberField.getText();
        String company = insuranceCompanyField.getText();
        String plateNumber = vehiclePlateField.getText();

        if (policyNumber.isBlank() || company.isBlank() || plateNumber.isBlank()) {
            AlertUtils.showWarning("Validation Error", "All fields are required.");
            return;
        }

        try {
            InsurancePolicy policy = new InsurancePolicy(
                0,
                plateNumber,
                company,
                policyNumber,
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "Standard coverage"
            );

            insuranceService.addPolicy(policy);
            AlertUtils.showInfo("Success", "Policy added successfully.");
            clearPolicyFields();
            loadPolicies();
        } catch (Exception e) {
            AlertUtils.showError("Error adding policy: " + e.getMessage());
        }
    }

    @FXML
    private void addClaim() {
        if (!requireManagePermission()) return;
        String policyNumber = policyNumberField.getText();
        String amountText = claimAmountField.getText();

        if (policyNumber.isBlank() || amountText.isBlank()) {
            AlertUtils.showWarning("Validation Error", "Policy number and claim amount are required.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            Claim claim = new Claim(
                0,
                0,
                policyNumber,
                LocalDate.now(),
                amount,
                "Pending",
                claimDescriptionArea.getText()
            );

            insuranceService.addClaim(claim);
            AlertUtils.showInfo("Success", "Claim submitted successfully.");
            clearClaimFields();
            loadClaims();
        } catch (NumberFormatException e) {
            AlertUtils.showError("Invalid Input", "Claim amount must be a valid number.");
        } catch (Exception e) {
            AlertUtils.showError("Error adding claim: " + e.getMessage());
        }
    }

    @FXML
    private void updateClaimStatus() {
        if (!requireManagePermission()) return;
        Claim selectedClaim = claimTable.getSelectionModel().getSelectedItem();
        if (selectedClaim == null) {
            AlertUtils.showWarning("No Selection", "Please select a claim to update.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Update Claim Status");
        alert.setHeaderText("Update Status for Claim #" + selectedClaim.getClaimId());
        alert.setContentText("Select new status:");

        ButtonType buttonPending = new ButtonType("Pending");
        ButtonType buttonApproved = new ButtonType("Approved");
        ButtonType buttonRejected = new ButtonType("Rejected");
        ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonPending, buttonApproved, buttonRejected, buttonCancel);

        alert.showAndWait().ifPresent(result -> {
            if (result == buttonPending) selectedClaim.setStatus("Pending");
            else if (result == buttonApproved) selectedClaim.setStatus("Approved");
            else if (result == buttonRejected) selectedClaim.setStatus("Rejected");

            try {
                insuranceService.updateClaim(selectedClaim);
                AlertUtils.showInfo("Success", "Claim status updated.");
                loadClaims();
            } catch (Exception e) {
                AlertUtils.showError("Error updating claim: " + e.getMessage());
            }
        });
    }

    @FXML
    private void clearPolicyFields() {
        if (!requireManagePermission()) return;
        policyNumberField.clear();
        insuranceCompanyField.clear();
        vehiclePlateField.clear();
    }

    @FXML
    private void clearClaimFields() {
        if (!requireManagePermission()) return;
        claimAmountField.clear();
        claimDescriptionArea.clear();
    }

    @FXML
    private void searchPolicy() {
        String searchTerm = policyNumberField.getText();
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            loadPolicies();
            return;
        }
        try {
            List<InsurancePolicy> policies = insuranceService.searchPolicies(searchTerm);
            policyList.clear();
            policyList.addAll(policies);
            updateExpiryProgress(policies);
        } catch (Exception e) {
            AlertUtils.showError("Error searching policies: " + e.getMessage());
        }
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
        SceneNavigator.switchScene(event, "/fxml/dashboard.fxml");
    }
}


