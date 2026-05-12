package com.plateiq.model;

public class InsuranceUser extends User {

    public InsuranceUser() {
        super();
    }

    public InsuranceUser(int id, String username, String password, String role, String status) {
        super(id, username, password, role, status);
    }

    @Override
    public void login() {
        System.out.println("Insurance user " + username + " logged in successfully.");
    }

    @Override
    public void logout() {
        System.out.println("Insurance user " + username + " logged out successfully.");
    }

    @Override
    public String displayDashboard() {
        return "/fxml/dashboard.fxml";
    }
}
