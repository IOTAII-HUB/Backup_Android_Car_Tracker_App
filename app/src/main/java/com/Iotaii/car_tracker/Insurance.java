package com.Iotaii.car_tracker;

public class Insurance {
    private String title;
    private String expiryDate;

    public Insurance(String title, String expiryDate) {
        this.title = title;
        this.expiryDate = expiryDate;
    }

    public String getTitle() {
        return title;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}


