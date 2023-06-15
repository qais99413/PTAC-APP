package com.example.ptacappliaction.models;

public class OilServiceBookingModel {
    private String id;
    private String title;
    private String price;
    private String duration;
    private String timeConsumed;
    private String warranty;
    private String userId;
    private String userName;
    private String date;

    public OilServiceBookingModel() {}

    public OilServiceBookingModel(String id, String title, String price, String duration, String timeConsumed,
                                  String warranty, String userId, String userName, String date) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.duration = duration;
        this.timeConsumed = timeConsumed;
        this.warranty = warranty;
        this.userId = userId;
        this.userName = userName;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getDuration() {
        return duration;
    }

    public String getTimeConsumed() {
        return timeConsumed;
    }

    public String getWarranty() {
        return warranty;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getDate() {
        return date;
    }
}
