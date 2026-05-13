package com.plateiq.controller;

import com.plateiq.model.PoliceReport;
import com.plateiq.model.Violation;
import com.plateiq.service.PoliceService;
import com.plateiq.utils.AccessControl;
import com.plateiq.utils.AccessibilityHelper;
import com.plateiq.utils.AlertUtils;
import com.plateiq.utils.SceneNavigator;
import com.plateiq.utils.SessionManager;
import com.plateiq.utils.StateManager;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class PoliceController implements Initializable {

    @FXML
    private TableView<PoliceReport> reportTable;

    @FXML
    private TableColumn<PoliceReport, Integer> colReportId;

    @FXML
    private TableColumn<PoliceReport, String> colVehiclePlate;

    @FXML
    private TableColumn<PoliceReport, LocalDate> colReportDate;

    @FXML
    private TableColumn<PoliceReport, String> colReportType;

    @FXML
    private TableColumn<PoliceReport, String> colDescription;

    @FXML
    private TableView<Violation> violationTable;

    @FXML
    private TableColumn<Violation, Integer> colViolationId;

    @FXML
    private TableColumn<Violation, String> colViolationType;

    @FXML
    private TableColumn<Violation, LocalDate> colViolationDate;

    @FXML
    private TableColumn<Violation, BigDecimal> colFineAmount;

    @FXML
    private TableColumn<Violation, String> colStatus;

    @FXML
    private TextField vehicleSearchField;

    @FXML
    private TextField officerNameField;

    @FXML
    private TextField violationTypeField;

    @FXML
    private TextField fineAmountField;

    @FXML
    private TextArea reportDescriptionArea;

    @FXML
    private TextArea violationDescriptionArea;

    @FXML
    private ChoiceBox<String> reportTypeChoiceBox;

    @FXML
    private ChoiceBox<String> violationStatusChoiceBox;

    @FXML
    private Button addReportButton;

    @FXML
    private Button addViolationButton;

    @FXML
    private Button updateViolationStatusButton;

    private final PoliceService policeService = new PoliceService();
    private final ObservableList<PoliceReport> reportList =
        FXCollections.observableArrayList();
    private final ObservableList<Violation> violationList =
        FXCollections.observableArrayList();
    private boolean canManagePolice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (
            !AccessControl.enforceOrRedirect(
                reportTable,
                AccessControl.Module.POLICE
            )
        ) {
            return;
        }

        colReportId.setCellValueFactory(new PropertyValueFactory<>("reportId"));
        colVehiclePlate.setCellValueFactory(
            new PropertyValueFactory<>("vehiclePlate")
        );
        colReportDate.setCellValueFactory(
            new PropertyValueFactory<>("reportDate")
        );
        colReportType.setCellValueFactory(
            new PropertyValueFactory<>("reportType")
        );
        colDescription.setCellValueFactory(
            new PropertyValueFactory<>("description")
        );

        colViolationId.setCellValueFactory(
            new PropertyValueFactory<>("violationId")
        );
        colViolationType.setCellValueFactory(
            new PropertyValueFactory<>("violationType")
        );
        colViolationDate.setCellValueFactory(
            new PropertyValueFactory<>("violationDate")
        );
        colFineAmount.setCellValueFactory(
            new PropertyValueFactory<>("fineAmount")
        );
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        reportTable.setItems(reportList);
        violationTable.setItems(violationList);
        StateManager.showEmptyState(
            reportTable,
            "No police reports available.\n\nSearch for a vehicle to view related reports."
        );
        StateManager.showEmptyState(
            violationTable,
            "No violations available.\n\nSearch for a vehicle to view related violations."
        );

        reportTypeChoiceBox
            .getItems()
            .addAll("ACCIDENT", "THEFT", "INSPECTION", "OTHER");
        violationStatusChoiceBox
            .getItems()
            .addAll("UNPAID", "PAID", "DISPUTED", "WAIVED");

        canManagePolice = AccessControl.canManagePolice(
            SessionManager.getCurrentUser()
        );
        applyFeaturePermissions();

        // Setup accessibility
        setupAccessibility();

        loadReports();
        loadViolations();
    }

    private void applyFeaturePermissions() {
        setDisabled(officerNameField, !canManagePolice);
        setDisabled(violationTypeField, !canManagePolice);
        setDisabled(fineAmountField, !canManagePolice);
        setDisabled(reportDescriptionArea, !canManagePolice);
        setDisabled(violationDescriptionArea, !canManagePolice);
        setDisabled(reportTypeChoiceBox, !canManagePolice);
        setDisabled(violationStatusChoiceBox, !canManagePolice);
        setDisabled(addReportButton, !canManagePolice);
        setDisabled(addViolationButton, !canManagePolice);
        setDisabled(updateViolationStatusButton, !canManagePolice);
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
            vehicleSearchField,
            officerNameField,
            violationTypeField,
            fineAmountField,
            reportDescriptionArea,
            violationDescriptionArea,
            reportTypeChoiceBox,
            violationStatusChoiceBox,
            addReportButton,
            addViolationButton,
            updateViolationStatusButton
        );

        // Setup table keyboard shortcuts (Enter/Space to activate)
        AccessibilityHelper.setupTableKeyboard(
            reportTable,
            this::handleReportTableActivation
        );
        AccessibilityHelper.setupTableKeyboard(
            violationTable,
            this::handleViolationTableActivation
        );

        // Setup button keyboard activation
        AccessibilityHelper.setupButtonKeyboard(addReportButton);
        AccessibilityHelper.setupButtonKeyboard(addViolationButton);
        AccessibilityHelper.setupButtonKeyboard(updateViolationStatusButton);

        // Add focus indicators
        AccessibilityHelper.addFocusIndicator(vehicleSearchField);
        AccessibilityHelper.addFocusIndicator(reportTable);
        AccessibilityHelper.addFocusIndicator(violationTable);

        // Set initial focus
        Platform.runLater(() ->
            AccessibilityHelper.setInitialFocus(vehicleSearchField)
        );
    }

    /**
     * Handle report table row activation (Enter/Space key)
     */
    private void handleReportTableActivation() {
        PoliceReport selected = reportTable
            .getSelectionModel()
            .getSelectedItem();
        if (selected != null) {
            AccessibilityHelper.announceToScreenReader(
                "Selected police report: ID " +
                    selected.getReportId() +
                    ", Type: " +
                    selected.getReportType() +
                    ", Vehicle: " +
                    selected.getVehiclePlate() +
                    ", Date: " +
                    selected.getReportDate()
            );
        }
    }

    /**
     * Handle violation table row activation (Enter/Space key)
     */
    private void handleViolationTableActivation() {
        Violation selected = violationTable
            .getSelectionModel()
            .getSelectedItem();
        if (selected != null) {
            AccessibilityHelper.announceToScreenReader(
                "Selected violation: ID " +
                    selected.getViolationId() +
                    ", Type: " +
                    selected.getViolationType() +
                    ", Fine: $" +
                    selected.getFineAmount() +
                    ", Status: " +
                    selected.getStatus()
            );
        }
    }

    private boolean requireManagePermission() {
        if (canManagePolice) {
            return true;
        }
        AlertUtils.showWarning(
            "Access Denied",
            "You have read-only access in Police Records."
        );
        return false;
    }

    private void loadReports() {
        StateManager.showLoadingState(reportTable, true);
        try {
            List<PoliceReport> reports = policeService.getAllReports();
            reportList.setAll(reports);

            if (reports.isEmpty()) {
                StateManager.showEmptyState(
                    reportTable,
                    "No police reports found.\n\nSearch for a vehicle to view related reports."
                );
            }

            AccessibilityHelper.announceToScreenReader(
                "Loaded " + reports.size() + " police reports"
            );
        } catch (Exception e) {
            StateManager.showErrorState(
                reportTable,
                "Failed to load reports: " + e.getMessage(),
                this::loadReports
            );
            AlertUtils.showError("Error loading reports", e.getMessage());
        } finally {
            StateManager.showLoadingState(reportTable, false);
        }
    }

    private void loadViolations() {
        StateManager.showLoadingState(violationTable, true);
        try {
            List<Violation> violations = policeService.getAllViolations();
            violationList.setAll(violations);

            if (violations.isEmpty()) {
                StateManager.showEmptyState(
                    violationTable,
                    "No violations found.\n\nSearch for a vehicle to view related violations."
                );
            }

            AccessibilityHelper.announceToScreenReader(
                "Loaded " + violations.size() + " violations"
            );
        } catch (Exception e) {
            StateManager.showErrorState(
                violationTable,
                "Failed to load violations: " + e.getMessage(),
                this::loadViolations
            );
            AlertUtils.showError("Error loading violations", e.getMessage());
        } finally {
            StateManager.showLoadingState(violationTable, false);
        }
    }

    @FXML
    private void searchVehicle() {
        String searchTerm = safeTrim(vehicleSearchField.getText());
        if (searchTerm.isBlank()) {
            loadReports();
            loadViolations();
            return;
        }

        StateManager.showLoadingState(reportTable, true);
        StateManager.showLoadingState(violationTable, true);
        try {
            String normalizedSearch = searchTerm.toUpperCase();
            List<PoliceReport> reports = policeService.searchReportsByVehicle(
                normalizedSearch
            );
            List<Violation> violations =
                policeService.searchViolationsByVehicle(normalizedSearch);

            reportList.setAll(reports);
            violationList.setAll(violations);

            if (reports.isEmpty()) {
                StateManager.showEmptyState(
                    reportTable,
                    "No police reports found for '" +
                        searchTerm +
                        "'\n\nVerify the vehicle registration and try again."
                );
            }

            if (violations.isEmpty()) {
                StateManager.showEmptyState(
                    violationTable,
                    "No violations found for '" +
                        searchTerm +
                        "'\n\nThis vehicle has no recorded violations."
                );
            }

            AccessibilityHelper.announceToScreenReader(
                "Search completed. Found " +
                    reports.size() +
                    " police reports and " +
                    violations.size() +
                    " violations for " +
                    searchTerm
            );
        } catch (Exception e) {
            StateManager.showErrorState(
                reportTable,
                "Search failed: " + e.getMessage(),
                this::searchVehicle
            );
            StateManager.showErrorState(
                violationTable,
                "Search failed: " + e.getMessage(),
                this::searchVehicle
            );
            AlertUtils.showError("Error searching records", e.getMessage());
        } finally {
            StateManager.showLoadingState(reportTable, false);
            StateManager.showLoadingState(violationTable, false);
        }
    }

    @FXML
    private void addReport() {
        if (!requireManagePermission()) {
            return;
        }

        String plateNumber = safeTrim(
            vehicleSearchField.getText()
        ).toUpperCase();
        String reportType = reportTypeChoiceBox.getValue();
        String description = safeTrim(reportDescriptionArea.getText());
        String officerName = safeTrim(officerNameField.getText());

        if (
            plateNumber.isBlank() ||
            reportType == null ||
            description.isBlank() ||
            officerName.isBlank()
        ) {
            AlertUtils.showWarning(
                "Validation Error",
                "Vehicle, report type, description, and officer name are required."
            );
            return;
        }

        try {
            PoliceReport report = new PoliceReport(
                0,
                plateNumber,
                LocalDate.now(),
                reportType,
                description,
                officerName
            );

            boolean created = policeService.addReport(report);
            if (!created) {
                AlertUtils.showError(
                    "Create Failed",
                    "Report could not be created. Check vehicle registration."
                );
                return;
            }
            AlertUtils.showInfo("Success", "Report added successfully.");
            AccessibilityHelper.announceToScreenReader(
                "Police report added successfully for vehicle " + plateNumber
            );
            clearReportFieldsInternal();
            loadReports();
        } catch (Exception e) {
            AlertUtils.showError("Error adding report", e.getMessage());
        }
    }

    @FXML
    private void addViolation() {
        if (!requireManagePermission()) {
            return;
        }

        String plateNumber = safeTrim(
            vehicleSearchField.getText()
        ).toUpperCase();
        String violationType = safeTrim(violationTypeField.getText());
        String fineText = safeTrim(fineAmountField.getText());
        String description = safeTrim(violationDescriptionArea.getText());
        String status = violationStatusChoiceBox.getValue();

        if (
            plateNumber.isBlank() ||
            violationType.isBlank() ||
            fineText.isBlank() ||
            status == null
        ) {
            AlertUtils.showWarning(
                "Validation Error",
                "Vehicle, violation type, fine amount, and status are required."
            );
            return;
        }

        try {
            double fineAmount = Double.parseDouble(fineText);
            if (fineAmount < 0) {
                AlertUtils.showWarning(
                    "Validation Error",
                    "Fine amount cannot be negative."
                );
                return;
            }

            Violation violation = new Violation(
                0,
                plateNumber,
                LocalDate.now(),
                violationType,
                fineAmount,
                status,
                description
            );

            boolean created = policeService.addViolation(violation);
            if (!created) {
                AlertUtils.showError(
                    "Create Failed",
                    "Violation could not be logged. Check vehicle registration and status."
                );
                return;
            }
            AlertUtils.showInfo("Success", "Violation logged successfully.");
            AccessibilityHelper.announceToScreenReader(
                "Violation logged successfully for vehicle " +
                    plateNumber +
                    ", fine amount: $" +
                    fineAmount
            );
            clearViolationFieldsInternal();
            loadViolations();
        } catch (NumberFormatException e) {
            AlertUtils.showError(
                "Invalid Input",
                "Fine amount must be a valid number."
            );
        } catch (Exception e) {
            AlertUtils.showError("Error adding violation", e.getMessage());
        }
    }

    @FXML
    private void updateViolationStatus() {
        if (!requireManagePermission()) {
            return;
        }

        Violation selectedViolation = violationTable
            .getSelectionModel()
            .getSelectedItem();
        if (selectedViolation == null) {
            AlertUtils.showWarning(
                "No Selection",
                "Please select a violation to update."
            );
            return;
        }

        String newStatus = violationStatusChoiceBox.getValue();
        if (newStatus == null || newStatus.isBlank()) {
            String currentStatus =
                selectedViolation.getStatus() == null
                    ? ""
                    : selectedViolation.getStatus().trim().toUpperCase();
            newStatus = currentStatus.equals("PAID") ? "UNPAID" : "PAID";
        }

        selectedViolation.setStatus(newStatus);

        try {
            policeService.updateViolation(selectedViolation);
            AlertUtils.showInfo(
                "Success",
                "Violation status updated to " + newStatus + "."
            );
            AccessibilityHelper.announceToScreenReader(
                "Violation status updated to " +
                    newStatus +
                    " for violation ID " +
                    selectedViolation.getViolationId()
            );
            loadViolations();
        } catch (Exception e) {
            AlertUtils.showError("Error updating violation", e.getMessage());
        }
    }

    @FXML
    private void clearReportFields() {
        if (!requireManagePermission()) {
            return;
        }
        clearReportFieldsInternal();
    }

    private void clearReportFieldsInternal() {
        reportTypeChoiceBox.setValue(null);
        reportDescriptionArea.clear();
        officerNameField.clear();
    }

    @FXML
    private void clearViolationFields() {
        if (!requireManagePermission()) {
            return;
        }
        clearViolationFieldsInternal();
    }

    private void clearViolationFieldsInternal() {
        violationTypeField.clear();
        fineAmountField.clear();
        violationDescriptionArea.clear();
        violationStatusChoiceBox.setValue(null);
    }

    @FXML
    private void viewUnpaidViolations() {
        try {
            List<Violation> unpaid = policeService.getUnpaidViolations();
            violationList.setAll(unpaid);
            AlertUtils.showInfo(
                "Unpaid Violations",
                "Loaded " + unpaid.size() + " unpaid/disputed violations."
            );
            AccessibilityHelper.announceToScreenReader(
                "Showing " + unpaid.size() + " unpaid or disputed violations"
            );
        } catch (Exception e) {
            AlertUtils.showError(
                "Error loading unpaid violations",
                e.getMessage()
            );
        }
    }

    @FXML
    private void viewAllViolations() {
        loadViolations();
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
