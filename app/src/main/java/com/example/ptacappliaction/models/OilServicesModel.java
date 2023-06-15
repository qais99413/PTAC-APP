package com.example.ptacappliaction.models;

public class OilServicesModel {
    private String id;
    private String title;
    private String price;
    private String duration;
    private String timeConsumed;
    private String warranty;

    public OilServicesModel() {}

    public OilServicesModel(String id, String title, String price,
                            String duration, String timeConsumed, String warranty) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.duration = duration;
        this.timeConsumed = timeConsumed;
        this.warranty = warranty;
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
}
