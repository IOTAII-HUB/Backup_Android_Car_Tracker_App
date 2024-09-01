package com.Iotaii.car_tracker;

public class Item {
    private String title;
    private String description;
    private int imageResId;
    private String additionalText;

    public Item(String title, String description, int imageResId, String additionalText) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.additionalText = this.additionalText;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getAdditionalText() {
        return additionalText;
    }
}