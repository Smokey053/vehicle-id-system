package com.plateiq.utils;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;

// Manages scene transitions between FXML views.
public class SceneNavigator {

    // Switches to a new scene based on the provided FXML path.
    public static void switchScene(ActionEvent event, String fxmlPath) {
        try {
            Stage stage = resolveStage(event);
            if (stage == null) {
                AlertUtils.showError(
                    "Scene Load Error",
                    "Unable to resolve application window for navigation."
                );
                return;
            }

            java.net.URL resource = SceneNavigator.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("FXML not found: " + fxmlPath);
            }

            Parent root = FXMLLoader.load(resource);

            // Create a new scene with the loaded FXML.
            Scene newScene = new Scene(root);
            java.net.URL cssUrl = SceneNavigator.class.getResource(
                "/fxml/styles.css"
            );
            if (cssUrl != null) {
                newScene.getStylesheets().add(cssUrl.toExternalForm());
            }

            // Set the new scene on the stage.
            stage.setScene(newScene);
            stage.show();
        } catch (IOException e) {
            AlertUtils.showError(
                "Scene Load Error",
                "Failed to load FXML: " + fxmlPath
            );
        }
    }

    private static Stage resolveStage(ActionEvent event) {
        Object source = event.getSource();

        if (source instanceof Node node) {
            Window window =
                node.getScene() == null ? null : node.getScene().getWindow();
            if (window instanceof Stage stage) {
                return stage;
            }
        }

        if (source instanceof MenuItem menuItem) {
            PopupWindow popupWindow = menuItem.getParentPopup();
            if (popupWindow != null) {
                Window ownerWindow = popupWindow.getOwnerWindow();
                if (ownerWindow instanceof Stage stage) {
                    return stage;
                }
            }
        }

        return null;
    }
}
