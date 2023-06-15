package com.example.ptacappliaction.models;

public class BookMaintenanceModel {
    private String id;
    private String userId;
    private String userName;
    private String phone;
    private String date;
    private String time;
    private String serviceType;
    private String branch;

    public BookMaintenanceModel() {}

    public BookMaintenanceModel(String id, String userId, String userName, String phone,
                                String date, String time, String serviceType, String branch) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.phone = phone;
        this.date = date;
        this.time = time;
        this.serviceType = serviceType;
        this.branch = branch;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhone() {
        return phone;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getBranch() {
        return branch;
    }
}
