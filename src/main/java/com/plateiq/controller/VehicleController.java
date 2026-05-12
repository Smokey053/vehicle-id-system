package com.plateiq.controller;

import com.plateiq.model.Vehicle;
import com.plateiq.service.VehicleService;
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
import java.util.List;
import java.util.ResourceBundle;

public class VehicleController implements Initializable {

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

    private VehicleService vehicleService;
    private ObservableList<Vehicle> vehicleList;
    private boolean canManageVehicles;

    public VehicleController() {
        this.vehicleService = new VehicleService();
        this.vehicleList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!AccessControl.enforceOrRedirect(vehicleTable, AccessControl.Module.VEHICLE)) {
            return;
        }
        colPlateNumber.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colOwnerName.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        colOwnerPhone.setCellValueFactory(new PropertyValueFactory<>("ownerPhone"));

        canManageVehicles = AccessControl.canManageVehicles(SessionManager.getCurrentUser());
        applyFeaturePermissions();
        vehicleTable.setItems(vehicleList);
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

    private boolean requireManagePermission() {
        if (canManageVehicles) {
            return true;
        }
        AlertUtils.showWarning("Access Denied", "You have read-only access in Vehicle Management.");
        return false;
    }

    private void loadVehicles() {
        try {
            List<Vehicle> vehicles = vehicleService.getAllVehicles();
            vehicleList.clear();
            vehicleList.addAll(vehicles);
        } catch (Exception e) {
            AlertUtils.showError("Error loading vehicles: " + e.getMessage());
        }
    }

    @FXML
    private void searchVehicle() {
        String searchTerm = searchField.getText();
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            loadVehicles();
            return;
        }
        try {
            List<Vehicle> vehicles = vehicleService.searchVehicles(searchTerm);
            vehicleList.clear();
            vehicleList.addAll(vehicles);
        } catch (Exception e) {
            AlertUtils.showError("Error searching vehicles: " + e.getMessage());
        }
    }

    @FXML
    private void addVehicle() {
        if (!requireManagePermission()) return;
        if (!validateFields()) return;

        try {
            Vehicle vehicle = new Vehicle();
            vehicle.setPlateNumber(plateNumberField.getText());
            vehicle.setBrand(brandField.getText());
            vehicle.setModel(modelField.getText());
            vehicle.setYear(Integer.parseInt(yearField.getText()));
            vehicle.setOwnerName(ownerNameField.getText());
            vehicle.setOwnerPhone(ownerPhoneField.getText());
            vehicle.setColor(colorField != null ? colorField.getText() : null);

            vehicleService.addVehicle(vehicle);
            AlertUtils.showInfo("Vehicle added successfully!");
            clearFields();
            loadVehicles();
        } catch (Exception e) {
            AlertUtils.showError("Error adding vehicle: " + e.getMessage());
        }
    }

    @FXML
    private void updateVehicle() {
        if (!requireManagePermission()) return;
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning("Please select a vehicle to update.");
            return;
        }

        if (!validateFields()) return;

        try {
            selected.setBrand(brandField.getText());
            selected.setModel(modelField.getText());
            selected.setYear(Integer.parseInt(yearField.getText()));
            selected.setOwnerName(ownerNameField.getText());
            selected.setOwnerPhone(ownerPhoneField.getText());
            selected.setColor(colorField != null ? colorField.getText() : null);

            vehicleService.updateVehicle(selected);
            AlertUtils.showInfo("Vehicle updated successfully!");
            clearFields();
            loadVehicles();
        } catch (Exception e) {
            AlertUtils.showError("Error updating vehicle: " + e.getMessage());
        }
    }

    @FXML
    private void deleteVehicle() {
        if (!requireManagePermission()) return;
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning("Please select a vehicle to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setContentText("Are you sure you want to delete this vehicle?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                vehicleService.deleteVehicle(selected.getPlateNumber());
                AlertUtils.showInfo("Vehicle deleted successfully!");
                clearFields();
                loadVehicles();
            } catch (Exception e) {
                AlertUtils.showError("Error deleting vehicle: " + e.getMessage());
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
        }
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
        SceneNavigator.switchScene(event, "/fxml/dashboard.fxml");
    }

    @FXML
    private void editVehicle() {
        if (!requireManagePermission()) return;
        updateVehicle();
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        loadVehicles();
    }

    private boolean validateFields() {
        if (plateNumberField.getText().isEmpty() || brandField.getText().isEmpty() ||
            modelField.getText().isEmpty() || yearField.getText().isEmpty() ||
            ownerNameField.getText().isEmpty()) {
            AlertUtils.showWarning("Please fill in all required fields.");
            return false;
        }
        try {
            Integer.parseInt(yearField.getText());
        } catch (NumberFormatException e) {
            AlertUtils.showWarning("Year must be a valid number.");
            return false;
        }
        return true;
    }

    private void clearFields() {
        if (!requireManagePermission()) return;
        plateNumberField.clear();
        brandField.clear();
        modelField.clear();
        yearField.clear();
        ownerNameField.clear();
        ownerPhoneField.clear();
        if (colorField != null) {
            colorField.clear();
        }
    }
}


