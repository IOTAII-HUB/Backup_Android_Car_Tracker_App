package com.Iotaii.car_tracker;

// ItemModel.java
public class ItemModel {
    private String timestamp;
    private String amount;
    private String location1;
    private String location2;

    public ItemModel(String timestamp, String amount, String location1, String location2) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.location1 = location1;
        this.location2 = location2;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAmount() {
        return amount;
    }

    public String getLocation1() {
        return location1;
    }

    public String getLocation2() {
        return location2;
    }
}

