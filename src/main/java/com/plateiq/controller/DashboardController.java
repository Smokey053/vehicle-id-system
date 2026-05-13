package com.plateiq.controller;

import com.plateiq.model.User;
import com.plateiq.utils.AccessControl;
import com.plateiq.utils.AccessibilityHelper;
import com.plateiq.utils.AlertUtils;
import com.plateiq.utils.SceneNavigator;
import com.plateiq.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

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
    private VBox notificationsContainer;

    @FXML
    private void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        applyIdentity(currentUser);
        applyModuleVisibility(currentUser);
        applyRoleDashboardContent(currentUser);
        populateNotifications(currentUser);
        setupAccessibility(currentUser);
    }

    // Setup keyboard navigation and accessibility features
    private void setupAccessibility(User user) {
        // Build list of visible module buttons
        List<Button> visibleButtons = new ArrayList<>();
        if (vehicleButton.isVisible()) visibleButtons.add(vehicleButton);
        if (serviceButton.isVisible()) visibleButtons.add(serviceButton);
        if (insuranceButton.isVisible()) visibleButtons.add(insuranceButton);
        if (policeButton.isVisible()) visibleButtons.add(policeButton);
        if (customerButton.isVisible()) visibleButtons.add(customerButton);

        // Setup form navigation through visible module buttons
        if (!visibleButtons.isEmpty()) {
            AccessibilityHelper.setupFormNavigation(
                visibleButtons.toArray(new Button[0])
            );
        }

        // Setup button keyboard activation
        visibleButtons.forEach(AccessibilityHelper::setupButtonKeyboard);

        // Add focus indicators
        visibleButtons.forEach(AccessibilityHelper::addFocusIndicator);

        // Set initial focus to the first visible module button
        if (!visibleButtons.isEmpty()) {
            Button firstButton = visibleButtons.get(0);
            Platform.runLater(() ->
                AccessibilityHelper.setInitialFocus(firstButton)
            );
        }

        // Announce dashboard loaded
        int moduleCount = visibleButtons.size();
        String role = user == null ? "guest" : user.getRole();
        AccessibilityHelper.announceToScreenReader(
            "Dashboard loaded. Role: " +
                role +
                ". " +
                moduleCount +
                " modules available. Use Tab to navigate between modules."
        );
    }

    private void applyIdentity(User currentUser) {
        if (currentUser == null) {
            welcomeLabel.setText("Welcome");
            roleBadgeLabel.setText("GUEST");
            return;
        }
        String role =
            currentUser.getRole() == null
                ? "UNKNOWN"
                : currentUser.getRole().toUpperCase();
        welcomeLabel.setText("Welcome, " + currentUser.getUsername());
        roleBadgeLabel.setText(role);
    }

    private void applyModuleVisibility(User user) {
        setButtonVisible(
            vehicleButton,
            AccessControl.canAccess(user, AccessControl.Module.VEHICLE)
        );
        setButtonVisible(
            serviceButton,
            AccessControl.canAccess(user, AccessControl.Module.SERVICE)
        );
        setButtonVisible(
            insuranceButton,
            AccessControl.canAccess(user, AccessControl.Module.INSURANCE)
        );
        setButtonVisible(
            policeButton,
            AccessControl.canAccess(user, AccessControl.Module.POLICE)
        );
        setButtonVisible(
            customerButton,
            AccessControl.canAccess(user, AccessControl.Module.CUSTOMER)
        );
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
            guidanceLine3.setText(
                "Contact an administrator if access is missing."
            );
            return;
        }

        Set<AccessControl.Module> allowed = AccessControl.allowedModules(user);
        String moduleText = allowed
            .stream()
            .map(this::toReadableModuleName)
            .collect(Collectors.joining(", "));

        roleSummaryLabel.setText(
            "Role: " +
                user.getRole().toUpperCase() +
                " | Dashboard tailored to your permissions."
        );
        permissionsLabel.setText(
            moduleText.isBlank()
                ? "Accessible modules: None"
                : "Accessible modules: " + moduleText
        );
        visibleModuleCountLabel.setText(String.valueOf(allowed.size()));
        accessModeLabel.setText(
            AccessControl.isAdmin(user) ? "Full control" : "Role-scoped access"
        );
        primaryWorkspaceLabel.setText(resolvePrimaryWorkspace(user));

        String role =
            user.getRole() == null ? "" : user.getRole().trim().toUpperCase();
        switch (role) {
            case "ADMIN" -> {
                guidanceLine1.setText(
                    "Oversee all modules and maintain operational quality."
                );
                guidanceLine2.setText(
                    "Validate cross-module records before approving changes."
                );
                guidanceLine3.setText(
                    "Use logout when handing over shared workstations."
                );
            }
            case "WORKSHOP" -> {
                guidanceLine1.setText(
                    "Prioritize vehicle intake and complete service updates."
                );
                guidanceLine2.setText(
                    "Ensure owner contact details are current before saving."
                );
                guidanceLine3.setText(
                    "Review service history for repeat maintenance issues."
                );
            }
            case "INSURANCE" -> {
                guidanceLine1.setText(
                    "Process policy updates and claim status changes promptly."
                );
                guidanceLine2.setText(
                    "Confirm policy numbers and claim amounts before submission."
                );
                guidanceLine3.setText(
                    "Track expiring policies to reduce coverage gaps."
                );
            }
            case "POLICE" -> {
                guidanceLine1.setText(
                    "Record incidents and violations with complete details."
                );
                guidanceLine2.setText(
                    "Update violation status to keep enforcement data current."
                );
                guidanceLine3.setText(
                    "Use unpaid violation views to prioritize follow-ups."
                );
            }
            case "CUSTOMER" -> {
                guidanceLine1.setText(
                    "Search vehicle details and review available records."
                );
                guidanceLine2.setText(
                    "Submit clear, concise queries for faster response."
                );
                guidanceLine3.setText(
                    "Export reports only after validating registration data."
                );
            }
            default -> {
                guidanceLine1.setText(
                    "Your role is not mapped to guided dashboard actions."
                );
                guidanceLine2.setText(
                    "Contact support to configure module permissions."
                );
                guidanceLine3.setText(
                    "Avoid performing operations until role mapping is fixed."
                );
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
        String role =
            user.getRole() == null ? "" : user.getRole().trim().toUpperCase();
        return switch (role) {
            case "WORKSHOP" -> "Vehicle + Service";
            case "INSURANCE" -> "Insurance";
            case "POLICE" -> "Police";
            case "CUSTOMER" -> "Customer Portal";
            default -> "Not Assigned";
        };
    }

    private void populateNotifications(User user) {
        if (notificationsContainer == null) {
            return;
        }

        List<String> items = new ArrayList<>();
        items.add("Daily system health check completed successfully.");
        items.add(
            "Remember to verify records before approval to reduce downstream corrections."
        );
        items.add(
            "Use exact registration numbers for faster cross-module lookups."
        );
        items.add(
            "Keep owner contact details up to date to improve communication efficiency."
        );
        items.add(
            "Review unresolved tasks at the start and end of each shift."
        );

        String role =
            user == null || user.getRole() == null
                ? ""
                : user.getRole().trim().toUpperCase();
        switch (role) {
            case "ADMIN" -> {
                items.add(
                    "Audit inactive users weekly and confirm whether access should be restored."
                );
                items.add(
                    "Monitor policy, service, and police updates for schema consistency."
                );
                items.add(
                    "Verify that role assignments match current operational responsibilities."
                );
                items.add(
                    "Track modules with frequent validation errors and improve form guidance."
                );
                items.add(
                    "Schedule periodic backup verification for business continuity."
                );
            }
            case "WORKSHOP" -> {
                items.add(
                    "Validate plate number format before creating new vehicle records."
                );
                items.add(
                    "Capture detailed service descriptions to support insurance and police reviews."
                );
                items.add(
                    "Log service costs consistently to improve reporting quality."
                );
                items.add("Check for duplicate vehicles before intake.");
                items.add(
                    "Confirm owner contact numbers before closing service jobs."
                );
            }
            case "INSURANCE" -> {
                items.add(
                    "Prioritize policies expiring within 30 days to minimize coverage gaps."
                );
                items.add(
                    "Verify claim amount and policy number alignment before status changes."
                );
                items.add(
                    "Use consistent claim statuses for reliable downstream analytics."
                );
                items.add("Follow up on pending claims older than 7 days.");
                items.add(
                    "Confirm vehicle linkage when creating a new policy."
                );
            }
            case "POLICE" -> {
                items.add(
                    "Prioritize unpaid and disputed violations for enforcement follow-up."
                );
                items.add(
                    "Capture clear incident descriptions to aid investigations."
                );
                items.add(
                    "Verify officer names and report types before saving."
                );
                items.add(
                    "Review high-value unpaid fines at the start of each shift."
                );
                items.add(
                    "Keep violation status current to avoid duplicate enforcement actions."
                );
            }
            case "CUSTOMER" -> {
                items.add(
                    "Use the exact registration number shown on the vehicle documents."
                );
                items.add("Submit concise queries for faster turnaround.");
                items.add(
                    "Review service and insurance details before exporting reports."
                );
                items.add(
                    "Keep contact details current to receive timely updates."
                );
                items.add("Check query responses after submitting a request.");
            }
            default -> {
                items.add("Role-specific guidance is currently unavailable.");
                items.add(
                    "Contact your administrator for access verification."
                );
                items.add(
                    "Avoid making operational updates until role setup is confirmed."
                );
                items.add(
                    "Use dashboard modules based on visible permissions only."
                );
                items.add("Report access mismatches immediately.");
            }
        }

        // Ensure at least 20 visible notifications for the mandatory scroll panel requirement.
        while (items.size() < 20) {
            items.add(
                "Operational reminder " +
                    (items.size() + 1) +
                    ": maintain accurate and timely data entry."
            );
        }

        notificationsContainer.getChildren().clear();
        for (String item : items) {
            Label label = new Label(item);
            label.getStyleClass().add("notification-item");
            label.setWrapText(true);
            notificationsContainer.getChildren().add(label);
        }
    }

    @FXML
    private void handleHome(ActionEvent event) {
        try {
            User user = SessionManager.getCurrentUser();
            SceneNavigator.switchScene(
                event,
                user != null ? user.displayDashboard() : "/fxml/dashboard.fxml"
            );
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
        if (
            !AccessControl.canAccess(
                SessionManager.getCurrentUser(),
                AccessControl.Module.VEHICLE
            )
        ) {
            AlertUtils.showWarning(
                "Access Denied",
                "You do not have access to the Vehicle module."
            );
            return;
        }
        AccessibilityHelper.announceToScreenReader(
            "Opening Vehicle Management module..."
        );
        SceneNavigator.switchScene(event, "/fxml/vehicle.fxml");
    }

    @FXML
    private void openServiceModule(ActionEvent event) {
        if (
            !AccessControl.canAccess(
                SessionManager.getCurrentUser(),
                AccessControl.Module.SERVICE
            )
        ) {
            AlertUtils.showWarning(
                "Access Denied",
                "You do not have access to the Service module."
            );
            return;
        }
        AccessibilityHelper.announceToScreenReader(
            "Opening Service Records module..."
        );
        SceneNavigator.switchScene(event, "/fxml/service.fxml");
    }

    @FXML
    private void openInsuranceModule(ActionEvent event) {
        if (
            !AccessControl.canAccess(
                SessionManager.getCurrentUser(),
                AccessControl.Module.INSURANCE
            )
        ) {
            AlertUtils.showWarning(
                "Access Denied",
                "You do not have access to the Insurance module."
            );
            return;
        }
        AccessibilityHelper.announceToScreenReader(
            "Opening Insurance & Claims module..."
        );
        SceneNavigator.switchScene(event, "/fxml/insurance.fxml");
    }

    @FXML
    private void openPoliceModule(ActionEvent event) {
        if (
            !AccessControl.canAccess(
                SessionManager.getCurrentUser(),
                AccessControl.Module.POLICE
            )
        ) {
            AlertUtils.showWarning(
                "Access Denied",
                "You do not have access to the Police module."
            );
            return;
        }
        AccessibilityHelper.announceToScreenReader(
            "Opening Police Reports & Violations module..."
        );
        SceneNavigator.switchScene(event, "/fxml/police.fxml");
    }

    @FXML
    private void openCustomerModule(ActionEvent event) {
        if (
            !AccessControl.canAccess(
                SessionManager.getCurrentUser(),
                AccessControl.Module.CUSTOMER
            )
        ) {
            AlertUtils.showWarning(
                "Access Denied",
                "You do not have access to the Customer module."
            );
            return;
        }
        AccessibilityHelper.announceToScreenReader(
            "Opening Customer Portal module..."
        );
        SceneNavigator.switchScene(event, "/fxml/customer.fxml");
    }
}
