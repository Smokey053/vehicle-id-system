package com.plateiq.model;

public class AdminUser extends User {

    public AdminUser() {
    }

    public AdminUser(int id, String username, String password, String role, String status) {
        super(id, username, password, role, status);
    }

    @Override
    public void login() {
        System.out.println("Admin " + username + " logged in successfully.");
    }

    @Override
    public void logout() {
        System.out.println("Admin " + username + " logged out successfully.");
    }

    @Override
    public String displayDashboard() {
        return "/fxml/dashboard.fxml";
    }
}