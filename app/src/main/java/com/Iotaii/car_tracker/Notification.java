package com.Iotaii.car_tracker;

public class Notification {
    private String vehicleNumber;
    private String timestamp;
    private String message;
    private String location;
    private boolean isStarted; // true if the vehicle is started, false if stopped

    public Notification(String vehicleNumber, String timestamp, String message, String location, boolean isStarted) {
        this.vehicleNumber = vehicleNumber;
        this.timestamp = timestamp;
        this.message = message;
        this.location = location;
        this.isStarted = isStarted;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getLocation() {
        return location;
    }

    public boolean isStarted() {
        return isStarted;
    }
}
