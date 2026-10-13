package com.example.ai_notification_manager.model;

import java.io.Serializable;

public class FilterOptions implements Serializable {
    private String category = "All";
    private String priority = "All";
    private String readStatus = "All";
    private String dateRange = "All";
    private String sortBy = "Newest First";

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getReadStatus() { return readStatus; }
    public void setReadStatus(String readStatus) { this.readStatus = readStatus; }

    public String getDateRange() { return dateRange; }
    public void setDateRange(String dateRange) { this.dateRange = dateRange; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public void reset() {
        this.category = "All";
        this.priority = "All";
        this.readStatus = "All";
        this.dateRange = "All";
        this.sortBy = "Newest First";
    }
}
