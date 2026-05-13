package com.plateiq.controller;

import com.plateiq.model.ServiceRecord;
import com.plateiq.model.Vehicle;
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
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

        serviceTable.setItems(serviceList);
        StateManager.showEmptyState(
            serviceTable,
            "No service records available.\n\nSearch for a vehicle and add a service record."
        );

        // Setup accessibility
        setupAccessibility();

        pagination
            .currentPageIndexProperty()
            .addListener((obs, oldV, newV) -> updatePage(newV.intValue()));
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

    /**
     * Setup keyboard navigation and accessibility features
     */
    private void setupAccessibility() {
        AccessibilityHelper.setupFormNavigation(
            vehicleSearchField,
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
        AccessibilityHelper.setupButtonKeyboard(addServiceButton);
        AccessibilityHelper.addFocusIndicator(vehicleSearchField);
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
