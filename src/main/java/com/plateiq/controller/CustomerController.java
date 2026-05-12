package com.plateiq.controller;

import com.plateiq.model.CustomerQuery;
import com.plateiq.model.InsurancePolicy;
import com.plateiq.model.ServiceRecord;
import com.plateiq.model.Vehicle;
import com.plateiq.service.CustomerService;
import com.plateiq.service.InsuranceService;
import com.plateiq.service.ServiceRecordService;
import com.plateiq.utils.AlertUtils;
import com.plateiq.utils.ReportExporter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the customer module.
 * Allows customers to view vehicle information, service history, and submit queries.
 */
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
    private TableColumn<ServiceRecord, Double> colCost;

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

    private CustomerService customerService;
    private ServiceRecordService serviceRecordService;
    private InsuranceService insuranceService;
    private Vehicle currentVehicle;
    private ObservableList<ServiceRecord> serviceList;
    private ObservableList<InsurancePolicy> insuranceList;

    public CustomerController() {
        this.customerService = new CustomerService();
        this.serviceRecordService = new ServiceRecordService();
        this.insuranceService = new InsuranceService();
        this.serviceList = FXCollections.observableArrayList();
        this.insuranceList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colServiceId.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        colServiceDate.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));
        colServiceType.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        colPolicyNumber.setCellValueFactory(new PropertyValueFactory<>("policyNumber"));
        colCompany.setCellValueFactory(new PropertyValueFactory<>("insuranceCompany"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        serviceHistoryTable.setItems(serviceList);
        insuranceTable.setItems(insuranceList);
    }

    @FXML
    private void searchVehicle() {
        String plateNumber = vehicleSearchField.getText();
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            AlertUtils.showWarning("Search Error", "Please enter a vehicle registration number.");
            return;
        }

        try {
            currentVehicle = customerService.getVehicleByPlate(plateNumber);
            if (currentVehicle != null) {
                displayVehicleInfo(currentVehicle);
                loadServiceHistory(currentVehicle.getVehicleId());
                loadInsuranceInfo(currentVehicle.getVehicleId());
                AlertUtils.showInfo("Vehicle Found", "Vehicle information loaded successfully.");
            } else {
                clearVehicleInfo();
                AlertUtils.showError("Vehicle Not Found", "No vehicle found with registration number: " + plateNumber);
            }
        } catch (Exception e) {
            AlertUtils.showError("Search Error", e.getMessage());
        }
    }

    private void displayVehicleInfo(Vehicle vehicle) {
        vehicleInfoLabel.setText(vehicle.getPlateNumber() + " - " + vehicle.getBrand() + " " + vehicle.getModel());
        ownerNameLabel.setText(vehicle.getOwnerName());
        ownerPhoneLabel.setText(vehicle.getOwnerPhone());
        vehicleDetailsLabel.setText(String.format(
            "Year: %d | Color: %s",
            vehicle.getYear(),
            vehicle.getColor()
        ));
    }

    private void clearVehicleInfo() {
        vehicleInfoLabel.setText("No vehicle selected");
        ownerNameLabel.setText("-");
        ownerPhoneLabel.setText("-");
        vehicleDetailsLabel.setText("-");
        serviceList.clear();
        insuranceList.clear();
    }

    private void loadServiceHistory(int vehicleId) {
        try {
            List<ServiceRecord> services = serviceRecordService.getServiceByVehicleId(vehicleId);
            serviceList.clear();
            serviceList.addAll(services);
        } catch (Exception e) {
            AlertUtils.showError("Error loading service history: " + e.getMessage());
        }
    }

    private void loadInsuranceInfo(int vehicleId) {
        try {
            List<InsurancePolicy> policies = insuranceService.getPoliciesByVehicleId(vehicleId);
            insuranceList.clear();
            insuranceList.addAll(policies);
        } catch (Exception e) {
            AlertUtils.showError("Error loading insurance info: " + e.getMessage());
        }
    }

    @FXML
    private void submitQuery() {
        String queryText = queryTextArea.getText();
        if (queryText == null || queryText.trim().isEmpty()) {
            AlertUtils.showWarning("Validation Error", "Please enter your query.");
            return;
        }

        if (currentVehicle == null) {
            AlertUtils.showWarning("No Vehicle", "Please search for a vehicle first.");
            return;
        }

        try {
            CustomerQuery query = new CustomerQuery(
                0,
                1,
                currentVehicle.getVehicleId(),
                LocalDate.now(),
                queryText,
                null
            );

            customerService.submitQuery(query);
            AlertUtils.showInfo("Query Submitted", "Your query has been submitted and will be responded to shortly.");
            queryTextArea.clear();
        } catch (Exception e) {
            AlertUtils.showError("Error submitting query: " + e.getMessage());
        }
    }

    @FXML
    private void exportVehicleReport() {
        if (currentVehicle == null) {
            AlertUtils.showWarning("No Vehicle", "Please search for a vehicle first.");
            return;
        }

        try {
            String reportContent = generateVehicleReport(currentVehicle);
            ReportExporter.exportToFile(reportContent, "vehicle_report_" + currentVehicle.getPlateNumber() + ".txt");
            AlertUtils.showInfo("Report Exported", "Vehicle report exported successfully.");
        } catch (Exception e) {
            AlertUtils.showError("Error exporting report: " + e.getMessage());
        }
    }

    private String generateVehicleReport(Vehicle vehicle) {
        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("       PLATE IQ - VEHICLE REPORT       \n");
        report.append("========================================\n\n");
        report.append("Vehicle Information:\n");
        report.append("Plate Number: ").append(vehicle.getPlateNumber()).append("\n");
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
            List<ServiceRecord> services = serviceRecordService.getServiceByVehicleId(vehicle.getVehicleId());
            if (services.isEmpty()) {
                report.append("No service records found.\n");
            } else {
                for (ServiceRecord service : services) {
                    report.append(String.format("Date: %s | Type: %s | Cost: $%.2f\n",
                        service.getServiceDate(), service.getServiceType(), service.getCost()));
                    report.append("Description: ").append(service.getDescription()).append("\n\n");
                }
            }
        } catch (Exception e) {
            report.append("Error loading service history.\n");
        }

        report.append("Insurance Information:\n");
        report.append("----------------------------------------\n");

        try {
            List<InsurancePolicy> policies = insuranceService.getPoliciesByVehicleId(vehicle.getVehicleId());
            if (policies.isEmpty()) {
                report.append("No insurance policies found.\n");
            } else {
                for (InsurancePolicy policy : policies) {
                    report.append(String.format("Policy: %s | Company: %s | Expires: %s\n",
                        policy.getPolicyNumber(), policy.getInsuranceCompany(), policy.getEndDate()));
                }
            }
        } catch (Exception e) {
            report.append("Error loading insurance information.\n");
        }

        report.append("\n========================================\n");
        report.append("Report Generated: ").append(LocalDate.now()).append("\n");
        report.append("========================================\n");

        return report.toString();
    }

    @FXML
    private void clearSearch() {
        vehicleSearchField.clear();
        clearVehicleInfo();
        queryTextArea.clear();
        responseTextArea.clear();
    }
}