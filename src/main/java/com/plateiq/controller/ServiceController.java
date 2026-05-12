package com.plateiq.controller;

import com.plateiq.model.ServiceRecord;
import com.plateiq.model.Vehicle;
import com.plateiq.service.ServiceRecordService;
import com.plateiq.service.VehicleService;
import com.plateiq.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the service management module.
 * Manages service records with pagination and progress tracking.
 */
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
    private TableColumn<ServiceRecord, Double> colCost;

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

    private ServiceRecordService serviceRecordService;
    private VehicleService vehicleService;
    private ObservableList<ServiceRecord> serviceList;
    private Vehicle selectedVehicle;

    public ServiceController() {
        this.serviceRecordService = new ServiceRecordService();
        this.vehicleService = new VehicleService();
        this.serviceList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colServiceId.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        colVehiclePlate.setCellValueFactory(new PropertyValueFactory<>("vehiclePlate"));
        colServiceType.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        serviceTable.setItems(serviceList);
        setupPagination();
        loadServices();
    }

    private void setupPagination() {
        pagination.setPageFactory(this::createServiceView);
    }

    private void loadServices() {
        try {
            List<ServiceRecord> services = serviceRecordService.getAllServiceRecords();
            serviceList.clear();
            serviceList.addAll(services);
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

    private Node createServiceView(int pageIndex) {
        Label pageLabel = new Label("Page " + (pageIndex + 1));
        pageLabel.setStyle("-fx-padding: 20; -fx-alignment: center;");
        return new VBox(10, pageLabel, serviceTable);
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
            serviceList.clear();
            serviceList.addAll(services);
            updateProgress(services.size());
        } catch (Exception e) {
            AlertUtils.showError("Error searching services: " + e.getMessage());
        }
    }

    @FXML
    private void addServiceRecord() {
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

            serviceRecordService.addServiceRecord(service);
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
}
