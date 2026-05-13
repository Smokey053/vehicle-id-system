package com.plateiq.controller;

import com.plateiq.model.ServiceRecord;
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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ServiceController implements Initializable {

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
    private TableView<ServiceRecord> serviceTable;

    @FXML
    private TableColumn<ServiceRecord, Integer> colServiceId;

    @FXML
    private TableColumn<ServiceRecord, String> colVehiclePlate;

    @FXML
    private TableColumn<ServiceRecord, String> colServiceType;

    @FXML
    private TableColumn<ServiceRecord, String> colDescription;

    @FXML
    private TableColumn<ServiceRecord, BigDecimal> colCost;

    @FXML
    private TextField vehicleSearchField;

    @FXML
    private ChoiceBox<String> vehicleQuickFilterChoiceBox;

    @FXML
    private Label selectedVehicleSummaryLabel;

    @FXML
    private TextField serviceTypeField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField costField;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private Pagination pagination;

    @FXML
    private Button addServiceButton;

    @FXML
    private Button clearFormButton;

    private static final int PAGE_SIZE = 15;
    private final ServiceRecordService serviceRecordService =
        new ServiceRecordService();
    private final VehicleService vehicleService = new VehicleService();
    private final InsuranceService insuranceService = new InsuranceService();
    private final ObservableList<Vehicle> vehicleDirectoryList =
        FXCollections.observableArrayList();
    private final List<Vehicle> allVehicleDirectory = new ArrayList<>();
    private final ObservableList<ServiceRecord> serviceList =
        FXCollections.observableArrayList();
    private final List<ServiceRecord> currentSource = new ArrayList<>();
    private Vehicle selectedVehicle;
    private boolean canManageServices;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (
            !AccessControl.enforceOrRedirect(
                serviceTable,
                AccessControl.Module.SERVICE
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
        colVehiclePlate.setCellValueFactory(
            new PropertyValueFactory<>("vehiclePlate")
        );
        colServiceType.setCellValueFactory(
            new PropertyValueFactory<>("serviceType")
        );
        colDescription.setCellValueFactory(
            new PropertyValueFactory<>("description")
        );
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colCost.setCellFactory(column ->
            new TableCell<>() {
                @Override
                protected void updateItem(BigDecimal item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(
                        empty || item == null ? null : item.toPlainString()
                    );
                }
            }
        );

        canManageServices = AccessControl.canManageServices(
            SessionManager.getCurrentUser()
        );
        applyFeaturePermissions();
        setupQuickFilterOptions();
        updateSelectedVehicleRibbon(null);

        vehicleListTable.setItems(vehicleDirectoryList);
        serviceTable.setItems(serviceList);
        StateManager.showEmptyState(
            vehicleListTable,
            "No vehicles available.\n\nVehicle directory entries will appear here."
        );
        StateManager.showEmptyState(
            serviceTable,
            "No service records available.\n\nSearch for a vehicle and add a service record."
        );

        // Setup accessibility
        setupAccessibility();

        pagination
            .currentPageIndexProperty()
            .addListener((obs, oldV, newV) -> updatePage(newV.intValue()));
        loadVehicleDirectory();
        loadServices();
    }

    private void applyFeaturePermissions() {
        setDisabled(serviceTypeField, !canManageServices);
        setDisabled(descriptionArea, !canManageServices);
        setDisabled(costField, !canManageServices);
        setDisabled(addServiceButton, !canManageServices);
        setDisabled(clearFormButton, !canManageServices);
    }

    private void setDisabled(Control control, boolean disabled) {
        if (control != null) {
            control.setDisable(disabled);
        }
    }

    // Setup keyboard navigation and accessibility features
    private void setupAccessibility() {
        AccessibilityHelper.setupFormNavigation(
            vehicleSearchField,
            vehicleQuickFilterChoiceBox,
            serviceTypeField,
            descriptionArea,
            costField,
            addServiceButton,
            clearFormButton
        );

        AccessibilityHelper.setupTableKeyboard(
            serviceTable,
            this::handleTableActivation
        );
        AccessibilityHelper.setupTableKeyboard(
            vehicleListTable,
            this::handleVehicleListActivation
        );
        AccessibilityHelper.setupButtonKeyboard(addServiceButton);
        AccessibilityHelper.addFocusIndicator(vehicleSearchField);
        AccessibilityHelper.addFocusIndicator(vehicleQuickFilterChoiceBox);
        AccessibilityHelper.addFocusIndicator(vehicleListTable);
        AccessibilityHelper.addFocusIndicator(serviceTable);
        Platform.runLater(() ->
            AccessibilityHelper.setInitialFocus(vehicleSearchField)
        );
    }

    private void handleTableActivation() {
        ServiceRecord selected = serviceTable
            .getSelectionModel()
            .getSelectedItem();
        if (selected != null) {
            AccessibilityHelper.announceToScreenReader(
                "Selected service record: ID " +
                    selected.getServiceId() +
                    ", Type: " +
                    selected.getServiceType() +
                    ", Cost: $" +
                    selected.getCost()
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
                "Loaded " + vehicles.size() + " vehicles in workshop directory"
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

        selectedVehicle = selected;
        vehicleSearchField.setText(selected.getPlateNumber());
        updateSelectedVehicleRibbon(selected);
        searchVehicle();

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
            for (var policy : insuranceService.getAllPolicies()) {
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
        if (canManageServices) {
            return true;
        }
        AlertUtils.showWarning(
            "Access Denied",
            "You have read-only access in Service Records."
        );
        return false;
    }

    private void loadServices() {
        StateManager.showLoadingState(serviceTable, true);
        try {
            List<ServiceRecord> services =
                serviceRecordService.getAllServiceRecords();
            setPagedSource(services);

            if (services.isEmpty()) {
                StateManager.showEmptyState(
                    serviceTable,
                    "No service records found.\n\nSearch for a vehicle to add service history."
                );
            }

            AccessibilityHelper.announceToScreenReader(
                "Loaded " + services.size() + " service records"
            );
        } catch (Exception e) {
            StateManager.showErrorState(
                serviceTable,
                "Failed to load services: " + e.getMessage(),
                this::loadServices
            );
            AlertUtils.showError(
                "Error loading service records",
                e.getMessage()
            );
        } finally {
            StateManager.showLoadingState(serviceTable, false);
        }
    }

    private void setPagedSource(List<ServiceRecord> source) {
        currentSource.clear();
        currentSource.addAll(source);
        int pageCount = Math.max(
            1,
            (int) Math.ceil((double) currentSource.size() / PAGE_SIZE)
        );
        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(0);
        updatePage(0);
        updateProgress(currentSource.size());
    }

    private void updateProgress(int count) {
        if (count > 0) {
            double progress = Math.min(count / 100.0, 1.0);
            progressBar.setProgress(progress);
            progressLabel.setText(String.format("Total Services: %d", count));
        } else {
            progressBar.setProgress(0);
            progressLabel.setText("No service records found");
        }
    }

    private void updatePage(int pageIndex) {
        int from = Math.min(pageIndex * PAGE_SIZE, currentSource.size());
        int to = Math.min(from + PAGE_SIZE, currentSource.size());
        serviceList.setAll(currentSource.subList(from, to));
    }

    @FXML
    private void searchVehicle() {
        String searchTerm = vehicleSearchField.getText();
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            selectedVehicle = null;
            updateSelectedVehicleRibbon(null);
            loadServices();
            return;
        }

        StateManager.showLoadingState(serviceTable, true);
        try {
            String normalizedSearch = searchTerm.trim().toUpperCase();
            List<ServiceRecord> results = serviceRecordService.searchByVehicle(
                normalizedSearch
            );
            setPagedSource(results);

            selectedVehicle = vehicleService.getVehicleByRegistration(
                normalizedSearch
            );
            if (selectedVehicle != null) {
                updateSelectedVehicleRibbon(selectedVehicle);
                progressLabel.setText(
                    "Vehicle selected: " + selectedVehicle.getPlateNumber()
                );
                AccessibilityHelper.announceToScreenReader(
                    "Found " +
                        results.size() +
                        " service records for vehicle " +
                        selectedVehicle.getPlateNumber()
                );
            } else {
                updateSelectedVehicleRibbon(null);
                progressLabel.setText(
                    "Search complete. No exact vehicle selected."
                );
                if (results.isEmpty()) {
                    StateManager.showEmptyState(
                        serviceTable,
                        "No service records found for '" +
                            searchTerm +
                            "'\n\nVerify the vehicle registration and try again."
                    );
                }
            }
        } catch (Exception e) {
            StateManager.showErrorState(
                serviceTable,
                "Search failed: " + e.getMessage(),
                this::searchVehicle
            );
            AlertUtils.showError("Error searching services", e.getMessage());
        } finally {
            StateManager.showLoadingState(serviceTable, false);
        }
    }

    @FXML
    private void addServiceRecord() {
        if (!requireManagePermission()) {
            return;
        }

        selectedVehicle = resolveVehicleForEntry();
        if (selectedVehicle == null) {
            AlertUtils.showWarning(
                "No Vehicle Selected",
                "Please enter an exact vehicle registration number before adding a service."
            );
            return;
        }

        String serviceType = serviceTypeField.getText();
        String description = descriptionArea.getText();
        String costText = costField.getText();

        if (
            serviceType == null ||
            serviceType.isBlank() ||
            costText == null ||
            costText.isBlank()
        ) {
            AlertUtils.showWarning(
                "Validation Error",
                "Service type and cost are required."
            );
            return;
        }

        try {
            double cost = Double.parseDouble(costText.trim());
            if (cost < 0) {
                AlertUtils.showWarning(
                    "Validation Error",
                    "Cost cannot be negative."
                );
                return;
            }

            ServiceRecord service = new ServiceRecord(
                0,
                selectedVehicle.getVehicleId(),
                LocalDate.now(),
                serviceType.trim(),
                description == null ? null : description.trim(),
                cost
            );

            boolean created = serviceRecordService.addServiceRecord(service);
            if (!created) {
                AlertUtils.showError(
                    "Create Failed",
                    "Service record could not be added. Confirm vehicle and input details."
                );
                return;
            }
            AlertUtils.showInfo(
                "Success",
                "Service record added successfully."
            );
            AccessibilityHelper.announceToScreenReader(
                "Service record added for vehicle " +
                    selectedVehicle.getPlateNumber()
            );
            clearFieldsInternal();
            loadServices();
        } catch (NumberFormatException e) {
            AlertUtils.showError(
                "Invalid Input",
                "Cost must be a valid number."
            );
        } catch (Exception e) {
            AlertUtils.showError("Error adding service", e.getMessage());
        }
    }

    private Vehicle resolveVehicleForEntry() {
        if (selectedVehicle != null) {
            return selectedVehicle;
        }

        String registration = vehicleSearchField.getText();
        if (registration == null || registration.isBlank()) {
            return null;
        }

        return vehicleService.getVehicleByRegistration(
            registration.trim().toUpperCase()
        );
    }

    @FXML
    private void clearFields() {
        if (!requireManagePermission()) {
            return;
        }
        clearFieldsInternal();
    }

    private void clearFieldsInternal() {
        serviceTypeField.clear();
        descriptionArea.clear();
        costField.clear();
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
