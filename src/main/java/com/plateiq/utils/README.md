# Utils Package - Quick Reference

This package contains utility classes for the PlateIQ Vehicle Management System.

## Available Utilities

### Core Utilities

1. **AccessControl.java** - Role-based access control for user permissions
2. **AlertUtils.java** - Standardized alert dialogs (error, info, warning, confirmation)
3. **ReportExporter.java** - Export functionality for reports (Excel, PDF, etc.)
4. **SceneNavigator.java** - Navigation between application scenes
5. **SessionManager.java** - User session management

### New Utilities (Latest)

6. **AccessibilityHelper.java** ⭐ - Comprehensive accessibility features
7. **StateManager.java** ⭐ - TableView state management (loading, empty, error)

---

## Quick Start Guide

### AccessibilityHelper

**Purpose:** Make your application accessible to all users, including those using keyboard navigation and screen readers.

**Quick Example:**
```java
@Override
public void initialize(URL location, ResourceBundle resources) {
    // Setup form navigation
    AccessibilityHelper.setupFormNavigation(field1, field2, button);
    
    // Enable table keyboard navigation
    AccessibilityHelper.setupTableKeyboard(myTable, () -> handleRowActivation());
    
    // Set initial focus
    AccessibilityHelper.setInitialFocus(field1);
}
```

**Key Methods:**
- `setupFormNavigation(Control... controls)` - Tab navigation setup
- `setupTableKeyboard(TableView, Runnable)` - Enter key on tables
- `setupButtonKeyboard(Button)` - Space/Enter on buttons
- `addFocusIndicator(Node)` - Visual focus highlight
- `setInitialFocus(Node)` - Set starting focus
- `announceToScreenReader(String)` - Screen reader announcements

---

### StateManager

**Purpose:** Display professional loading, empty, and error states in TableView components.

**Quick Example:**
```java
private void loadData() {
    // Show loading
    StateManager.showLoadingState(myTable, true);
    
    // Load data
    new Thread(() -> {
        try {
            List<Data> data = service.getData();
            Platform.runLater(() -> {
                StateManager.showLoadingState(myTable, false);
                if (data.isEmpty()) {
                    StateManager.showEmptyState(myTable, "No data available");
                } else {
                    myTable.setItems(FXCollections.observableArrayList(data));
                }
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                StateManager.showErrorState(myTable, e.getMessage(), () -> loadData());
            });
        }
    }).start();
}
```

**Key Methods:**
- `showLoadingState(TableView, boolean)` - Loading indicator
- `showEmptyState(TableView, String)` - Empty state message
- `showErrorState(TableView, String, Runnable)` - Error with retry
- `showNoResultsState(TableView, String)` - Search no results
- `showInfoState(TableView, String)` - Informational message
- `clearState(TableView)` - Remove state placeholder

---

## Integration Template

Here's a complete template for integrating both utilities in a controller:

```java
package com.plateiq.controller;

import com.plateiq.utils.AccessibilityHelper;
import com.plateiq.utils.StateManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MyController implements Initializable {

    @FXML private TableView<MyModel> dataTable;
    @FXML private TextField searchField;
    @FXML private Button searchButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupAccessibility();
        loadData();
    }

    private void setupAccessibility() {
        // Form navigation
        AccessibilityHelper.setupFormNavigation(searchField, searchButton);
        
        // Table keyboard support
        AccessibilityHelper.setupTableKeyboard(dataTable, this::handleRowActivation);
        
        // Focus indicators
        AccessibilityHelper.addFocusIndicator(searchField);
        AccessibilityHelper.addFocusIndicator(dataTable);
        
        // Initial focus
        AccessibilityHelper.setInitialFocus(searchField);
    }

    private void loadData() {
        StateManager.showLoadingState(dataTable, true);
        
        // Load data asynchronously
        new Thread(() -> {
            try {
                // Simulate data loading
                Thread.sleep(1000);
                var data = fetchData();
                
                javafx.application.Platform.runLater(() -> {
                    StateManager.showLoadingState(dataTable, false);
                    
                    if (data.isEmpty()) {
                        StateManager.showEmptyState(dataTable, "No data available");
                    } else {
                        dataTable.setItems(javafx.collections.FXCollections.observableArrayList(data));
                        AccessibilityHelper.announceToScreenReader(data.size() + " items loaded");
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    StateManager.showErrorState(dataTable, "Failed to load data", this::loadData);
                    AccessibilityHelper.announceToScreenReader("Error loading data");
                });
            }
        }).start();
    }

    private void handleRowActivation() {
        var selected = dataTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Handle the selected item
            System.out.println("Activated: " + selected);
        }
    }

    private java.util.List<MyModel> fetchData() {
        // Your data fetching logic here
        return new java.util.ArrayList<>();
    }
}
```

---

## Best Practices

### ✅ DO:
- Always setup form navigation for better keyboard accessibility
- Show loading states during async operations
- Provide retry actions for error states
- Announce important changes to screen readers
- Set initial focus for keyboard users
- Use appropriate state for each scenario (loading/empty/error)

### ❌ DON'T:
- Don't forget to clear loading state after data loads
- Don't show error states without retry options (when applicable)
- Don't skip accessibility setup for critical forms
- Don't use generic messages - be specific and helpful

---

## Documentation

For complete documentation with detailed examples, see:
- **[UTILITIES_GUIDE.md](../../../UTILITIES_GUIDE.md)** - Comprehensive guide with all methods and examples

---

## Testing Accessibility

### Keyboard Navigation Test Checklist:
- [ ] Can you navigate the entire form using only Tab/Shift+Tab?
- [ ] Can you activate buttons using Enter and Space keys?
- [ ] Can you navigate and activate table rows with keyboard?
- [ ] Is there a visible focus indicator on all interactive elements?
- [ ] Does focus start in a logical place when the screen loads?

### State Management Test Checklist:
- [ ] Does the loading indicator appear during data loading?
- [ ] Is the empty state shown when no data is available?
- [ ] Is the error state shown when operations fail?
- [ ] Does the retry button work in error states?
- [ ] Are state transitions smooth (fade-in animations)?

---

## Support

For questions or issues:
1. Check the comprehensive guide: `UTILITIES_GUIDE.md`
2. Review existing controller implementations
3. Contact the PlateIQ Development Team

---

## Version History

### v1.0 (Current)
- ✨ New: AccessibilityHelper utility
- ✨ New: StateManager utility
- 📝 Complete documentation and examples
- ✅ Production-ready with comprehensive JavaDoc

---

**Happy coding! 🚀**
