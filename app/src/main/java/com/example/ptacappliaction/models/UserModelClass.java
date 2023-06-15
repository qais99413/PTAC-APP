package com.example.ptacappliaction.models;

public class UserModelClass {
    private String id;
    private String userName;
    private String phone;
    private String email;
    private String password;

    public UserModelClass() {}

    public UserModelClass(String id, String userName, String phone, String email, String password) {
        this.id = id;
        this.userName = userName;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}