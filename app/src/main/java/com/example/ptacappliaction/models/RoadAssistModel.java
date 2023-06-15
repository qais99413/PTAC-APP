package com.example.ptacappliaction.models;

public class RoadAssistModel {
    private String id;
    private String userId;
    private String userName;
    private String phone;
    private String carModel;
    private String damageType;
    private double latitude;
    private double longitude;

    public RoadAssistModel() {}

    public RoadAssistModel(String id, String userId, String userName, String phone,
                           String carModel, String damageType, double latitude, double longitude) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.phone = phone;
        this.carModel = carModel;
        this.damageType = damageType;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getCarModel() {
        return carModel;
    }

    public String getDamageType() {
        return damageType;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
