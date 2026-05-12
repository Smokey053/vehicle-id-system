package com.plateiq.controller;

import com.plateiq.model.PoliceReport;
import com.plateiq.model.Violation;
import com.plateiq.service.PoliceService;
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

    private PoliceService policeService;
    private ObservableList<PoliceReport> reportList;
    private ObservableList<Violation> violationList;
    private boolean canManagePolice;

    public PoliceController() {
        this.policeService = new PoliceService();
        this.reportList = FXCollections.observableArrayList();
        this.violationList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!AccessControl.enforceOrRedirect(reportTable, AccessControl.Module.POLICE)) {
            return;
        }
        colReportId.setCellValueFactory(new PropertyValueFactory<>("reportId"));
        colVehiclePlate.setCellValueFactory(new PropertyValueFactory<>("vehiclePlate"));
        colReportDate.setCellValueFactory(new PropertyValueFactory<>("reportDate"));
        colReportType.setCellValueFactory(new PropertyValueFactory<>("reportType"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        colViolationId.setCellValueFactory(new PropertyValueFactory<>("violationId"));
        colViolationType.setCellValueFactory(new PropertyValueFactory<>("violationType"));
        colViolationDate.setCellValueFactory(new PropertyValueFactory<>("violationDate"));
        colFineAmount.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        reportTable.setItems(reportList);
        violationTable.setItems(violationList);

        reportTypeChoiceBox.getItems().addAll("Accident", "Theft");
        violationStatusChoiceBox.getItems().addAll("Paid", "Unpaid");

        canManagePolice = AccessControl.canManagePolice(SessionManager.getCurrentUser());
        applyFeaturePermissions();
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

    private boolean requireManagePermission() {
        if (canManagePolice) {
            return true;
        }
        AlertUtils.showWarning("Access Denied", "You have read-only access in Police Records.");
        return false;
    }

    private void loadReports() {
        try {
            List<PoliceReport> reports = policeService.getAllReports();
            reportList.clear();
            reportList.addAll(reports);
        } catch (Exception e) {
            AlertUtils.showError("Error loading reports: " + e.getMessage());
        }
    }

    private void loadViolations() {
        try {
            List<Violation> violations = policeService.getAllViolations();
            violationList.clear();
            violationList.addAll(violations);
        } catch (Exception e) {
            AlertUtils.showError("Error loading violations: " + e.getMessage());
        }
    }

    @FXML
    private void searchVehicle() {
        String searchTerm = vehicleSearchField.getText();
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            loadReports();
            loadViolations();
            return;
        }
        try {
            List<PoliceReport> reports = policeService.searchReportsByVehicle(searchTerm);
            reportList.clear();
            reportList.addAll(reports);

            List<Violation> violations = policeService.searchViolationsByVehicle(searchTerm);
            violationList.clear();
            violationList.addAll(violations);
        } catch (Exception e) {
            AlertUtils.showError("Error searching: " + e.getMessage());
        }
    }

    @FXML
    private void addReport() {
        if (!requireManagePermission()) return;
        String plateNumber = vehicleSearchField.getText();
        String reportType = reportTypeChoiceBox.getValue();
        String description = reportDescriptionArea.getText();
        String officerName = officerNameField.getText();

        if (plateNumber.isBlank() || reportType == null || description.isBlank() || officerName.isBlank()) {
            AlertUtils.showWarning("Validation Error", "All fields are required.");
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

            policeService.addReport(report);
            AlertUtils.showInfo("Success", "Report added successfully.");
            clearReportFields();
            loadReports();
        } catch (Exception e) {
            AlertUtils.showError("Error adding report: " + e.getMessage());
        }
    }

    @FXML
    private void addViolation() {
        if (!requireManagePermission()) return;
        String plateNumber = vehicleSearchField.getText();
        String violationType = violationTypeField.getText();
        String fineText = fineAmountField.getText();
        String description = violationDescriptionArea.getText();
        String status = violationStatusChoiceBox.getValue();

        if (plateNumber.isBlank() || violationType.isBlank() || fineText.isBlank() || status == null) {
            AlertUtils.showWarning("Validation Error", "All fields are required.");
            return;
        }

        try {
            double fineAmount = Double.parseDouble(fineText);
            Violation violation = new Violation(
                0,
                plateNumber,
                LocalDate.now(),
                violationType,
                fineAmount,
                status,
                description
            );

            policeService.addViolation(violation);
            AlertUtils.showInfo("Success", "Violation logged successfully.");
            clearViolationFields();
            loadViolations();
        } catch (NumberFormatException e) {
            AlertUtils.showError("Invalid Input", "Fine amount must be a valid number.");
        } catch (Exception e) {
            AlertUtils.showError("Error adding violation: " + e.getMessage());
        }
    }

    @FXML
    private void updateViolationStatus() {
        if (!requireManagePermission()) return;
        Violation selectedViolation = violationTable.getSelectionModel().getSelectedItem();
        if (selectedViolation == null) {
            AlertUtils.showWarning("No Selection", "Please select a violation to update.");
            return;
        }

        String newStatus = selectedViolation.getStatus().equals("Paid") ? "Unpaid" : "Paid";
        selectedViolation.setStatus(newStatus);

        try {
            policeService.updateViolation(selectedViolation);
            AlertUtils.showInfo("Success", "Violation status updated to " + newStatus);
            loadViolations();
        } catch (Exception e) {
            AlertUtils.showError("Error updating violation: " + e.getMessage());
        }
    }

    @FXML
    private void clearReportFields() {
        if (!requireManagePermission()) return;
        vehicleSearchField.clear();
        reportTypeChoiceBox.setValue(null);
        reportDescriptionArea.clear();
        officerNameField.clear();
    }

    @FXML
    private void clearViolationFields() {
        if (!requireManagePermission()) return;
        vehicleSearchField.clear();
        violationTypeField.clear();
        fineAmountField.clear();
        violationDescriptionArea.clear();
        violationStatusChoiceBox.setValue(null);
    }

    @FXML
    private void viewUnpaidViolations() {
        try {
            List<Violation> unpaid = policeService.getUnpaidViolations();
            violationList.clear();
            violationList.addAll(unpaid);
            AlertUtils.showInfo("Unpaid Violations", "Loaded " + unpaid.size() + " unpaid violations.");
        } catch (Exception e) {
            AlertUtils.showError("Error loading unpaid violations: " + e.getMessage());
        }
    }

    @FXML
    private void viewAllViolations() {
        loadViolations();
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
        SceneNavigator.switchScene(event, "/fxml/dashboard.fxml");
    }
}


