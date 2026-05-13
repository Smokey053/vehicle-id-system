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

import java.util.Set;
import java.util.stream.Collectors;

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
    private Label roleBadgeLabel;

    @FXML
    private Label roleSummaryLabel;

    @FXML
    private Label permissionsLabel;

    @FXML
    private Label primaryWorkspaceLabel;

    @FXML
    private Label accessModeLabel;

    @FXML
    private Label visibleModuleCountLabel;

    @FXML
    private Label guidanceLine1;

    @FXML
    private Label guidanceLine2;

    @FXML
    private Label guidanceLine3;

    @FXML
    private void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        applyIdentity(currentUser);
        applyModuleVisibility(currentUser);
        applyRoleDashboardContent(currentUser);
    }

    private void applyIdentity(User currentUser) {
        if (currentUser == null) {
            welcomeLabel.setText("Welcome");
            roleBadgeLabel.setText("GUEST");
            return;
        }
        String role = currentUser.getRole() == null ? "UNKNOWN" : currentUser.getRole().toUpperCase();
        welcomeLabel.setText("Welcome, " + currentUser.getUsername());
        roleBadgeLabel.setText(role);
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

    private void applyRoleDashboardContent(User user) {
        if (user == null) {
            roleSummaryLabel.setText("No active session.");
            permissionsLabel.setText("No modules are available.");
            primaryWorkspaceLabel.setText("None");
            accessModeLabel.setText("No access");
            visibleModuleCountLabel.setText("0");
            guidanceLine1.setText("Sign in to access modules.");
            guidanceLine2.setText("Use valid credentials for your role.");
            guidanceLine3.setText("Contact an administrator if access is missing.");
            return;
        }

        Set<AccessControl.Module> allowed = AccessControl.allowedModules(user);
        String moduleText = allowed.stream()
            .map(this::toReadableModuleName)
            .collect(Collectors.joining(", "));

        roleSummaryLabel.setText("Role: " + user.getRole().toUpperCase() + " | Dashboard tailored to your permissions.");
        permissionsLabel.setText(moduleText.isBlank() ? "Accessible modules: None" : "Accessible modules: " + moduleText);
        visibleModuleCountLabel.setText(String.valueOf(allowed.size()));
        accessModeLabel.setText(AccessControl.isAdmin(user) ? "Full control" : "Role-scoped access");
        primaryWorkspaceLabel.setText(resolvePrimaryWorkspace(user));

        String role = user.getRole() == null ? "" : user.getRole().trim().toUpperCase();
        switch (role) {
            case "ADMIN" -> {
                guidanceLine1.setText("Oversee all modules and maintain operational quality.");
                guidanceLine2.setText("Validate cross-module records before approving changes.");
                guidanceLine3.setText("Use logout when handing over shared workstations.");
            }
            case "WORKSHOP" -> {
                guidanceLine1.setText("Prioritize vehicle intake and complete service updates.");
                guidanceLine2.setText("Ensure owner contact details are current before saving.");
                guidanceLine3.setText("Review service history for repeat maintenance issues.");
            }
            case "INSURANCE" -> {
                guidanceLine1.setText("Process policy updates and claim status changes promptly.");
                guidanceLine2.setText("Confirm policy numbers and claim amounts before submission.");
                guidanceLine3.setText("Track expiring policies to reduce coverage gaps.");
            }
            case "POLICE" -> {
                guidanceLine1.setText("Record incidents and violations with complete details.");
                guidanceLine2.setText("Update violation status to keep enforcement data current.");
                guidanceLine3.setText("Use unpaid violation views to prioritize follow-ups.");
            }
            case "CUSTOMER" -> {
                guidanceLine1.setText("Search vehicle details and review available records.");
                guidanceLine2.setText("Submit clear, concise queries for faster response.");
                guidanceLine3.setText("Export reports only after validating registration data.");
            }
            default -> {
                guidanceLine1.setText("Your role is not mapped to guided dashboard actions.");
                guidanceLine2.setText("Contact support to configure module permissions.");
                guidanceLine3.setText("Avoid performing operations until role mapping is fixed.");
            }
        }
    }

    private String toReadableModuleName(AccessControl.Module module) {
        return switch (module) {
            case VEHICLE -> "Vehicle Management";
            case SERVICE -> "Service Records";
            case INSURANCE -> "Insurance";
            case POLICE -> "Police Records";
            case CUSTOMER -> "Customer Portal";
        };
    }

    private String resolvePrimaryWorkspace(User user) {
        if (AccessControl.isAdmin(user)) {
            return "All Modules";
        }
        String role = user.getRole() == null ? "" : user.getRole().trim().toUpperCase();
        return switch (role) {
            case "WORKSHOP" -> "Vehicle + Service";
            case "INSURANCE" -> "Insurance";
            case "POLICE" -> "Police";
            case "CUSTOMER" -> "Customer Portal";
            default -> "Not Assigned";
        };
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
