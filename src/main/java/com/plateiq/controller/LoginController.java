package com.plateiq.controller;

import com.plateiq.model.*;
import com.plateiq.service.UserService;
import com.plateiq.utils.AlertUtils;
import com.plateiq.utils.SceneNavigator;
import com.plateiq.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField passwordVisibleField;

    @FXML
    private Button togglePasswordVisibilityButton;

    private boolean passwordVisible = false;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        passwordVisibleField.managedProperty().bind(passwordVisibleField.visibleProperty());
        passwordField.managedProperty().bind(passwordField.visibleProperty());
        passwordField.visibleProperty().bind(passwordVisibleField.visibleProperty().not());
        passwordVisibleField.setVisible(false);
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    private void togglePasswordVisibility(ActionEvent event) {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passwordField.setVisible(false);
            passwordVisibleField.setVisible(true);
            togglePasswordVisibilityButton.setText("Hide");
            passwordVisibleField.requestFocus();
            passwordVisibleField.positionCaret(passwordVisibleField.getText().length());
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

        if (username.isBlank() || password.isBlank()) {
            AlertUtils.showWarning("Login Failed", "Username and password must not be empty.");
            return;
        }

        try {
            User user = userService.authenticate(username, password);
            if (user != null) {
                SessionManager.setCurrentUser(user);
                String dashboardPath = user.displayDashboard();
                SceneNavigator.switchScene(event, dashboardPath);
            } else {
                AlertUtils.showError("Login Failed", "Invalid credentials.");
            }
        } catch (Exception e) {
            AlertUtils.showError("Login Error", e.getMessage());
        }
    }
}
