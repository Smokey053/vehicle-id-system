package com.plateiq.controller;

import com.plateiq.model.Claim;
import com.plateiq.model.InsurancePolicy;
import com.plateiq.model.Vehicle;
import com.plateiq.service.InsuranceService;
import com.plateiq.service.ServiceRecordService;
import com.plateiq.service.VehicleService;
import com.plateiq.utils.AccessControl;
import com.plateiq.utils.AccessibilityHelper;
import com.plateiq.utils.AlertUtils;
import com.plateiq.utils.SceneNavigator;
import com.plateiq.utils.SessionManager;
import com.plateiq.utils.StateManager;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class InsuranceController implements Initializable {

    @FXML
    private TableView<Vehicle> vehicleListTable;

    @FXML
    private TableColumn<Vehicle, String> colVehicleListPlate;

    @FXML
    private TableColumn<Vehicle, String> colVehicleListBrand;

    @FXML
    private TableColumn<Vehicle, String> colVehicleListModel;

    @FXML
    private TableColumn<Vehicle, String> colVehicleListOwner;

    @FXML
    private TableColumn<Vehicle, String> colVehicleListPhone;

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
    private ChoiceBox<String> vehicleQuickFilterChoiceBox;

    @FXML
    private Label selectedVehicleSummaryLabel;

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
    private TextArea coverageArea;

    @FXML
    private Button addPolicyButton;

    @FXML
    private Button updateClaimStatusButton;

    @FXML
    private Button submitClaimButton;

    private final InsuranceService insuranceService = new InsuranceService();
    private final ServiceRecordService serviceRecordService =
        new ServiceRecordService();
    private final VehicleService vehicleService = new VehicleService();
    private final ObservableList<Vehicle> vehicleDirectoryList =
        FXCollections.observableArrayList();
    private final List<Vehicle> allVehicleDirectory = new ArrayList<>();
    private final ObservableList<InsurancePolicy> policyList =
        FXCollections.observableArrayList();
    private final ObservableList<Claim> claimList =
        FXCollections.observableArrayList();
    private boolean canManageInsurance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (
            !AccessControl.enforceOrRedirect(
                policyTable,
                AccessControl.Module.INSURANCE
            )
        ) {
            return;
        }

        colVehicleListPlate.setCellValueFactory(
            new PropertyValueFactory<>("plateNumber")
        );
        colVehicleListBrand.setCellValueFactory(
            new PropertyValueFactory<>("brand")
        );
        colVehicleListModel.setCellValueFactory(
            new PropertyValueFactory<>("model")
        );
        colVehicleListOwner.setCellValueFactory(
            new PropertyValueFactory<>("ownerName")
        );
        colVehicleListPhone.setCellValueFactory(
            new PropertyValueFactory<>("ownerPhone")
        );
        applyDefaultVehicleSort();

        colPolicyId.setCellValueFactory(new PropertyValueFactory<>("policyId"));
        colVehiclePlate.setCellValueFactory(
            new PropertyValueFactory<>("vehiclePlate")
        );
        colCompany.setCellValueFactory(
            new PropertyValueFactory<>("insuranceCompany")
        );
        colPolicyNumber.setCellValueFactory(
            new PropertyValueFactory<>("policyNumber")
        );
        colStartDate.setCellValueFactory(
            new PropertyValueFactory<>("startDate")
        );
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        colClaimId.setCellValueFactory(new PropertyValueFactory<>("claimId"));
        colPolicyNum.setCellValueFactory(
            new PropertyValueFactory<>("policyNumber")
        );
        colClaimDate.setCellValueFactory(
            new PropertyValueFactory<>("claimDate")
        );
        colClaimAmount.setCellValueFactory(
            new PropertyValueFactory<>("claimAmount")
        );
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        canManageInsurance = AccessControl.canManageInsurance(
            SessionManager.getCurrentUser()
        );
        applyFeaturePermissions();
        setupQuickFilterOptions();
        updateSelectedVehicleRibbon(null);

        vehicleListTable.setItems(vehicleDirectoryList);
        policyTable.setItems(policyList);
        claimTable.setItems(claimList);
        StateManager.showEmptyState(
            vehicleListTable,
            "No vehicles available.\n\nVehicle directory entries will appear here."
        );
        StateManager.showEmptyState(
            policyTable,
            "No insurance policies found.\n\nAdd a policy to get started."
        );
        StateManager.showEmptyState(
            claimTable,
            "No claims available.\n\nSubmit a claim for an existing policy."
        );

        // Setup accessibility
        setupAccessibility();

        setupExpiryProgress();
        loadVehicleDirectory();
        loadPolicies();
    }

    private void applyFeaturePermissions() {
        setDisabled(policyNumberField, !canManageInsurance);
        setDisabled(insuranceCompanyField, !canManageInsurance);
        setDisabled(vehiclePlateField, !canManageInsurance);
        setDisabled(claimAmountField, !canManageInsurance);
        setDisabled(claimDescriptionArea, !canManageInsurance);
        setDisabled(coverageArea, !canManageInsurance);
        setDisabled(addPolicyButton, !canManageInsurance);
        setDisabled(updateClaimStatusButton, !canManageInsurance);
        setDisabled(submitClaimButton, !canManageInsurance);
    }

    private void setDisabled(Control control, boolean disabled) {
        if (control != null) {
            control.setDisable(disabled);
        }
    }

    /**
     * Setup keyboard navigation and accessibility features
     */
    private void setupAccessibility() {
        // Setup form navigation with Tab key
        AccessibilityHelper.setupFormNavigation(
            vehicleQuickFilterChoiceBox,
            policyNumberField,
            insuranceCompanyField,
            vehiclePlateField,
            coverageArea,
            claimAmountField,
            claimDescriptionArea,
            addPolicyButton,
            submitClaimButton,
            updateClaimStatusButton
        );

        // Setup table keyboard shortcuts (Enter/Space to select)
        AccessibilityHelper.setupTableKeyboard(
            policyTable,
            this::handlePolicyTableActivation
        );
        AccessibilityHelper.setupTableKeyboard(
            claimTable,
            this::handleClaimTableActivation
        );
        AccessibilityHelper.setupTableKeyboard(
            vehicleListTable,
            this::handleVehicleListActivation
        );

        // Setup button keyboard activation
        AccessibilityHelper.setupButtonKeyboard(addPolicyButton);
        AccessibilityHelper.setupButtonKeyboard(submitClaimButton);
        AccessibilityHelper.setupButtonKeyboard(updateClaimStatusButton);

        // Add focus indicators
        AccessibilityHelper.addFocusIndicator(vehicleQuickFilterChoiceBox);
        AccessibilityHelper.addFocusIndicator(policyNumberField);
        AccessibilityHelper.addFocusIndicator(vehicleListTable);
        AccessibilityHelper.addFocusIndicator(policyTable);
        AccessibilityHelper.addFocusIndicator(claimTable);

        // Set initial focus
        Platform.runLater(() ->
            AccessibilityHelper.setInitialFocus(policyNumberField)
        );
    }

    /**
     * Handle policy table row activation (Enter/Space key)
     */
    private void handlePolicyTableActivation() {
        InsurancePolicy selected = policyTable
            .getSelectionModel()
            .getSelectedItem();
        if (selected != null) {
            AccessibilityHelper.announceToScreenReader(
                "Selected policy: " +
                    selected.getPolicyNumber() +
                    " for vehicle " +
                    selected.getVehiclePlate() +
                    ", Company: " +
                    selected.getInsuranceCompany() +
                    ", Valid until: " +
                    selected.getEndDate()
            );
        }
    }

    /**
     * Handle claim table row activation (Enter/Space key)
     */
    private void handleClaimTableActivation() {
        Claim selected = claimTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            AccessibilityHelper.announceToScreenReader(
                "Selected claim: ID " +
                    selected.getClaimId() +
                    ", Policy: " +
                    selected.getPolicyNumber() +
                    ", Amount: $" +
                    selected.getClaimAmount() +
                    ", Status: " +
                    selected.getStatus()
            );
        }
    }

    private void handleVehicleListActivation() {
        Vehicle selected = vehicleListTable
            .getSelectionModel()
            .getSelectedItem();
        if (selected != null) {
            handleVehicleSelection();
        }
    }

    private void loadVehicleDirectory() {
        StateManager.showLoadingState(vehicleListTable, true);
        try {
            List<Vehicle> vehicles = vehicleService.getAllVehicles();
            allVehicleDirectory.clear();
            allVehicleDirectory.addAll(vehicles);
            applyQuickFilterToDirectory(
                vehicleQuickFilterChoiceBox == null
                    ? "All Vehicles"
                    : vehicleQuickFilterChoiceBox.getValue()
            );

            if (vehicles.isEmpty()) {
                StateManager.showEmptyState(
                    vehicleListTable,
                    "No vehicles found.\n\nVehicle records will appear here once registered."
                );
            }

            AccessibilityHelper.announceToScreenReader(
                "Loaded " + vehicles.size() + " vehicles in insurance directory"
            );
        } catch (Exception e) {
            StateManager.showErrorState(
                vehicleListTable,
                "Failed to load vehicle directory: " + e.getMessage(),
                this::loadVehicleDirectory
            );
            AlertUtils.showError("Error loading vehicles", e.getMessage());
        } finally {
            StateManager.showLoadingState(vehicleListTable, false);
        }
    }

    @FXML
    private void handleVehicleSelection() {
        Vehicle selected = vehicleListTable
            .getSelectionModel()
            .getSelectedItem();
        if (selected == null) {
            return;
        }

        vehiclePlateField.setText(selected.getPlateNumber());
        policyNumberField.setText(selected.getPlateNumber());
        updateSelectedVehicleRibbon(selected);
        searchPolicy();

        AccessibilityHelper.announceToScreenReader(
            "Selected vehicle " + selected.getPlateNumber() + " from directory"
        );
    }

    private void setupQuickFilterOptions() {
        if (vehicleQuickFilterChoiceBox == null) {
            return;
        }
        vehicleQuickFilterChoiceBox
            .getItems()
            .setAll(
                "All Vehicles",
                "My Vehicles",
                "Recently Serviced",
                "Has Active Policy"
            );
        vehicleQuickFilterChoiceBox.setValue("All Vehicles");
    }

    @FXML
    private void applyVehicleQuickFilter() {
        applyQuickFilterToDirectory(vehicleQuickFilterChoiceBox.getValue());
    }

    @FXML
    private void resetVehicleQuickFilter() {
        if (vehicleQuickFilterChoiceBox != null) {
            vehicleQuickFilterChoiceBox.setValue("All Vehicles");
        }
        applyQuickFilterToDirectory("All Vehicles");
    }

    private void applyQuickFilterToDirectory(String filterName) {
        String effectiveFilter = (filterName == null || filterName.isBlank())
            ? "All Vehicles"
            : filterName;

        List<Vehicle> filtered = switch (effectiveFilter) {
            case "My Vehicles" -> filterMyVehicles(allVehicleDirectory);
            case "Recently Serviced" -> filterRecentlyServicedVehicles(
                allVehicleDirectory
            );
            case "Has Active Policy" -> filterActivePolicyVehicles(
                allVehicleDirectory
            );
            default -> new ArrayList<>(allVehicleDirectory);
        };

        vehicleDirectoryList.setAll(filtered);
        applyDefaultVehicleSort();

        if (filtered.isEmpty()) {
            StateManager.showEmptyState(
                vehicleListTable,
                "No vehicles match the selected quick filter.\n\nTry a different filter or reset to All Vehicles."
            );
        }
    }

    private List<Vehicle> filterMyVehicles(List<Vehicle> source) {
        String username = SessionManager.getCurrentUsername();
        if (username == null || username.isBlank()) {
            return new ArrayList<>(source);
        }

        List<Vehicle> matched = source
            .stream()
            .filter(
                v ->
                    v.getOwnerName() != null &&
                    v
                        .getOwnerName()
                        .toLowerCase()
                        .contains(username.toLowerCase())
            )
            .toList();

        return matched.isEmpty() ? new ArrayList<>(source) : matched;
    }

    private List<Vehicle> filterRecentlyServicedVehicles(List<Vehicle> source) {
        try {
            LocalDate cutoff = LocalDate.now().minusDays(90);
            Set<Integer> servicedVehicleIds = new HashSet<>();
            for (var record : serviceRecordService.getAllServiceRecords()) {
                if (
                    record.getServiceDate() != null &&
                    !record.getServiceDate().isBefore(cutoff)
                ) {
                    servicedVehicleIds.add(record.getVehicleId());
                }
            }

            return source
                .stream()
                .filter(v -> servicedVehicleIds.contains(v.getVehicleId()))
                .toList();
        } catch (Exception e) {
            AlertUtils.showError(
                "Filter Error",
                "Failed to apply Recently Serviced filter: " + e.getMessage()
            );
            return new ArrayList<>(source);
        }
    }

    private List<Vehicle> filterActivePolicyVehicles(List<Vehicle> source) {
        try {
            LocalDate today = LocalDate.now();
            Set<Integer> activePolicyVehicleIds = new HashSet<>();
            for (InsurancePolicy policy : insuranceService.getAllPolicies()) {
                if (
                    policy.getEndDate() != null &&
                    !policy.getEndDate().isBefore(today)
                ) {
                    activePolicyVehicleIds.add(policy.getVehicleId());
                }
            }

            return source
                .stream()
                .filter(v -> activePolicyVehicleIds.contains(v.getVehicleId()))
                .toList();
        } catch (Exception e) {
            AlertUtils.showError(
                "Filter Error",
                "Failed to apply Active Policy filter: " + e.getMessage()
            );
            return new ArrayList<>(source);
        }
    }

    private void applyDefaultVehicleSort() {
        if (vehicleListTable == null || colVehicleListPlate == null) {
            return;
        }
        colVehicleListPlate.setSortType(TableColumn.SortType.ASCENDING);
        vehicleListTable.getSortOrder().setAll(colVehicleListPlate);
        vehicleListTable.sort();
    }

    private void updateSelectedVehicleRibbon(Vehicle vehicle) {
        if (selectedVehicleSummaryLabel == null) {
            return;
        }

        if (vehicle == null) {
            selectedVehicleSummaryLabel.setText("None");
            return;
        }

        selectedVehicleSummaryLabel.setText(
            vehicle.getPlateNumber() +
                " • " +
                vehicle.getBrand() +
                " " +
                vehicle.getModel() +
                " • Owner: " +
                (vehicle.getOwnerName() == null ? "-" : vehicle.getOwnerName())
        );
    }

    private boolean requireManagePermission() {
        if (canManageInsurance) {
            return true;
        }
        AlertUtils.showWarning(
            "Access Denied",
            "You have read-only access in Insurance."
        );
        return false;
    }

    private void setupExpiryProgress() {
        expiryProgressBar.setStyle("-fx-accent: #22c55e;");
    }

    private void loadPolicies() {
        StateManager.showLoadingState(policyTable, true);
        try {
            List<InsurancePolicy> policies = insuranceService.getAllPolicies();
            policyList.setAll(policies);
            updateExpiryProgress(policies);

            if (policies.isEmpty()) {
                StateManager.showEmptyState(
                    policyTable,
                    "No insurance policies found.\n\nAdd a policy to get started."
                );
            }

            AccessibilityHelper.announceToScreenReader(
                "Loaded " + policies.size() + " insurance policies"
            );
            loadClaims();
        } catch (Exception e) {
            StateManager.showErrorState(
                policyTable,
                "Failed to load policies: " + e.getMessage(),
                this::loadPolicies
            );
            AlertUtils.showError("Error loading policies", e.getMessage());
        } finally {
            StateManager.showLoadingState(policyTable, false);
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

        String color =
            progress > 0.7
                ? "#22c55e"
                : (progress > 0.4 ? "#f59e0b" : "#ef4444");
        expiryProgressBar.setStyle("-fx-accent: " + color + ";");

        expiryLabel.setText(
            String.format(
                "Valid: %d | Expiring Soon: %d | Expired: %d",
                valid,
                expiringSoon,
                expired
            )
        );
    }

    private void loadClaims() {
        StateManager.showLoadingState(claimTable, true);
        try {
            List<Claim> claims = insuranceService.getAllClaims();
            claimList.setAll(claims);

            if (claims.isEmpty()) {
                StateManager.showEmptyState(
                    claimTable,
                    "No claims available.\n\nSubmit a claim for an existing policy."
                );
            }

            AccessibilityHelper.announceToScreenReader(
                "Loaded " + claims.size() + " claims"
            );
        } catch (Exception e) {
            StateManager.showErrorState(
                claimTable,
                "Failed to load claims: " + e.getMessage(),
                this::loadClaims
            );
            AlertUtils.showError("Error loading claims", e.getMessage());
        } finally {
            StateManager.showLoadingState(claimTable, false);
        }
    }

    @FXML
    private void addPolicy() {
        if (!requireManagePermission()) {
            return;
        }

        String policyNumber = safeTrim(policyNumberField.getText());
        String company = safeTrim(insuranceCompanyField.getText());
        String plateNumber = safeTrim(vehiclePlateField.getText());

        if (
            policyNumber.isBlank() || company.isBlank() || plateNumber.isBlank()
        ) {
            AlertUtils.showWarning(
                "Validation Error",
                "Policy number, company, and vehicle plate are required."
            );
            return;
        }

        try {
            InsurancePolicy policy = new InsurancePolicy(
                0,
                plateNumber.toUpperCase(),
                company,
                policyNumber,
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                safeTrim(coverageArea.getText()).isBlank()
                    ? "Standard coverage"
                    : safeTrim(coverageArea.getText())
            );

            boolean created = insuranceService.addPolicy(policy);
            if (!created) {
                AlertUtils.showError(
                    "Create Failed",
                    "Policy could not be created. Check vehicle registration and policy number."
                );
                return;
            }
            AlertUtils.showInfo("Success", "Policy added successfully.");
            AccessibilityHelper.announceToScreenReader(
                "Policy " +
                    policy.getPolicyNumber() +
                    " added successfully for vehicle " +
                    policy.getVehiclePlate()
            );
            clearPolicyFieldsInternal();
            loadPolicies();
        } catch (Exception e) {
            AlertUtils.showError("Error adding policy", e.getMessage());
        }
    }

    @FXML
    private void addClaim() {
        if (!requireManagePermission()) {
            return;
        }

        String policyNumber = safeTrim(policyNumberField.getText());
        String amountText = safeTrim(claimAmountField.getText());

        if (policyNumber.isBlank() || amountText.isBlank()) {
            AlertUtils.showWarning(
                "Validation Error",
                "Policy number and claim amount are required."
            );
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                AlertUtils.showWarning(
                    "Validation Error",
                    "Claim amount must be greater than 0."
                );
                return;
            }

            Claim claim = new Claim(
                0,
                0,
                policyNumber,
                LocalDate.now(),
                amount,
                "PENDING",
                safeTrim(claimDescriptionArea.getText())
            );

            boolean created = insuranceService.addClaim(claim);
            if (!created) {
                AlertUtils.showError(
                    "Create Failed",
                    "Claim could not be submitted. Check policy number and claim data."
                );
                return;
            }
            AlertUtils.showInfo("Success", "Claim submitted successfully.");
            AccessibilityHelper.announceToScreenReader(
                "Claim submitted successfully for policy " +
                    claim.getPolicyNumber() +
                    ", Amount: $" +
                    claim.getClaimAmount()
            );
            clearClaimFieldsInternal();
            loadClaims();
        } catch (NumberFormatException e) {
            AlertUtils.showError(
                "Invalid Input",
                "Claim amount must be a valid number."
            );
        } catch (Exception e) {
            AlertUtils.showError("Error adding claim", e.getMessage());
        }
    }

    @FXML
    private void updateClaimStatus() {
        if (!requireManagePermission()) {
            return;
        }

        Claim selectedClaim = claimTable.getSelectionModel().getSelectedItem();
        if (selectedClaim == null) {
            AlertUtils.showWarning(
                "No Selection",
                "Please select a claim to update."
            );
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Update Claim Status");
        alert.setHeaderText(
            "Update Status for Claim #" + selectedClaim.getClaimId()
        );
        alert.setContentText("Select new status:");

        ButtonType buttonPending = new ButtonType("Pending");
        ButtonType buttonApproved = new ButtonType("Approved");
        ButtonType buttonRejected = new ButtonType("Rejected");
        ButtonType buttonCancel = new ButtonType(
            "Cancel",
            ButtonBar.ButtonData.CANCEL_CLOSE
        );

        alert
            .getButtonTypes()
            .setAll(
                buttonPending,
                buttonApproved,
                buttonRejected,
                buttonCancel
            );

        alert
            .showAndWait()
            .ifPresent(result -> {
                if (result == buttonPending) {
                    selectedClaim.setStatus("PENDING");
                } else if (result == buttonApproved) {
                    selectedClaim.setStatus("APPROVED");
                } else if (result == buttonRejected) {
                    selectedClaim.setStatus("REJECTED");
                } else {
                    return;
                }

                try {
                    insuranceService.updateClaim(selectedClaim);
                    AlertUtils.showInfo("Success", "Claim status updated.");
                    AccessibilityHelper.announceToScreenReader(
                        "Claim " +
                            selectedClaim.getClaimId() +
                            " status updated to " +
                            selectedClaim.getStatus()
                    );
                    loadClaims();
                } catch (Exception e) {
                    AlertUtils.showError(
                        "Error updating claim",
                        e.getMessage()
                    );
                }
            });
    }

    @FXML
    private void clearPolicyFields() {
        if (!requireManagePermission()) {
            return;
        }
        clearPolicyFieldsInternal();
    }

    private void clearPolicyFieldsInternal() {
        policyNumberField.clear();
        insuranceCompanyField.clear();
        vehiclePlateField.clear();
        if (coverageArea != null) {
            coverageArea.clear();
        }
    }

    @FXML
    private void clearClaimFields() {
        if (!requireManagePermission()) {
            return;
        }
        clearClaimFieldsInternal();
    }

    private void clearClaimFieldsInternal() {
        claimAmountField.clear();
        claimDescriptionArea.clear();
    }

    @FXML
    private void searchPolicy() {
        String searchTerm = safeTrim(policyNumberField.getText());
        if (searchTerm.isBlank()) {
            updateSelectedVehicleRibbon(null);
            loadPolicies();
            return;
        }

        StateManager.showLoadingState(policyTable, true);
        try {
            List<InsurancePolicy> policies = insuranceService.searchPolicies(
                searchTerm
            );
            policyList.setAll(policies);
            updateExpiryProgress(policies);

            if (policies.isEmpty()) {
                StateManager.showEmptyState(
                    policyTable,
                    "No policies found matching '" +
                        searchTerm +
                        "'\n\nTry a different search term or clear the search."
                );
            }

            AccessibilityHelper.announceToScreenReader(
                "Search completed. Found " +
                    policies.size() +
                    " matching policies"
            );
        } catch (Exception e) {
            StateManager.showErrorState(
                policyTable,
                "Search failed: " + e.getMessage(),
                this::searchPolicy
            );
            AlertUtils.showError("Error searching policies", e.getMessage());
        } finally {
            StateManager.showLoadingState(policyTable, false);
        }
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
        SceneNavigator.switchScene(event, "/fxml/dashboard.fxml");
    }

    @FXML
    private void handleHome(ActionEvent event) {
        goToDashboard(event);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();
        SceneNavigator.switchScene(event, "/fxml/login.fxml");
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Platform.exit();
    }
}
