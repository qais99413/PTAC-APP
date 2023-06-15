package com.example.ptacappliaction.models;

public class FeedbackModel {
    private String id;
    private String userName;
    private String email;
    private String phone;
    private String feedback;

    public FeedbackModel() {}

    public FeedbackModel(String id, String userName, String email, String phone, String feedback) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.feedback = feedback;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getFeedback() {
        return feedback;
    }
}
