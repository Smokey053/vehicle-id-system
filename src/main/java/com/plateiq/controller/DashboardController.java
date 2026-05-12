package com.plateiq.controller;

import com.plateiq.model.User;
import com.plateiq.utils.AccessControl;
import com.plateiq.utils.AlertUtils;
import com.plateiq.utils.SceneNavigator;
import com.plateiq.utils.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button vehicleButton;

    @FXML
    private Button serviceButton;

    @FXML
    private Button insuranceButton;

    @FXML
    private Button policeButton;

    @FXML
    private Button customerButton;

    @FXML
    private void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        }
        applyModuleVisibility(currentUser);
    }

    private void applyModuleVisibility(User user) {
        setButtonVisible(vehicleButton, AccessControl.canAccess(user, AccessControl.Module.VEHICLE));
        setButtonVisible(serviceButton, AccessControl.canAccess(user, AccessControl.Module.SERVICE));
        setButtonVisible(insuranceButton, AccessControl.canAccess(user, AccessControl.Module.INSURANCE));
        setButtonVisible(policeButton, AccessControl.canAccess(user, AccessControl.Module.POLICE));
        setButtonVisible(customerButton, AccessControl.canAccess(user, AccessControl.Module.CUSTOMER));
    }

    private void setButtonVisible(Button button, boolean visible) {
        if (button == null) {
            return;
        }
        button.setVisible(visible);
        button.setManaged(visible);
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
        Platform.exit();
    }

    @FXML
    private void openVehicleModule(ActionEvent event) {
        if (!AccessControl.canAccess(SessionManager.getCurrentUser(), AccessControl.Module.VEHICLE)) {
            AlertUtils.showWarning("Access Denied", "You do not have permission to open Vehicle Management.");
            return;
        }
        SceneNavigator.switchScene(event, "/fxml/vehicle.fxml");
    }

    @FXML
    private void openServiceModule(ActionEvent event) {
        if (!AccessControl.canAccess(SessionManager.getCurrentUser(), AccessControl.Module.SERVICE)) {
            AlertUtils.showWarning("Access Denied", "You do not have permission to open Service Records.");
            return;
        }
        SceneNavigator.switchScene(event, "/fxml/service.fxml");
    }

    @FXML
    private void openInsuranceModule(ActionEvent event) {
        if (!AccessControl.canAccess(SessionManager.getCurrentUser(), AccessControl.Module.INSURANCE)) {
            AlertUtils.showWarning("Access Denied", "You do not have permission to open Insurance.");
            return;
        }
        SceneNavigator.switchScene(event, "/fxml/insurance.fxml");
    }

    @FXML
    private void openPoliceModule(ActionEvent event) {
        if (!AccessControl.canAccess(SessionManager.getCurrentUser(), AccessControl.Module.POLICE)) {
            AlertUtils.showWarning("Access Denied", "You do not have permission to open Police Records.");
            return;
        }
        SceneNavigator.switchScene(event, "/fxml/police.fxml");
    }

    @FXML
    private void openCustomerModule(ActionEvent event) {
        if (!AccessControl.canAccess(SessionManager.getCurrentUser(), AccessControl.Module.CUSTOMER)) {
            AlertUtils.showWarning("Access Denied", "You do not have permission to open Customer Portal.");
            return;
        }
        SceneNavigator.switchScene(event, "/fxml/customer.fxml");
    }
}
