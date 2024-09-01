package com.Iotaii.car_tracker;

public class KmSummary {
    private String id;
    private int totalDistance;
    private String totalDuration;

    public KmSummary(String id, int totalDistance, String totalDuration) {
        this.id = id;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
    }

    public String getId() {
        return id;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public String getTotalDuration() {
        return totalDuration;
    }
}
