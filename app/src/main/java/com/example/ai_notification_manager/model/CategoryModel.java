package com.example.ai_notification_manager.model;

import java.io.Serializable;

public class CategoryModel implements Serializable {
    private String id;
    private String name;
    private int count;
    private int colorResId;
    private boolean isEnabled;

    public CategoryModel(String id, String name, int count, int colorResId, boolean isEnabled) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.colorResId = colorResId;
        this.isEnabled = isEnabled;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public int getColorResId() { return colorResId; }
    public void setColorResId(int colorResId) { this.colorResId = colorResId; }

    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }
}
