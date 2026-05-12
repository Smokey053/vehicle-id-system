package com.plateiq;

import com.plateiq.database.DBConnection;
import com.plateiq.utils.AlertUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * Main application entry point for Plate IQ.
 * Handles JavaFX application lifecycle, scene management, and graceful shutdown.
 *
 * @author Plate IQ
 * @version 1.0
 */
public class MainApp extends Application {

    private static final String APP_TITLE = "Plate IQ - Vehicle Management System";
    private static final String APP_ICON = "/icon.png";
    private static final double MIN_WIDTH = 1000;
    private static final double MIN_HEIGHT = 700;

    private Stage primaryStage;
    private Scene loginScene;

    @Override
    public void init() throws Exception {
        super.init();
        // Initialize database connection
        try {
            DBConnection.getConnection();
        } catch (Exception e) {
            AlertUtils.showError("Database Initialization Error", e.getMessage());
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        this.primaryStage.setTitle(APP_TITLE);
        this.primaryStage.setMinWidth(MIN_WIDTH);
        this.primaryStage.setMinHeight(MIN_HEIGHT);

        // Set application icon
        setApplicationIcon();

        // Load login scene
        loadLoginScene();

        // Show login scene
        this.primaryStage.setScene(loginScene);
        this.primaryStage.show();

        // Handle window close event
        this.primaryStage.setOnCloseRequest(event -> {
            handleShutdown();
            event.consume();
        });
    }

    private void setApplicationIcon() {
        try {
            InputStream iconStream = getClass().getResourceAsStream(APP_ICON);
            if (iconStream != null) {
                Image icon = new Image(iconStream);
                this.primaryStage.getIcons().add(icon);
            }
        } catch (Exception e) {
            // Icon is optional, continue without it
        }
    }

    private void loadLoginScene() throws Exception {
        java.net.URL fxmlUrl = getClass().getResource("/fxml/login.fxml");
        if (fxmlUrl == null) {
            throw new Exception("Login FXML file not found: /fxml/login.fxml");
        }
        Parent root = FXMLLoader.load(fxmlUrl);
        loginScene = new Scene(root);
        java.net.URL cssUrl = getClass().getResource("/fxml/styles.css");
        if (cssUrl != null) {
            loginScene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        handleShutdown();
    }

    private void handleShutdown() {
        // Close database connection
        try {
            DBConnection.closeConnection();
        } catch (Exception e) {
            AlertUtils.showError("Shutdown Error", "Failed to close database connection: " + e.getMessage());
        }

        // Exit application
        Platform.exit();
    }

    /**
     * Main method to launch the JavaFX application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
