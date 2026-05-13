package com.plateiq.utils;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/*Provides methods to display loading, empty, and error states with visual feedback.*/
public class StateManager {

    // Default messages
    private static final String DEFAULT_EMPTY_MESSAGE = "No data available";
    private static final String DEFAULT_ERROR_MESSAGE = "An error occurred while loading data";
    private static final String DEFAULT_LOADING_MESSAGE = "Loading...";

    // Default icons (using Unicode characters)
    private static final String EMPTY_ICON = "📭";
    private static final String ERROR_ICON = "⚠️";
    private static final String INFO_ICON = "ℹ️";

    // Styling constants
    private static final String PLACEHOLDER_STYLE =
        "-fx-padding: 40px; -fx-alignment: center;";
    private static final String MESSAGE_FONT_SIZE = "16px";
    private static final String ICON_FONT_SIZE = "48px";
    private static final Color MESSAGE_COLOR = Color.rgb(108, 117, 125);
    private static final Color ERROR_COLOR = Color.rgb(220, 53, 69);

    /*Private constructor to prevent instantiation of this utility class.*/
    private StateManager() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /*Shows or hides a loading state in the table with an animated progress indicator.*/
    
    public static void showLoadingState(TableView<?> table, boolean isLoading) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }

        Platform.runLater(() -> {
            if (isLoading) {
                Label loadingPlaceholder = createLoadingPlaceholder();
                table.setPlaceholder(loadingPlaceholder);

                // Add fade-in animation
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), loadingPlaceholder);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } else {
                // Clear placeholder if it's a loading placeholder
                if (table.getPlaceholder() != null) {
                    String placeholderText = "";
                    if (table.getPlaceholder() instanceof VBox) {
                        VBox vbox = (VBox) table.getPlaceholder();
                        if (!vbox.getChildren().isEmpty() &&
                            vbox.getChildren().get(0) instanceof ProgressIndicator) {
                            table.setPlaceholder(null);
                        }
                    }
                }
            }
        });
    }

    /*Shows a custom empty state message when the table has no data.*/
    public static void showEmptyState(TableView<?> table, String message) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        Platform.runLater(() -> {
            Label emptyPlaceholder = createEmptyStatePlaceholder(message, EMPTY_ICON);
            table.setPlaceholder(emptyPlaceholder);

            // Add fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), emptyPlaceholder);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
    }

    /*Shows an error state with an error message and optional retry button.*/
    
    public static void showErrorState(TableView<?> table, String errorMessage) {
        showErrorState(table, errorMessage, null);
    }

    /*Shows an error state with an error message and a retry button.*/
    
    public static void showErrorState(TableView<?> table, String errorMessage, Runnable retryAction) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Error message cannot be null or empty");
        }

        Platform.runLater(() -> {
            VBox errorPlaceholder = createErrorPlaceholder(errorMessage, retryAction);
            table.setPlaceholder(errorPlaceholder);

            // Add fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), errorPlaceholder);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
    }

    /*Creates a styled empty state placeholder label with custom message and icon.*/
    
    public static Label createEmptyStatePlaceholder(String message, String iconText) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setStyle(PLACEHOLDER_STYLE);

        // Icon label
        Label iconLabel = new Label(iconText != null ? iconText : EMPTY_ICON);
        iconLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.NORMAL, 48));
        iconLabel.setTextFill(MESSAGE_COLOR);

        // Message label
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        messageLabel.setTextFill(MESSAGE_COLOR);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        messageLabel.setTextAlignment(TextAlignment.CENTER);

        container.getChildren().addAll(iconLabel, messageLabel);

        // Create wrapper label for TableView placeholder compatibility
        Label placeholder = new Label();
        placeholder.setGraphic(container);
        placeholder.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);

        // Accessibility
        placeholder.setAccessibleText("Empty state: " + message);

        return placeholder;
    }

    /*Creates an animated loading placeholder with progress indicator.*/
    
    public static Label createLoadingPlaceholder() {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setStyle(PLACEHOLDER_STYLE);

        // Progress indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(50, 50);
        progressIndicator.setStyle("-fx-progress-color: #007bff;");

        // Message label
        Label messageLabel = new Label(DEFAULT_LOADING_MESSAGE);
        messageLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        messageLabel.setTextFill(MESSAGE_COLOR);

        container.getChildren().addAll(progressIndicator, messageLabel);

        // Create wrapper label for TableView placeholder compatibility
        Label placeholder = new Label();
        placeholder.setGraphic(container);
        placeholder.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);

        // Accessibility
        placeholder.setAccessibleText("Loading data, please wait");

        return placeholder;
    }

    /*Creates an error placeholder with message and optional retry button.*/
    
    public static VBox createErrorPlaceholder(String message, Runnable retryAction) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);
        container.setStyle(PLACEHOLDER_STYLE);

        // Error icon label
        Label iconLabel = new Label(ERROR_ICON);
        iconLabel.setFont(Font.font("Segoe UI Emoji", FontWeight.NORMAL, 48));
        iconLabel.setTextFill(ERROR_COLOR);

        // Error message label
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        messageLabel.setTextFill(ERROR_COLOR);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        messageLabel.setTextAlignment(TextAlignment.CENTER);

        container.getChildren().addAll(iconLabel, messageLabel);

        // Add retry button if action is provided
        if (retryAction != null) {
            Button retryButton = new Button("Retry");
            retryButton.setStyle(
                "-fx-background-color: #007bff; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 8 16; " +
                "-fx-font-size: 14px; " +
                "-fx-cursor: hand; " +
                "-fx-background-radius: 4;"
            );

            // Hover effect
            retryButton.setOnMouseEntered(e ->
                retryButton.setStyle(
                    "-fx-background-color: #0056b3; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 8 16; " +
                    "-fx-font-size: 14px; " +
                    "-fx-cursor: hand; " +
                    "-fx-background-radius: 4;"
                )
            );
            retryButton.setOnMouseExited(e ->
                retryButton.setStyle(
                    "-fx-background-color: #007bff; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 8 16; " +
                    "-fx-font-size: 14px; " +
                    "-fx-cursor: hand; " +
                    "-fx-background-radius: 4;"
                )
            );

            retryButton.setOnAction(e -> retryAction.run());

            // Accessibility
            retryButton.setAccessibleText("Retry loading data");
            AccessibilityHelper.setupButtonKeyboard(retryButton);

            container.getChildren().add(retryButton);
        }

        // Accessibility
        container.setAccessibleText("Error: " + message +
            (retryAction != null ? ". Press retry button to try again." : ""));

        return container;
    }

    /*Clears any state placeholder from the table, restoring it to normal display.*/
    
    public static void clearState(TableView<?> table) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }

        Platform.runLater(() -> {
            table.setPlaceholder(null);
        });
    }

    /*Shows a custom state with a custom placeholder node.*/
    
    public static void showCustomState(TableView<?> table, javafx.scene.Node customPlaceholder) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        if (customPlaceholder == null) {
            throw new IllegalArgumentException("Custom placeholder cannot be null");
        }

        Platform.runLater(() -> {
            table.setPlaceholder(customPlaceholder);

            // Add fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), customPlaceholder);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
    }

    /*Shows an info state with an informational message and icon.*/
    
    public static void showInfoState(TableView<?> table, String message) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        Platform.runLater(() -> {
            Label infoPlaceholder = createEmptyStatePlaceholder(message, INFO_ICON);
            table.setPlaceholder(infoPlaceholder);

            // Add fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), infoPlaceholder);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
    }

    /*Shows a "no results" state for search/filter operations.*/
    
    public static void showNoResultsState(TableView<?> table, String searchQuery) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        if (searchQuery == null) {
            searchQuery = "";
        }

        String message = searchQuery.trim().isEmpty()
            ? "No results found"
            : "No results found for \"" + searchQuery + "\"";

        Platform.runLater(() -> {
            Label noResultsPlaceholder = createEmptyStatePlaceholder(message, "🔍");
            table.setPlaceholder(noResultsPlaceholder);

            // Add fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), noResultsPlaceholder);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
    }
}
