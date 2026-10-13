package com.example.ai_notification_manager.model;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    private String id;
    private String title;
    private String message;
    private String sourceApp;
    private String category; // Important, Study, Career, Personal, Shopping, Other
    private String priority; // High, Normal, Low, Critical, Medium
    private String timestamp;
    private boolean isRead;
    private boolean isImportant;
    private int priorityScore;
    private String classificationReason;
    private String urgency; // Immediate, Soon, When convenient
    private float confidence; // 0.0 - 1.0
    private String classificationSource; // ML_MODEL, RULE_ENGINE, HYBRID, FALLBACK
    private String modelVersion; // ML_v1.0
    private String secondaryLabel;
    private String userCorrectedCategory;
    private String packageName;

    public NotificationModel(String id, String title, String message, String sourceApp, String category, String priority, String timestamp, boolean isRead, boolean isImportant) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.sourceApp = sourceApp;
        this.category = category;
        this.priority = priority;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.isImportant = isImportant;
        this.priorityScore = 50;
        this.classificationReason = "Default classification";
        this.urgency = "When convenient";
        this.confidence = 0.85f;
        this.classificationSource = "RULE_ENGINE";
        this.modelVersion = "Rule_v1.0";
        this.secondaryLabel = "General";
        this.userCorrectedCategory = null;
        this.packageName = "";
    }

    public NotificationModel(String id, String title, String message, String sourceApp, String category, String priority, String timestamp, boolean isRead, boolean isImportant, int priorityScore, String classificationReason, String urgency, float confidence, String classificationSource, String modelVersion) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.sourceApp = sourceApp;
        this.category = category;
        this.priority = priority;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.isImportant = isImportant;
        this.priorityScore = priorityScore;
        this.classificationReason = classificationReason;
        this.urgency = urgency;
        this.confidence = confidence;
        this.classificationSource = classificationSource;
        this.modelVersion = modelVersion;
        this.secondaryLabel = "General";
        this.userCorrectedCategory = null;
        this.packageName = "";
    }

    public NotificationModel(String id, String title, String message, String sourceApp, String category, String priority, String timestamp, boolean isRead, boolean isImportant, int priorityScore, String classificationReason, String urgency, float confidence, String classificationSource, String modelVersion, String secondaryLabel, String userCorrectedCategory, String packageName) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.sourceApp = sourceApp;
        this.category = category;
        this.priority = priority;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.isImportant = isImportant;
        this.priorityScore = priorityScore;
        this.classificationReason = classificationReason;
        this.urgency = urgency;
        this.confidence = confidence;
        this.classificationSource = classificationSource;
        this.modelVersion = modelVersion;
        this.secondaryLabel = secondaryLabel;
        this.userCorrectedCategory = userCorrectedCategory;
        this.packageName = packageName;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSourceApp() { return sourceApp; }
    public void setSourceApp(String sourceApp) { this.sourceApp = sourceApp; }

    public String getCategory() { 
        return userCorrectedCategory != null ? userCorrectedCategory : category; 
    }
    public void setCategory(String category) { this.category = category; }

    public String getOriginalCategory() { return category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public boolean isImportant() { return isImportant; }
    public void setImportant(boolean important) { this.isImportant = important; }

    public int getPriorityScore() { return priorityScore; }
    public void setPriorityScore(int priorityScore) { this.priorityScore = priorityScore; }

    public String getClassificationReason() { return classificationReason; }
    public void setClassificationReason(String classificationReason) { this.classificationReason = classificationReason; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public float getConfidence() { return confidence; }
    public void setConfidence(float confidence) { this.confidence = confidence; }

    public String getClassificationSource() { return classificationSource; }
    public void setClassificationSource(String classificationSource) { this.classificationSource = classificationSource; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public String getSecondaryLabel() { return secondaryLabel; }
    public void setSecondaryLabel(String secondaryLabel) { this.secondaryLabel = secondaryLabel; }

    public String getUserCorrectedCategory() { return userCorrectedCategory; }
    public void setUserCorrectedCategory(String userCorrectedCategory) { this.userCorrectedCategory = userCorrectedCategory; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
}
