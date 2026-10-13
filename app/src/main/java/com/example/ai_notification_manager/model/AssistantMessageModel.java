package com.example.ai_notification_manager.model;

import java.io.Serializable;
import java.util.List;

public class AssistantMessageModel implements Serializable {
    private final String messageText;
    private final boolean isUser;
    private final List<NotificationModel> notifications;

    public AssistantMessageModel(String messageText, boolean isUser, List<NotificationModel> notifications) {
        this.messageText = messageText;
        this.isUser = isUser;
        this.notifications = notifications;
    }

    public String getMessageText() { return messageText; }
    public boolean isUser() { return isUser; }
    public List<NotificationModel> getNotifications() { return notifications; }
}
