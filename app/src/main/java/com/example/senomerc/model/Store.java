package com.example.senomerc.model;

public class Store {
    private double latitude, longitude;
    private String name;

    public Store(double latitute, double longitute, String name) {
        this.latitude = latitute;
        this.longitude = longitute;
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}