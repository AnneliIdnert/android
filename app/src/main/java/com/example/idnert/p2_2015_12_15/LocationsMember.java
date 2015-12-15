package com.example.idnert.p2_2015_12_15;

public class LocationsMember {
    private String member;
    private double latitude;
    private double longitude;

    public LocationsMember(double latitude, double longitude, String member) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.member = member;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getMember() {
        return member;
    }
}
