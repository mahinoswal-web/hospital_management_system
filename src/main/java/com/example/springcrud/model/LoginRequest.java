package com.example.springcrud.model;

public class LoginRequest {

    private String role;
    private String phone;
    private String password;

    public LoginRequest() {}

    public LoginRequest(String role, String phone, String password) {
        this.role = role;
        this.phone = phone;
        this.password = password;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
