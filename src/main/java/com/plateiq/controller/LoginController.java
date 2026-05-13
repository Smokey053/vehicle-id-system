package com.plateiq.controller;

import com.plateiq.model.*;
import com.plateiq.service.UserService;
import com.plateiq.utils.AccessibilityHelper;
import com.plateiq.utils.AlertUtils;
import com.plateiq.utils.SceneNavigator;
import com.plateiq.utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordVisibleField;

    @FXML
    private Button togglePasswordVisibilityButton;

    @FXML
    private Button loginButton;

    @FXML
    private ProgressIndicator loginProgressIndicator;

    @FXML
    private Label welcomeLabel;

    private boolean passwordVisible = false;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        passwordVisibleField
            .managedProperty()
            .bind(passwordVisibleField.visibleProperty());
        passwordField.managedProperty().bind(passwordField.visibleProperty());
        passwordField
            .visibleProperty()
            .bind(passwordVisibleField.visibleProperty().not());
        passwordVisibleField.setVisible(false);
        passwordVisibleField
            .textProperty()
            .bindBidirectional(passwordField.textProperty());

        if (welcomeLabel != null) {
            FadeTransition fadeTransition = new FadeTransition(
                Duration.seconds(1.8),
                welcomeLabel
            );
            fadeTransition.setFromValue(0.35);
            fadeTransition.setToValue(1.0);
            fadeTransition.setAutoReverse(true);
            fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
            fadeTransition.play();
        }

        // Setup accessibility
        setupAccessibility();
    }

    // Setup keyboard navigation and accessibility features
    private void setupAccessibility() {
        // Setup form navigation: Tab through username -> password -> login button
        AccessibilityHelper.setupFormNavigation(
            usernameField,
            passwordField,
            togglePasswordVisibilityButton,
            loginButton
        );

        // Enable Enter key to submit login from any field
        usernameField.setOnAction(event -> handleLogin(event));
        passwordField.setOnAction(event -> handleLogin(event));
        passwordVisibleField.setOnAction(event -> handleLogin(event));

        // Setup button keyboard activation
        AccessibilityHelper.setupButtonKeyboard(loginButton);
        AccessibilityHelper.setupButtonKeyboard(togglePasswordVisibilityButton);

        // Add focus indicators
        AccessibilityHelper.addFocusIndicator(usernameField);
        AccessibilityHelper.addFocusIndicator(passwordField);
        AccessibilityHelper.addFocusIndicator(passwordVisibleField);
        AccessibilityHelper.addFocusIndicator(loginButton);

        // Set initial focus
        Platform.runLater(() ->
            AccessibilityHelper.setInitialFocus(usernameField)
        );
    }

    @FXML
    private void togglePasswordVisibility(ActionEvent event) {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passwordField.setVisible(false);
            passwordVisibleField.setVisible(true);
            togglePasswordVisibilityButton.setText("Hide");
            passwordVisibleField.requestFocus();
            passwordVisibleField.positionCaret(
                passwordVisibleField.getText().length()
            );
        } else {
            passwordVisibleField.setVisible(false);
            passwordField.setVisible(true);
            togglePasswordVisibilityButton.setText("Show");
            passwordField.requestFocus();
            passwordField.positionCaret(passwordField.getText().length());
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (
            username == null ||
            password == null ||
            username.isBlank() ||
            password.isBlank()
        ) {
            AlertUtils.showWarning(
                "Login Failed",
                "Username and password must not be empty."
            );
            AccessibilityHelper.announceToScreenReader(
                "Login failed: Please enter both username and password"
            );
            return;
        }

        setLoginInProgress(true);
        AccessibilityHelper.announceToScreenReader(
            "Logging in, please wait..."
        );

        try {
            User user = userService.authenticate(username.trim(), password);
            if (user != null) {
                SessionManager.setCurrentUser(user);
                AccessibilityHelper.announceToScreenReader(
                    "Login successful. Welcome " +
                        user.getUsername() +
                        ". Loading dashboard..."
                );
                String dashboardPath = user.displayDashboard();
                SceneNavigator.switchScene(event, dashboardPath);
            } else {
                AlertUtils.showError("Login Failed", "Invalid credentials.");
                AccessibilityHelper.announceToScreenReader(
                    "Login failed: Invalid username or password"
                );
            }
        } catch (Exception e) {
            AlertUtils.showError("Login Error", e.getMessage());
            AccessibilityHelper.announceToScreenReader(
                "Login error: " + e.getMessage()
            );
        } finally {
            setLoginInProgress(false);
        }
    }

    private void setLoginInProgress(boolean inProgress) {
        if (loginButton != null) {
            loginButton.setDisable(inProgress);
        }
        if (loginProgressIndicator != null) {
            loginProgressIndicator.setVisible(inProgress);
            loginProgressIndicator.setManaged(inProgress);
        }
    }

    @FXML
    private void handleHome(ActionEvent event) {
        SceneNavigator.switchScene(event, "/fxml/login.fxml");
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
