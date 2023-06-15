package com.example.ptacappliaction.models;

public class SchMileageModel {
    private String id;
    private String date;
    private String km;
    private String nextService;
    private String oilType;

    public SchMileageModel() {}

    public SchMileageModel(String id, String date, String km, String nextService, String oilType) {
        this.id = id;
        this.date = date;
        this.km = km;
        this.nextService = nextService;
        this.oilType = oilType;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getKm() {
        return km;
    }

    public String getNextService() {
        return nextService;
    }

    public String getOilType() {
        return oilType;
    }
}
