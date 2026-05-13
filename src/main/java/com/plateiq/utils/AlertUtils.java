package com.plateiq.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Font;

// Manages alert dialog display operations.
public class AlertUtils {
    
    // Displays an error dialog with title and message.
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #f8d7da; -fx-border-color: #f5c6cb;");
        alert.showAndWait();
    }

    public static void showError(String message) {
        showError("Error", message);
    }
    
    // Displays an error dialog with title, header, and message.
    public static void showError(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #f8d7da; -fx-border-color: #f5c6cb;");
        alert.showAndWait();
    }
    
    // Displays an info dialog with title and message.
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #d1ecf1; -fx-border-color: #bee5eb;");
        alert.showAndWait();
    }

    public static void showInfo(String message) {
        showInfo("Info", message);
    }
    
    // Displays an info dialog with title, header, and message.
    public static void showInfo(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #d1ecf1; -fx-border-color: #bee5eb;");
        alert.showAndWait();
    }
    
    // Displays a warning dialog with title and message.
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #fff3cd; -fx-border-color: #ffeeba;");
        alert.showAndWait();
    }

    public static void showWarning(String message) {
        showWarning("Warning", message);
    }
    
    // Displays a warning dialog with title, header, and message.
    public static void showWarning(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #fff3cd; -fx-border-color: #ffeeba;");
        alert.showAndWait();
    }
    
    // Displays a confirmation dialog and returns user selection.
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #d1e7dd; -fx-border-color: #badbcc;");
        
        ButtonType okButton = new ButtonType("OK");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, cancelButton);
        
        return alert.showAndWait().orElse(cancelButton) == okButton;
    }
    
    // Displays a confirmation dialog with header and returns user selection.
    public static boolean showConfirmation(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #d1e7dd; -fx-border-color: #badbcc;");
        
        ButtonType okButton = new ButtonType("OK");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, cancelButton);
        
        return alert.showAndWait().orElse(cancelButton) == okButton;
    }
    
    // Applies drop shadow effect to dialog pane for visual enhancement.
    public static void applyDropShadow(DialogPane dialogPane) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setOffsetX(3);
        dropShadow.setOffsetY(3);
        dropShadow.setColor(Color.color(0.4, 0.4, 0.4));
        dialogPane.setEffect(dropShadow);
    }
    
    // Applies modern font style to dialog pane.
    public static void applyModernFont(DialogPane dialogPane) {
        dialogPane.setStyle("-fx-font-family: 'Segoe UI', Arial, sans-serif;");
    }
}
