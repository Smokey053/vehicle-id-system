package com.plateiq.utils;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

 
public class AccessibilityHelper {

    // CSS class names for focus indicators
    private static final String FOCUS_INDICATOR_CLASS = "focus-indicator";
    private static final String FOCUS_VISIBLE_CLASS = "focus-visible";

    // ARIA live region for screen reader announcements
    private static Label screenReaderAnnouncer;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AccessibilityHelper() {
        throw new UnsupportedOperationException(
            "Utility class cannot be instantiated"
        );
    }

    /**
     * Sets up Tab key navigation with proper focus traversal for form controls.
     * Configures the controls to traverse focus in the order they are provided,
     * with Tab moving forward and Shift+Tab moving backward.
     * @param controls Variable number of controls to set up in navigation order.
     *                 Controls will be navigable in the order they are provided.
     * @throws IllegalArgumentException if controls array is null or empty
     */

    public static void setupFormNavigation(Control... controls) {
        if (controls == null || controls.length == 0) {
            throw new IllegalArgumentException(
                "Controls array cannot be null or empty"
            );
        }

        for (int i = 0; i < controls.length; i++) {
            Control current = controls[i];
            if (current == null) {
                continue;
            }

            final int currentIndex = i;

            // Set up Tab key to move forward
            current.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.TAB && !event.isShiftDown()) {
                    if (currentIndex < controls.length - 1) {
                        event.consume();
                        Control next = controls[currentIndex + 1];
                        if (next != null && !next.isDisabled()) {
                            next.requestFocus();
                        }
                    }
                }
                // Set up Shift+Tab to move backward
                else if (
                    event.getCode() == KeyCode.TAB && event.isShiftDown()
                ) {
                    if (currentIndex > 0) {
                        event.consume();
                        Control previous = controls[currentIndex - 1];
                        if (previous != null && !previous.isDisabled()) {
                            previous.requestFocus();
                        }
                    }
                }
            });

            // Enable focus traversal
            current.setFocusTraversable(true);
        }
    }

    /*Enables keyboard navigation for TableView with Enter key activation.*/
    public static void setupTableKeyboard(
        TableView<?> table,
        Runnable onEnterAction
    ) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        if (onEnterAction == null) {
            throw new IllegalArgumentException("OnEnterAction cannot be null");
        }

        table.setFocusTraversable(true);

        // Handle Enter key press
        table.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (table.getSelectionModel().getSelectedItem() != null) {
                    event.consume();
                    onEnterAction.run();
                }
            }
            // Handle Space key as alternative activation
            else if (event.getCode() == KeyCode.SPACE) {
                if (table.getSelectionModel().getSelectedItem() != null) {
                    event.consume();
                    onEnterAction.run();
                }
            }
        });

        // Ensure proper accessible text for screen readers
        // Note: JavaFX 17 may not have TABLE role, using default NODE
        table.setAccessibleText(
            "Data table - use arrow keys to navigate, Enter to activate"
        );
    }

    /*@throws IllegalArgumentException if button is null*/
    public static void setupButtonKeyboard(Button button) {
        if (button == null) {
            throw new IllegalArgumentException("Button cannot be null");
        }

        button.setFocusTraversable(true);
        button.setMnemonicParsing(true);

        // Add Enter key handler if not already present
        button.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER && !button.isDisabled()) {
                event.consume();
                button.fire();
            }
        });

        // Ensure Space key works (usually default, but explicitly set)
        button.setDefaultButton(false);
        button.setCancelButton(false);
    }

    /*Adds visual focus indicator styles to enhance visibility of focused elements.
     */
    public static void addFocusIndicator(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }

        // Add CSS class for styling
        node.getStyleClass().add(FOCUS_INDICATOR_CLASS);

        // Show visual indicator on focus
        node
            .focusedProperty()
            .addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    // Focused
                    if (!node.getStyleClass().contains(FOCUS_VISIBLE_CLASS)) {
                        node.getStyleClass().add(FOCUS_VISIBLE_CLASS);
                    }
                    // Add inline style for immediate visual feedback
                    if (node instanceof Region) {
                        Region region = (Region) node;
                        String currentStyle = region.getStyle();
                        if (!currentStyle.contains("-fx-border-color")) {
                            region.setStyle(
                                currentStyle +
                                    "; -fx-border-color: #007bff; -fx-border-width: 2px; " +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0, 123, 255, 0.5), 4, 0, 0, 0);"
                            );
                        }
                    }
                } else {
                    // Lost focus
                    node.getStyleClass().remove(FOCUS_VISIBLE_CLASS);
                    // Remove inline style
                    if (node instanceof Region) {
                        Region region = (Region) node;
                        String currentStyle = region.getStyle();
                        currentStyle = currentStyle.replaceAll(
                            "; -fx-border-color: #007bff; -fx-border-width: 2px; " +
                                "-fx-effect: dropshadow\\(three-pass-box, rgba\\(0, 123, 255, 0\\.5\\), 4, 0, 0, 0\\);",
                            ""
                        );
                        region.setStyle(currentStyle);
                    }
                }
            });

        // Ensure the node is focusable
        node.setFocusTraversable(true);
    }

    /**
     Sets initial focus to a node when the scene is loaded.
      This is important for keyboard-only users who need to start navigating*/

    public static void setInitialFocus(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }

        // Use Platform.runLater to ensure the scene is fully loaded
        Platform.runLater(() -> {
            if (node.getScene() != null && !node.isDisabled()) {
                node.requestFocus();
            }
        });
    }

    /*Announces a message to screen readers using ARIA live regions.*/

    public static void announceToScreenReader(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Message cannot be null or empty"
            );
        }

        Platform.runLater(() -> {
            // Create announcer if it doesn't exist
            if (screenReaderAnnouncer == null) {
                screenReaderAnnouncer = new Label();
                screenReaderAnnouncer.setVisible(false);
                screenReaderAnnouncer.setManaged(false);
                // Set ARIA live region properties
                screenReaderAnnouncer.setAccessibleRole(
                    javafx.scene.AccessibleRole.TEXT
                );
                screenReaderAnnouncer
                    .getProperties()
                    .put("aria-live", "polite");
                screenReaderAnnouncer
                    .getProperties()
                    .put("aria-atomic", "true");
            }

            // Update the text to trigger screen reader announcement
            screenReaderAnnouncer.setText(message);
            screenReaderAnnouncer.setAccessibleText(message);

            // Clear the message after a short delay
            Platform.runLater(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (screenReaderAnnouncer != null) {
                    screenReaderAnnouncer.setText("");
                }
            });
        });
    }

    /*Sets up comprehensive accessibility for a button with custom accessible text.*/

    public static void setupAccessibleButton(
        Button button,
        String accessibleText
    ) {
        if (button == null) {
            throw new IllegalArgumentException("Button cannot be null");
        }
        if (accessibleText == null || accessibleText.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Accessible text cannot be null or empty"
            );
        }

        setupButtonKeyboard(button);
        addFocusIndicator(button);
        button.setAccessibleText(accessibleText);
        button.setAccessibleRole(javafx.scene.AccessibleRole.BUTTON);
    }

    /*Sets up comprehensive accessibility for a table with custom accessible text.*/

    public static void setupAccessibleTable(
        TableView<?> table,
        String accessibleText,
        Runnable onEnterAction
    ) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        if (accessibleText == null || accessibleText.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Accessible text cannot be null or empty"
            );
        }
        if (onEnterAction == null) {
            throw new IllegalArgumentException("OnEnterAction cannot be null");
        }

        setupTableKeyboard(table, onEnterAction);
        addFocusIndicator(table);
        table.setAccessibleText(accessibleText);
    }

    /*Disables a control and updates its accessible state for screen readers.*/

    public static void disableWithAccessibility(
        Control control,
        String reason
    ) {
        if (control == null) {
            throw new IllegalArgumentException("Control cannot be null");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Reason cannot be null or empty"
            );
        }

        control.setDisable(true);
        String originalText = control.getAccessibleText();
        String newText =
            (originalText != null ? originalText + " - " : "") +
            "Disabled: " +
            reason;
        control.setAccessibleText(newText);
    }

    /**
     * Enables a control and updates its accessible state for screen readers.*/

    public static void enableWithAccessibility(
        Control control,
        String accessibleText
    ) {
        if (control == null) {
            throw new IllegalArgumentException("Control cannot be null");
        }

        control.setDisable(false);
        if (accessibleText != null && !accessibleText.trim().isEmpty()) {
            control.setAccessibleText(accessibleText);
        }
    }
}
