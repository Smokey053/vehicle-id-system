package com.plateiq.controller;

import com.plateiq.model.ServiceRecord;
import com.plateiq.model.Vehicle;
import com.plateiq.service.ServiceRecordService;
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

import java.math.BigDecimal;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
    private ServiceRecordService serviceRecordService;
    private VehicleService vehicleService;
    private ObservableList<ServiceRecord> serviceList;
    private Vehicle selectedVehicle;
    private boolean canManageServices;

    public ServiceController() {
        this.serviceRecordService = new ServiceRecordService();
        this.vehicleService = new VehicleService();
        this.serviceList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!AccessControl.enforceOrRedirect(serviceTable, AccessControl.Module.SERVICE)) {
            return;
        }
        colServiceId.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        colVehiclePlate.setCellValueFactory(new PropertyValueFactory<>("vehiclePlate"));
        colServiceType.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colCost.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toPlainString());
            }
        });

        canManageServices = AccessControl.canManageServices(SessionManager.getCurrentUser());
        applyFeaturePermissions();
        serviceTable.setItems(serviceList);
        pagination.currentPageIndexProperty().addListener((obs, oldV, newV) -> updatePage(newV.intValue()));
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

    private boolean requireManagePermission() {
        if (canManageServices) {
            return true;
        }
        AlertUtils.showWarning("Access Denied", "You have read-only access in Service Records.");
        return false;
    }

    private void loadServices() {
        try {
            List<ServiceRecord> services = serviceRecordService.getAllServiceRecords();
            pagination.setUserData(services);
            int pageCount = Math.max(1, (int) Math.ceil((double) services.size() / PAGE_SIZE));
            pagination.setPageCount(pageCount);
            pagination.setCurrentPageIndex(0);
            updatePage(0);
            updateProgress(services.size());
        } catch (Exception e) {
            AlertUtils.showError("Error loading service records: " + e.getMessage());
        }
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

    @SuppressWarnings("unchecked")
    private void updatePage(int pageIndex) {
        Object raw = pagination.getUserData();
        if (!(raw instanceof List<?> all)) {
            serviceList.clear();
            return;
        }
        List<ServiceRecord> records = (List<ServiceRecord>) all;
        int from = Math.min(pageIndex * PAGE_SIZE, records.size());
        int to = Math.min(from + PAGE_SIZE, records.size());
        serviceList.setAll(records.subList(from, to));
    }

    @FXML
    private void searchVehicle() {
        String searchTerm = vehicleSearchField.getText();
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            loadServices();
            return;
        }
        try {
            List<ServiceRecord> services = serviceRecordService.searchByVehicle(searchTerm);
            pagination.setUserData(services);
            int pageCount = Math.max(1, (int) Math.ceil((double) services.size() / PAGE_SIZE));
            pagination.setPageCount(pageCount);
            pagination.setCurrentPageIndex(0);
            updatePage(0);

            selectedVehicle = vehicleService.getVehicleByRegistration(searchTerm);
            updateProgress(services.size());
        } catch (Exception e) {
            AlertUtils.showError("Error searching services: " + e.getMessage());
        }
    }

    @FXML
    private void addServiceRecord() {
        if (!requireManagePermission()) return;
        if (selectedVehicle == null) {
            AlertUtils.showWarning("No Vehicle Selected", "Please search and select a vehicle first.");
            return;
        }

        String serviceType = serviceTypeField.getText();
        String description = descriptionArea.getText();
        String costText = costField.getText();

        if (serviceType.isBlank() || costText.isBlank()) {
            AlertUtils.showWarning("Validation Error", "Service type and cost are required.");
            return;
        }

        try {
            double cost = Double.parseDouble(costText);
            ServiceRecord service = new ServiceRecord(
                0,
                selectedVehicle.getVehicleId(),
                java.time.LocalDate.now(),
                serviceType,
                description,
                cost
            );

            boolean created = serviceRecordService.addServiceRecord(service);
            if (!created) {
                AlertUtils.showError("Create Failed", "Service record could not be added. Confirm vehicle and input details.");
                return;
            }
            AlertUtils.showInfo("Success", "Service record added successfully.");
            clearFields();
            loadServices();
        } catch (NumberFormatException e) {
            AlertUtils.showError("Invalid Input", "Cost must be a valid number.");
        } catch (Exception e) {
            AlertUtils.showError("Error adding service: " + e.getMessage());
        }
    }

    @FXML
    private void clearFields() {
        if (!requireManagePermission()) return;
        serviceTypeField.clear();
        descriptionArea.clear();
        costField.clear();
        selectedVehicle = null;
    }

    @FXML
    private void onVehicleSearchResult(Vehicle vehicle) {
        this.selectedVehicle = vehicle;
        AlertUtils.showInfo("Vehicle Selected", "Selected: " + vehicle.getPlateNumber());
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
        SceneNavigator.switchScene(event, "/fxml/dashboard.fxml");
    }
}


