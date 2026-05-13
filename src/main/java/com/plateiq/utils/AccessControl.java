package com.plateiq.utils;

import com.plateiq.model.User;
import java.io.IOException;
import java.net.URL;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class AccessControl {

    public enum Module {
        VEHICLE,
        SERVICE,
        INSURANCE,
        POLICE,
        CUSTOMER,
    }

    private static final String GLOBAL_STYLESHEET = "/fxml/styles.css";

    private AccessControl() {}

    public static boolean canAccess(User user, Module module) {
        if (user == null || user.getRole() == null) {
            return false;
        }

        String role = user.getRole().trim().toUpperCase(Locale.ROOT);
        return switch (role) {
            case "ADMIN" -> true;
            case "WORKSHOP" -> module == Module.VEHICLE ||
            module == Module.SERVICE;
            case "INSURANCE" -> module == Module.INSURANCE ||
            module == Module.VEHICLE;
            case "POLICE" -> module == Module.POLICE ||
            module == Module.VEHICLE;
            case "CUSTOMER" -> module == Module.CUSTOMER ||
            module == Module.VEHICLE;
            default -> false;
        };
    }

    public static Set<Module> allowedModules(User user) {
        if (user == null || user.getRole() == null) {
            return EnumSet.noneOf(Module.class);
        }

        String role = user.getRole().trim().toUpperCase(Locale.ROOT);
        return switch (role) {
            case "ADMIN" -> EnumSet.allOf(Module.class);
            case "WORKSHOP" -> EnumSet.of(Module.VEHICLE, Module.SERVICE);
            case "INSURANCE" -> EnumSet.of(Module.INSURANCE, Module.VEHICLE);
            case "POLICE" -> EnumSet.of(Module.POLICE, Module.VEHICLE);
            case "CUSTOMER" -> EnumSet.of(Module.CUSTOMER, Module.VEHICLE);
            default -> EnumSet.noneOf(Module.class);
        };
    }

    public static boolean hasRole(User user, String role) {
        return (
            user != null &&
            user.getRole() != null &&
            user.getRole().trim().equalsIgnoreCase(role)
        );
    }

    public static boolean isAdmin(User user) {
        return hasRole(user, "ADMIN");
    }

    public static boolean canManageVehicles(User user) {
        return hasRole(user, "WORKSHOP") || isAdmin(user);
    }

    public static boolean canViewVehicles(User user) {
        return (
            hasRole(user, "WORKSHOP") ||
            hasRole(user, "INSURANCE") ||
            hasRole(user, "POLICE") ||
            hasRole(user, "CUSTOMER") ||
            isAdmin(user)
        );
    }

    public static boolean canManageServices(User user) {
        return hasRole(user, "WORKSHOP") || isAdmin(user);
    }

    public static boolean canManageInsurance(User user) {
        return hasRole(user, "INSURANCE") || isAdmin(user);
    }

    public static boolean canManagePolice(User user) {
        return hasRole(user, "POLICE") || isAdmin(user);
    }

    public static boolean canManageCustomerActions(User user) {
        return hasRole(user, "CUSTOMER") || isAdmin(user);
    }

    public static boolean enforceOrRedirect(Node currentNode, Module module) {
        User currentUser = SessionManager.getCurrentUser();
        if (canAccess(currentUser, module)) {
            return true;
        }

        AlertUtils.showWarning(
            "Access Denied",
            "You do not have permission to access this module."
        );
        redirectTo(currentNode, "/fxml/dashboard.fxml");
        return false;
    }

    public static void redirectTo(Node currentNode, String fxmlPath) {
        if (currentNode == null || currentNode.getScene() == null) {
            return;
        }
        Stage stage = (Stage) currentNode.getScene().getWindow();
        if (stage == null) {
            return;
        }

        try {
            URL resource = SceneNavigator.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("FXML not found: " + fxmlPath);
            }

            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root);
            URL cssResource = SceneNavigator.class.getResource(
                GLOBAL_STYLESHEET
            );
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            AlertUtils.showError(
                "Scene Load Error",
                "Failed to load FXML: " + fxmlPath
            );
        }
    }
}
