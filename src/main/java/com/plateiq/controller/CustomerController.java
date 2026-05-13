package com.plateiq.controller;

import com.plateiq.model.CustomerQuery;
import com.plateiq.model.CustomerUser;
import com.plateiq.model.InsurancePolicy;
import com.plateiq.model.ServiceRecord;
import com.plateiq.model.Vehicle;
import com.plateiq.service.CustomerService;
import com.plateiq.service.InsuranceService;
import com.plateiq.service.ServiceRecordService;
import com.plateiq.service.VehicleService;
import com.plateiq.utils.AccessControl;
import com.plateiq.utils.AccessibilityHelper;
import com.plateiq.utils.AlertUtils;
import com.plateiq.utils.ReportExporter;
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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomerController implements Initializable {

    @FXML
    private TextField vehicleSearchField;

    @FXML
    private Label vehicleInfoLabel;

    @FXML
    private Label ownerNameLabel;

    @FXML
    private Label ownerPhoneLabel;

    @FXML
    private Label vehicleDetailsLabel;

    @FXML
    private ChoiceBox<String> vehicleQuickFilterChoiceBox;

    @FXML
    private Label selectedVehicleSummaryLabel;

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
    private TableView<ServiceRecord> serviceHistoryTable;

    @FXML
    private TableColumn<ServiceRecord, Integer> colServiceId;

    @FXML
    private TableColumn<ServiceRecord, LocalDate> colServiceDate;

    @FXML
    private TableColumn<ServiceRecord, String> colServiceType;

    @FXML
    private TableColumn<ServiceRecord, String> colDescription;

    @FXML
    private TableColumn<ServiceRecord, BigDecimal> colCost;

    @FXML
    private TableView<InsurancePolicy> insuranceTable;

    @FXML
    private TableColumn<InsurancePolicy, String> colPolicyNumber;

    @FXML
    private TableColumn<InsurancePolicy, String> colCompany;

    @FXML
    private TableColumn<InsurancePolicy, LocalDate> colStartDate;

    @FXML
    private TableColumn<InsurancePolicy, LocalDate> colEndDate;

    @FXML
    private TextArea queryTextArea;

    @FXML
    private TextArea responseTextArea;

    @FXML
    private Button exportReportButton;

    @FXML
    private Button submitQueryButton;

    private final CustomerService customerService = new CustomerService();
    private final ServiceRecordService serviceRecordService =
        new ServiceRecordService();
    private final InsuranceService insuranceService = new InsuranceService();
    private final VehicleService vehicleService = new VehicleService();
    private Vehicle currentVehicle;
    private final ObservableList<Vehicle> vehicleDirectoryList =
        FXCollections.observableArrayList();
    private final List<Vehicle> allVehicleDirectory = new ArrayList<>();
    private final ObservableList<ServiceRecord> serviceList =
        FXCollections.observableArrayList();
    private final ObservableList<InsurancePolicy> insuranceList =
        FXCollections.observableArrayList();
    private boolean canManageCustomerActions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (
            !AccessControl.enforceOrRedirect(
                serviceHistoryTable,
                AccessControl.Module.CUSTOMER
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

        colServiceId.setCellValueFactory(
            new PropertyValueFactory<>("serviceId")
        );
        colServiceDate.setCellValueFactory(
            new PropertyValueFactory<>("serviceDate")
        );
        colServiceType.setCellValueFactory(
            new PropertyValueFactory<>("serviceType")
        );
        colDescription.setCellValueFactory(
            new PropertyValueFactory<>("description")
        );
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        colPolicyNumber.setCellValueFactory(
            new PropertyValueFactory<>("policyNumber")
        );
        colCompany.setCellValueFactory(
            new PropertyValueFactory<>("insuranceCompany")
        );
        colStartDate.setCellValueFactory(
            new PropertyValueFactory<>("startDate")
        );
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        canManageCustomerActions = AccessControl.canManageCustomerActions(
            SessionManager.getCurrentUser()
        );
        applyFeaturePermissions();

        vehicleListTable.setItems(vehicleDirectoryList);
        serviceHistoryTable.setItems(serviceList);
        insuranceTable.setItems(insuranceList);
        StateManager.showEmptyState(
            vehicleListTable,
            "No vehicles available.\n\nVehicle directory entries will appear here."
        );
        StateManager.showEmptyState(
            serviceHistoryTable,
            "No service history available.\n\nSearch for a vehicle to view its service records."
        );
        StateManager.showEmptyState(
            insuranceTable,
            "No insurance policies found.\n\nSearch for a vehicle to view its insurance information."
        );

        responseTextArea.setText(
            "Search and select a vehicle, then submit a query to receive responses."
        );

        setupQuickFilterOptions();
        updateSelectedVehicleRibbon(null);

        // Setup accessibility
        setupAccessibility();
        loadVehicleDirectory();
    }

    private void applyFeaturePermissions() {
        setDisabled(queryTextArea, !canManageCustomerActions);
        setDisabled(submitQueryButton, !canManageCustomerActions);
        setDisabled(exportReportButton, !canManageCustomerActions);
    }

    private void setDisabled(Control control, boolean disabled) {
        if (control != null) {
            control.setDisable(disabled);
        }
    }

    // Setup keyboard navigation and accessibility features
    private void setupAccessibility() {
        // Setup form navigation with Tab key
        AccessibilityHelper.setupFormNavigation(
            vehicleSearchField,
            vehicleQuickFilterChoiceBox,
            queryTextArea,
            submitQueryButton,
            exportReportButton
        );

        // Setup table keyboard shortcuts (Enter/Space to activate)
        AccessibilityHelper.setupTableKeyboard(
            serviceHistoryTable,
            this::handleServiceTableActivation
        );
        AccessibilityHelper.setupTableKeyboard(
            insuranceTable,
            this::handleInsuranceTableActivation
        );
        AccessibilityHelper.setupTableKeyboard(
            vehicleListTable,
            this::handleVehicleListActivation
        );

        // Setup button keyboard activation
        AccessibilityHelper.setupButtonKeyboard(submitQueryButton);
        AccessibilityHelper.setupButtonKeyboard(exportReportButton);

        // Add focus indicators
        AccessibilityHelper.addFocusIndicator(vehicleSearchField);
        AccessibilityHelper.addFocusIndicator(vehicleQuickFilterChoiceBox);
        AccessibilityHelper.addFocusIndicator(queryTextArea);
        AccessibilityHelper.addFocusIndicator(vehicleListTable);
        AccessibilityHelper.addFocusIndicator(serviceHistoryTable);
        AccessibilityHelper.addFocusIndicator(insuranceTable);

        // Set initial focus
        Platform.runLater(() ->
            AccessibilityHelper.setInitialFocus(vehicleSearchField)
        );
    }

    // Handle service table row activation (Enter/Space key)
    private void handleServiceTableActivation() {
        ServiceRecord selected = serviceHistoryTable
            .getSelectionModel()
            .getSelectedItem();
        if (selected != null) {
            AccessibilityHelper.announceToScreenReader(
                "Selected service record: ID " +
                    selected.getServiceId() +
                    ", Type: " +
                    selected.getServiceType() +
                    ", Date: " +
                    selected.getServiceDate() +
                    ", Cost: $" +
                    selected.getCost()
            );
        }
    }

    // Handle insurance table row activation (Enter/Space key)
    private void handleInsuranceTableActivation() {
        InsurancePolicy selected = insuranceTable
            .getSelectionModel()
            .getSelectedItem();
        if (selected != null) {
            AccessibilityHelper.announceToScreenReader(
                "Selected insurance policy: " +
                    selected.getPolicyNumber() +
                    ", Company: " +
                    selected.getInsuranceCompany() +
                    ", Expires: " +
                    selected.getEndDate()
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
                "Loaded " + vehicles.size() + " vehicles in customer directory"
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

        currentVehicle = selected;
        vehicleSearchField.setText(selected.getPlateNumber());
        updateSelectedVehicleRibbon(selected);
        displayVehicleInfo(selected);
        loadServiceHistory(selected.getVehicleId());
        loadInsuranceInfo(selected.getVehicleId());
        responseTextArea.setText(
            "Vehicle selected from list: " +
                selected.getPlateNumber() +
                ". You can now submit a query or export a report."
        );

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
        if (SessionManager.getCurrentUser() instanceof CustomerUser customer) {
            return source
                .stream()
                .filter(
                    v ->
                        v.getOwnerId() == customer.getCustomerId() ||
                        equalsIgnoreCase(v.getOwnerName(), customer.getName())
                )
                .toList();
        }

        String username = SessionManager.getCurrentUsername();
        if (username == null || username.isBlank()) {
            return List.of();
        }

        return source
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
    }

    private List<Vehicle> filterRecentlyServicedVehicles(List<Vehicle> source) {
        try {
            LocalDate cutoff = LocalDate.now().minusDays(90);
            Set<Integer> servicedVehicleIds = new HashSet<>();
            for (ServiceRecord record : serviceRecordService.getAllServiceRecords()) {
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

    private boolean equalsIgnoreCase(String left, String right) {
        if (left == null || right == null) {
            return false;
        }
        return left.trim().equalsIgnoreCase(right.trim());
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
        if (canManageCustomerActions) {
            return true;
        }
        AlertUtils.showWarning(
            "Access Denied",
            "You have read-only access in Customer Portal."
        );
        return false;
    }

    @FXML
    private void searchVehicle() {
        String plateNumber = safeTrim(vehicleSearchField.getText());
        if (plateNumber.isBlank()) {
            AlertUtils.showWarning(
                "Search Error",
                "Please enter a vehicle registration number."
            );
            AccessibilityHelper.announceToScreenReader(
                "Validation error: Please enter a vehicle registration number"
            );
            return;
        }

        // Show loading state
        StateManager.showLoadingState(serviceHistoryTable, true);
        StateManager.showLoadingState(insuranceTable, true);
        responseTextArea.setText(
            "Searching for vehicle " + plateNumber.toUpperCase() + "..."
        );
        AccessibilityHelper.announceToScreenReader("Searching for vehicle");

        try {
            currentVehicle = customerService.getVehicleByPlate(
                plateNumber.toUpperCase()
            );
            if (currentVehicle != null) {
                displayVehicleInfo(currentVehicle);
                updateSelectedVehicleRibbon(currentVehicle);
                loadServiceHistory(currentVehicle.getVehicleId());
                loadInsuranceInfo(currentVehicle.getVehicleId());
                responseTextArea.setText(
                    "Vehicle loaded successfully. You can now submit a query or export a report."
                );
                AccessibilityHelper.announceToScreenReader(
                    "Vehicle found: " +
                        currentVehicle.getPlateNumber() +
                        " - " +
                        currentVehicle.getBrand() +
                        " " +
                        currentVehicle.getModel()
                );
            } else {
                clearVehicleInfo();
                String errorMsg =
                    "No vehicle found for registration: " +
                    plateNumber.toUpperCase();
                responseTextArea.setText(errorMsg);

                StateManager.showEmptyState(
                    serviceHistoryTable,
                    "Vehicle not found.\n\nPlease verify the registration number and try again."
                );
                StateManager.showEmptyState(
                    insuranceTable,
                    "Vehicle not found.\n\nPlease verify the registration number and try again."
                );

                AccessibilityHelper.announceToScreenReader("Vehicle not found");
                AlertUtils.showError(
                    "Vehicle Not Found",
                    "No vehicle found with registration number: " +
                        plateNumber.toUpperCase() +
                        "\n\nPlease verify the registration number and try again."
                );
            }
        } catch (Exception e) {
            String errorMsg = "Failed to search vehicle: " + e.getMessage();
            responseTextArea.setText(errorMsg);

            StateManager.showErrorState(
                serviceHistoryTable,
                "Failed to load service history: " + e.getMessage(),
                () ->
                    loadServiceHistory(
                        currentVehicle != null
                            ? currentVehicle.getVehicleId()
                            : 0
                    )
            );
            StateManager.showErrorState(
                insuranceTable,
                "Failed to load insurance info: " + e.getMessage(),
                () ->
                    loadInsuranceInfo(
                        currentVehicle != null
                            ? currentVehicle.getVehicleId()
                            : 0
                    )
            );

            AccessibilityHelper.announceToScreenReader(
                "Search failed: " + e.getMessage()
            );
            AlertUtils.showError(
                "Search Error",
                "Failed to search vehicle: " + e.getMessage()
            );
        }
    }

    private void displayVehicleInfo(Vehicle vehicle) {
        vehicleInfoLabel.setText(
            vehicle.getPlateNumber() +
                " - " +
                vehicle.getBrand() +
                " " +
                vehicle.getModel()
        );
        ownerNameLabel.setText(
            vehicle.getOwnerName() == null ? "-" : vehicle.getOwnerName()
        );
        ownerPhoneLabel.setText(
            vehicle.getOwnerPhone() == null ? "-" : vehicle.getOwnerPhone()
        );
        vehicleDetailsLabel.setText(
            String.format(
                "Year: %d | Color: %s",
                vehicle.getYear(),
                vehicle.getColor() == null ? "-" : vehicle.getColor()
            )
        );
    }

    private void clearVehicleInfo() {
        vehicleInfoLabel.setText("No vehicle selected");
        ownerNameLabel.setText("-");
        ownerPhoneLabel.setText("-");
        vehicleDetailsLabel.setText("-");
        serviceList.clear();
        insuranceList.clear();
        currentVehicle = null;
        updateSelectedVehicleRibbon(null);
    }

    private void loadServiceHistory(int vehicleId) {
        StateManager.showLoadingState(serviceHistoryTable, true);
        try {
            List<ServiceRecord> services =
                serviceRecordService.getServiceByVehicleId(vehicleId);
            serviceList.setAll(services);

            if (services.isEmpty()) {
                StateManager.showEmptyState(
                    serviceHistoryTable,
                    "No service history available for this vehicle.\n\nService records will appear here once added."
                );
            }

            AccessibilityHelper.announceToScreenReader(
                "Loaded " + services.size() + " service records"
            );
        } catch (Exception e) {
            StateManager.showErrorState(
                serviceHistoryTable,
                "Failed to load service history: " + e.getMessage(),
                () -> loadServiceHistory(vehicleId)
            );
            AlertUtils.showError(
                "Error loading service history",
                "Failed to load service history: " + e.getMessage()
            );
        }
    }

    private void loadInsuranceInfo(int vehicleId) {
        StateManager.showLoadingState(insuranceTable, true);
        try {
            List<InsurancePolicy> policies =
                insuranceService.getPoliciesByVehicleId(vehicleId);
            insuranceList.setAll(policies);

            if (policies.isEmpty()) {
                StateManager.showEmptyState(
                    insuranceTable,
                    "No insurance policies found for this vehicle.\n\nInsurance information will appear here once added."
                );
            }

            AccessibilityHelper.announceToScreenReader(
                "Loaded " + policies.size() + " insurance policies"
            );
        } catch (Exception e) {
            StateManager.showErrorState(
                insuranceTable,
                "Failed to load insurance info: " + e.getMessage(),
                () -> loadInsuranceInfo(vehicleId)
            );
            AlertUtils.showError(
                "Error loading insurance info",
                "Failed to load insurance information: " + e.getMessage()
            );
        }
    }

    @FXML
    private void submitQuery() {
        if (!requireManagePermission()) {
            return;
        }

        String queryText = safeTrim(queryTextArea.getText());
        if (queryText.isBlank()) {
            AlertUtils.showWarning(
                "Validation Error",
                "Please enter your query."
            );
            AccessibilityHelper.announceToScreenReader(
                "Validation error: Please enter your query"
            );
            return;
        }

        if (currentVehicle == null) {
            AlertUtils.showWarning(
                "No Vehicle",
                "Please search for a vehicle first."
            );
            AccessibilityHelper.announceToScreenReader(
                "Validation error: Please search for a vehicle first"
            );
            return;
        }

        try {
            int customerId = 1;
            if (
                SessionManager.getCurrentUser() instanceof
                    CustomerUser customerUser
            ) {
                customerId = customerUser.getCustomerId();
            }

            CustomerQuery query = new CustomerQuery(
                0,
                customerId,
                currentVehicle.getVehicleId(),
                LocalDate.now(),
                queryText,
                null
            );

            boolean submitted = customerService.submitQuery(query);
            if (!submitted) {
                AlertUtils.showError(
                    "Submit Failed",
                    "Query could not be submitted."
                );
                AccessibilityHelper.announceToScreenReader(
                    "Failed to submit query"
                );
                return;
            }

            // File handling requirement: keep a local query log as well
            ReportExporter.exportCustomerQuery(
                queryText,
                "customer_query_" + LocalDate.now() + ".txt"
            );

            AlertUtils.showInfo(
                "Query Submitted",
                "Your query has been submitted and logged."
            );
            responseTextArea.setText(
                "Query submitted on " +
                    LocalDate.now() +
                    ". Awaiting response from the responsible team."
            );
            queryTextArea.clear();

            AccessibilityHelper.announceToScreenReader(
                "Query submitted successfully. Awaiting response from the responsible team."
            );
        } catch (Exception e) {
            AccessibilityHelper.announceToScreenReader(
                "Error submitting query: " + e.getMessage()
            );
            AlertUtils.showError(
                "Error submitting query",
                "Failed to submit query: " + e.getMessage()
            );
        }
    }

    @FXML
    private void exportVehicleReport() {
        if (!requireManagePermission()) {
            return;
        }

        if (currentVehicle == null) {
            AlertUtils.showWarning(
                "No Vehicle",
                "Please search for a vehicle first."
            );
            AccessibilityHelper.announceToScreenReader(
                "Validation error: Please search for a vehicle first"
            );
            return;
        }

        AccessibilityHelper.announceToScreenReader(
            "Exporting vehicle report for " + currentVehicle.getPlateNumber()
        );

        try {
            String reportContent = generateVehicleReport(currentVehicle);
            boolean exported = ReportExporter.exportToFile(
                reportContent,
                "vehicle_report_" + currentVehicle.getPlateNumber() + ".txt"
            );
            if (!exported) {
                AlertUtils.showError(
                    "Export Failed",
                    "Vehicle report could not be exported."
                );
                AccessibilityHelper.announceToScreenReader(
                    "Failed to export vehicle report"
                );
                return;
            }
            AlertUtils.showInfo(
                "Report Exported",
                "Vehicle report exported successfully."
            );
            AccessibilityHelper.announceToScreenReader(
                "Vehicle report exported successfully for " +
                    currentVehicle.getPlateNumber()
            );
        } catch (Exception e) {
            AccessibilityHelper.announceToScreenReader(
                "Error exporting report: " + e.getMessage()
            );
            AlertUtils.showError(
                "Error exporting report",
                "Failed to export report: " + e.getMessage()
            );
        }
    }

    private String generateVehicleReport(Vehicle vehicle) {
        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("       PLATE IQ - VEHICLE REPORT       \n");
        report.append("========================================\n\n");
        report.append("Vehicle Information:\n");
        report
            .append("Plate Number: ")
            .append(vehicle.getPlateNumber())
            .append("\n");
        report.append("Make: ").append(vehicle.getBrand()).append("\n");
        report.append("Model: ").append(vehicle.getModel()).append("\n");
        report.append("Year: ").append(vehicle.getYear()).append("\n");
        report.append("Color: ").append(vehicle.getColor()).append("\n\n");
        report.append("Owner Information:\n");
        report.append("Name: ").append(vehicle.getOwnerName()).append("\n");
        report.append("Phone: ").append(vehicle.getOwnerPhone()).append("\n\n");
        report.append("Service History:\n");
        report.append("----------------------------------------\n");

        try {
            List<ServiceRecord> services =
                serviceRecordService.getServiceByVehicleId(
                    vehicle.getVehicleId()
                );
            if (services.isEmpty()) {
                report.append("No service records found.\n");
            } else {
                for (ServiceRecord service : services) {
                    report.append(
                        String.format(
                            "Date: %s | Type: %s | Cost: $%.2f\n",
                            service.getServiceDate(),
                            service.getServiceType(),
                            service.getCost()
                        )
                    );
                    report
                        .append("Description: ")
                        .append(service.getDescription())
                        .append("\n\n");
                }
            }
        } catch (Exception e) {
            report.append("Error loading service history.\n");
        }

        report.append("Insurance Information:\n");
        report.append("----------------------------------------\n");

        try {
            List<InsurancePolicy> policies =
                insuranceService.getPoliciesByVehicleId(vehicle.getVehicleId());
            if (policies.isEmpty()) {
                report.append("No insurance policies found.\n");
            } else {
                for (InsurancePolicy policy : policies) {
                    report.append(
                        String.format(
                            "Policy: %s | Company: %s | Expires: %s\n",
                            policy.getPolicyNumber(),
                            policy.getInsuranceCompany(),
                            policy.getEndDate()
                        )
                    );
                }
            }
        } catch (Exception e) {
            report.append("Error loading insurance information.\n");
        }

        report.append("\n========================================\n");
        report
            .append("Report Generated: ")
            .append(LocalDate.now())
            .append("\n");
        report.append("========================================\n");

        return report.toString();
    }

    @FXML
    private void clearSearch() {
        vehicleSearchField.clear();
        queryTextArea.clear();
        responseTextArea.clear();
        vehicleListTable.getSelectionModel().clearSelection();
        clearVehicleInfo();
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
