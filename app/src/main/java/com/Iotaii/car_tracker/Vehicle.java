package com.Iotaii.car_tracker;

public class Vehicle {
    private int vehicleId;
    private int distance;
    private String runningTime;
    private String stopTime;
    private String idleTime;
    private String halts;

    private int fuelUsed;
    private String location;
    // Constructor, getters and setters
    public Vehicle(int vehicleId, int distance, String runningTime, String stopTime, String idleTime, String halts,int fuelUsed,String location) {
        this.vehicleId = vehicleId;
        this.distance = distance;
        this.runningTime = runningTime;
        this.stopTime = stopTime;
        this.idleTime = idleTime;
        this.halts = halts;
        this.fuelUsed=fuelUsed;
        this.location=location;
    }
    public Vehicle(){

    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }


    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(String idleTime) {
        this.idleTime = idleTime;
    }

    public String getHalts() {
        return halts;
    }

    public void setHalts(String halts) {
        this.halts = halts;
    }

    public void setFuelUsed(int fuelUsed){
        this.fuelUsed=fuelUsed;
    }
    public int getFuelUsed(){
        return fuelUsed;
    }
    public void setLocation(String location){
        this.location=location;
    }
    public String getLocation(){
        return location;
    }
}
