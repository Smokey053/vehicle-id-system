package com.plateiq.model;

public class WorkshopUser extends User {

    public WorkshopUser() {
        super();
    }

    public WorkshopUser(int id, String username, String password, String role, String status) {
        super(id, username, password, role, status);
    }

    @Override
    public void login() {
        System.out.println("Workshop user " + username + " logged in successfully.");
    }

    @Override
    public void logout() {
        System.out.println("Workshop user " + username + " logged out successfully.");
    }

    @Override
    public String displayDashboard() {
        return "/fxml/dashboard.fxml";
    }
}
