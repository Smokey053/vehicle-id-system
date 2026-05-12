package com.plateiq.controller;

import com.plateiq.database.DBConnection;
import com.plateiq.model.User;
import com.plateiq.utils.AlertUtils;
import com.plateiq.utils.SceneNavigator;
import com.plateiq.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        }
    }

    @FXML
    private void handleHome(ActionEvent event) {
        try {
            User user = SessionManager.getCurrentUser();
            SceneNavigator.switchScene(event, user != null ? user.displayDashboard() : "/fxml/dashboard.fxml");
        } catch (Exception e) {
            AlertUtils.showError("Navigation Error", e.getMessage());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();
        SceneNavigator.switchScene(event, "/fxml/login.fxml");
    }

    @FXML
    private void handleExit(ActionEvent event) {
        DBConnection.closeConnection();
        System.exit(0);
    }

    @FXML
    private void openVehicleModule(ActionEvent event) {
        SceneNavigator.switchScene(event, "/fxml/vehicle.fxml");
    }

    @FXML
    private void openServiceModule(ActionEvent event) {
        SceneNavigator.switchScene(event, "/fxml/service.fxml");
    }

    @FXML
    private void openInsuranceModule(ActionEvent event) {
        SceneNavigator.switchScene(event, "/fxml/insurance.fxml");
    }

    @FXML
    private void openPoliceModule(ActionEvent event) {
        SceneNavigator.switchScene(event, "/fxml/police.fxml");
    }

    @FXML
    private void openCustomerModule(ActionEvent event) {
        SceneNavigator.switchScene(event, "/fxml/customer.fxml");
    }
}
