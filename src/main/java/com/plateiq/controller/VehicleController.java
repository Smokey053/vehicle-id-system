package com.plateiq.controller;

import com.plateiq.model.Vehicle;
import com.plateiq.service.VehicleService;
import com.plateiq.utils.AccessControl;
import com.plateiq.utils.AccessibilityHelper;
import com.plateiq.utils.AlertUtils;
import com.plateiq.utils.SceneNavigator;
import com.plateiq.utils.SessionManager;
import com.plateiq.utils.StateManager;
import java.net.URL;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class VehicleController implements Initializable {

    private static final int PAGE_SIZE = 12;

    @FXML
    private TableView<Vehicle> vehicleTable;

    @FXML
    private TableColumn<Vehicle, String> colPlateNumber;

    @FXML
    private TableColumn<Vehicle, String> colBrand;

    @FXML
    private TableColumn<Vehicle, String> colModel;

    @FXML
    private TableColumn<Vehicle, Integer> colYear;

    @FXML
    private TableColumn<Vehicle, String> colOwnerName;

    @FXML
    private TableColumn<Vehicle, String> colColor;

    @FXML
    private TableColumn<Vehicle, String> colOwnerPhone;

    @FXML
    private TextField searchField;

    @FXML
    private TextField plateNumberField;

    @FXML
    private TextField brandField;

    @FXML
    private TextField modelField;

    @FXML
    private TextField yearField;

    @FXML
    private TextField ownerNameField;

    @FXML
    private TextField ownerPhoneField;

    @FXML
    private TextField colorField;

    @FXML
    private Button addVehicleButton;

    @FXML
    private Button editVehicleButton;

    @FXML
    private Button deleteVehicleButton;

    @FXML
    private Button clearFormButton;

    @FXML
    private Pagination pagination;

    @FXML
    private ProgressIndicator searchProgressIndicator;

    private final VehicleService vehicleService = new VehicleService();
    private final ObservableList<Vehicle> vehicleList =
        FXCollections.observableArrayList();
    private final List<Vehicle> currentSource = new ArrayList<>();
    private boolean canManageVehicles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (
            !AccessControl.enforceOrRedirect(
                vehicleTable,
                AccessControl.Module.VEHICLE
            )
        ) {
            return;
        }

        colPlateNumber.setCellValueFactory(
            new PropertyValueFactory<>("plateNumber")
        );
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colOwnerName.setCellValueFactory(
            new PropertyValueFactory<>("ownerName")
        );
        colOwnerPhone.setCellValueFactory(
            new PropertyValueFactory<>("ownerPhone")
        );

        canManageVehicles = AccessControl.canManageVehicles(
            SessionManager.getCurrentUser()
        );
        applyFeaturePermissions();

        vehicleTable.setItems(vehicleList);
        StateManager.showEmptyState(
            vehicleTable,
            "No vehicle records found. Click 'Add Vehicle' to create your first entry."
        );

        // Setup accessibility
        setupAccessibility();

        pagination
            .currentPageIndexProperty()
            .addListener((obs, oldV, newV) -> updatePage(newV.intValue()));
        loadVehicles();
    }

    private void applyFeaturePermissions() {
        setDisabled(plateNumberField, !canManageVehicles);
        setDisabled(brandField, !canManageVehicles);
        setDisabled(modelField, !canManageVehicles);
        setDisabled(yearField, !canManageVehicles);
        setDisabled(ownerNameField, !canManageVehicles);
        setDisabled(ownerPhoneField, !canManageVehicles);
        setDisabled(colorField, !canManageVehicles);
        setDisabled(addVehicleButton, !canManageVehicles);
        setDisabled(editVehicleButton, !canManageVehicles);
        setDisabled(deleteVehicleButton, !canManageVehicles);
        setDisabled(clearFormButton, !canManageVehicles);
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
            searchField,
            plateNumberField,
            brandField,
            modelField,
            yearField,
            ownerNameField,
            ownerPhoneField,
            colorField,
            addVehicleButton,
            editVehicleButton,
            deleteVehicleButton,
            clearFormButton
        );

        // Setup table keyboard shortcuts (Enter/Space to edit)
        AccessibilityHelper.setupTableKeyboard(
            vehicleTable,
            this::handleTableActivation
        );

        // Setup button keyboard activation
        AccessibilityHelper.setupButtonKeyboard(addVehicleButton);
        AccessibilityHelper.setupButtonKeyboard(editVehicleButton);
        AccessibilityHelper.setupButtonKeyboard(deleteVehicleButton);

        // Add focus indicators
        AccessibilityHelper.addFocusIndicator(searchField);
        AccessibilityHelper.addFocusIndicator(vehicleTable);

        // Set initial focus
        Platform.runLater(() ->
            AccessibilityHelper.setInitialFocus(searchField)
        );
    }

    /**
     * Handle table row activation (Enter/Space key)
     */
    private void handleTableActivation() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            handleTableClick();
            AccessibilityHelper.announceToScreenReader(
                "Selected vehicle: " +
                    selected.getPlateNumber() +
                    " - " +
                    selected.getBrand() +
                    " " +
                    selected.getModel()
            );
        }
    }

    private boolean requireManagePermission() {
        if (canManageVehicles) {
            return true;
        }
        AlertUtils.showWarning(
            "Access Denied",
            "You have read-only access in Vehicle Management."
        );
        return false;
    }

    private void loadVehicles() {
        StateManager.showLoadingState(vehicleTable, true);
        try {
            List<Vehicle> vehicles = vehicleService.getAllVehicles();
            setPagedSource(vehicles);

            if (vehicles.isEmpty()) {
                StateManager.showEmptyState(
                    vehicleTable,
                    "No vehicle records found.\n\nClick 'Add Vehicle' to register your first vehicle."
                );
            }

            AccessibilityHelper.announceToScreenReader(
                "Loaded " + vehicles.size() + " vehicle records"
            );
        } catch (Exception e) {
            StateManager.showErrorState(
                vehicleTable,
                "Failed to load vehicles: " + e.getMessage(),
                this::loadVehicles
            );
            AlertUtils.showError("Error loading vehicles", e.getMessage());
        } finally {
            StateManager.showLoadingState(vehicleTable, false);
        }
    }

    private void setPagedSource(List<Vehicle> source) {
        currentSource.clear();
        currentSource.addAll(source);
        int pageCount = Math.max(
            1,
            (int) Math.ceil((double) currentSource.size() / PAGE_SIZE)
        );
        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(0);
        updatePage(0);
    }

    private void updatePage(int pageIndex) {
        int fromIndex = Math.min(pageIndex * PAGE_SIZE, currentSource.size());
        int toIndex = Math.min(fromIndex + PAGE_SIZE, currentSource.size());
        vehicleList.setAll(currentSource.subList(fromIndex, toIndex));
    }

    @FXML
    private void searchVehicle() {
        String searchTerm = searchField.getText();
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            loadVehicles();
            return;
        }

        setSearchInProgress(true);
        StateManager.showLoadingState(vehicleTable, true);
        try {
            List<Vehicle> results = vehicleService.searchVehicles(
                searchTerm.trim()
            );
            setPagedSource(results);

            if (results.isEmpty()) {
                StateManager.showEmptyState(
                    vehicleTable,
                    "No vehicles found matching '" +
                        searchTerm +
                        "'\n\nTry a different search term or clear the search."
                );
            }

            AccessibilityHelper.announceToScreenReader(
                "Search completed. Found " +
                    results.size() +
                    " matching vehicles"
            );
        } catch (Exception e) {
            StateManager.showErrorState(
                vehicleTable,
                "Search failed: " + e.getMessage(),
                this::searchVehicle
            );
            AlertUtils.showError("Error searching vehicles", e.getMessage());
        } finally {
            setSearchInProgress(false);
            StateManager.showLoadingState(vehicleTable, false);
        }
    }

    private void setSearchInProgress(boolean inProgress) {
        if (searchProgressIndicator != null) {
            searchProgressIndicator.setVisible(inProgress);
            searchProgressIndicator.setManaged(inProgress);
        }
    }

    @FXML
    private void addVehicle() {
        if (!requireManagePermission()) {
            return;
        }
        if (!validateFields()) {
            return;
        }

        try {
            Vehicle vehicle = new Vehicle();
            vehicle.setPlateNumber(
                plateNumberField.getText().trim().toUpperCase()
            );
            vehicle.setBrand(brandField.getText().trim());
            vehicle.setModel(modelField.getText().trim());
            vehicle.setYear(Integer.parseInt(yearField.getText().trim()));
            vehicle.setOwnerName(ownerNameField.getText().trim());
            vehicle.setOwnerPhone(
                ownerPhoneField.getText() == null
                    ? null
                    : ownerPhoneField.getText().trim()
            );
            vehicle.setColor(
                colorField == null ? null : colorField.getText().trim()
            );

            boolean created = vehicleService.addVehicle(vehicle);
            if (!created) {
                AlertUtils.showError(
                    "Create Failed",
                    "Vehicle could not be added. Ensure owner details match an existing customer."
                );
                return;
            }
            AlertUtils.showInfo("Vehicle added successfully.");
            AccessibilityHelper.announceToScreenReader(
                "Vehicle " + vehicle.getPlateNumber() + " added successfully"
            );
            clearFieldsInternal();
            loadVehicles();
        } catch (Exception e) {
            AlertUtils.showError("Error adding vehicle", e.getMessage());
        }
    }

    @FXML
    private void updateVehicle() {
        if (!requireManagePermission()) {
            return;
        }

        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning(
                "Selection Required",
                "Please select a vehicle to update."
            );
            return;
        }

        if (!validateFields()) {
            return;
        }

        try {
            selected.setBrand(brandField.getText().trim());
            selected.setModel(modelField.getText().trim());
            selected.setYear(Integer.parseInt(yearField.getText().trim()));
            selected.setOwnerName(ownerNameField.getText().trim());
            selected.setOwnerPhone(
                ownerPhoneField.getText() == null
                    ? null
                    : ownerPhoneField.getText().trim()
            );
            selected.setColor(
                colorField == null ? null : colorField.getText().trim()
            );

            boolean updated = vehicleService.updateVehicle(selected);
            if (!updated) {
                AlertUtils.showError(
                    "Update Failed",
                    "Vehicle could not be updated. Ensure owner details match an existing customer."
                );
                return;
            }
            AlertUtils.showInfo("Vehicle updated successfully.");
            AccessibilityHelper.announceToScreenReader(
                "Vehicle " + selected.getPlateNumber() + " updated successfully"
            );
            clearFieldsInternal();
            loadVehicles();
        } catch (Exception e) {
            AlertUtils.showError("Error updating vehicle", e.getMessage());
        }
    }

    @FXML
    private void editVehicle() {
        updateVehicle();
    }

    @FXML
    private void deleteVehicle() {
        if (!requireManagePermission()) {
            return;
        }

        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning(
                "Selection Required",
                "Please select a vehicle to delete."
            );
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete vehicle " + selected.getPlateNumber());
        alert.setContentText("This action cannot be undone.");
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean deleted = vehicleService.deleteVehicle(
                    selected.getPlateNumber()
                );
                if (!deleted) {
                    AlertUtils.showError(
                        "Delete Failed",
                        "Vehicle could not be deleted."
                    );
                    return;
                }
                AlertUtils.showInfo("Vehicle deleted successfully.");
                AccessibilityHelper.announceToScreenReader(
                    "Vehicle " +
                        selected.getPlateNumber() +
                        " deleted successfully"
                );
                clearFieldsInternal();
                loadVehicles();
            } catch (Exception e) {
                AlertUtils.showError("Error deleting vehicle", e.getMessage());
            }
        }
    }

    @FXML
    private void handleTableClick() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            plateNumberField.setText(selected.getPlateNumber());
            brandField.setText(selected.getBrand());
            modelField.setText(selected.getModel());
            yearField.setText(String.valueOf(selected.getYear()));
            ownerNameField.setText(selected.getOwnerName());
            ownerPhoneField.setText(selected.getOwnerPhone());
            colorField.setText(selected.getColor());
        }
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        loadVehicles();
    }

    private boolean validateFields() {
        if (
            plateNumberField.getText() == null ||
            plateNumberField.getText().isBlank() ||
            brandField.getText() == null ||
            brandField.getText().isBlank() ||
            modelField.getText() == null ||
            modelField.getText().isBlank() ||
            yearField.getText() == null ||
            yearField.getText().isBlank() ||
            ownerNameField.getText() == null ||
            ownerNameField.getText().isBlank()
        ) {
            AlertUtils.showWarning(
                "Validation Error",
                "Registration, brand, model, year, and owner name are required."
            );
            return false;
        }

        try {
            int year = Integer.parseInt(yearField.getText().trim());
            int maxYear = Year.now().getValue() + 1;
            if (year < 1900 || year > maxYear) {
                AlertUtils.showWarning(
                    "Validation Error",
                    "Year must be between 1900 and " + maxYear + "."
                );
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtils.showWarning(
                "Validation Error",
                "Year must be a valid number."
            );
            return false;
        }

        return true;
    }

    @FXML
    private void clearFields() {
        if (!requireManagePermission()) {
            return;
        }
        clearFieldsInternal();
    }

    private void clearFieldsInternal() {
        plateNumberField.clear();
        brandField.clear();
        modelField.clear();
        yearField.clear();
        ownerNameField.clear();
        ownerPhoneField.clear();
        if (colorField != null) {
            colorField.clear();
        }
        vehicleTable.getSelectionModel().clearSelection();
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
