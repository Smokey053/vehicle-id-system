package com.plateiq.model;

public class PoliceUser extends User {

    public PoliceUser() {
        super();
    }

    public PoliceUser(int id, String username, String password, String role, String status) {
        super(id, username, password, role, status);
    }

    @Override
    public void login() {
        System.out.println("Police user " + username + " logged in successfully.");
    }

    @Override
    public void logout() {
        System.out.println("Police user " + username + " logged out successfully.");
    }

    @Override
    public String displayDashboard() {
        return "/fxml/dashboard.fxml";
    }
}
