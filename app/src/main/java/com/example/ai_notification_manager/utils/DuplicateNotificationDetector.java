package com.example.ai_notification_manager.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DuplicateNotificationDetector {

    private static DuplicateNotificationDetector instance;
    // Map of signature -> timestamp
    private final Map<String, Long> recentNotifications = new HashMap<>();
    private long timeWindowMs = 5 * 60 * 1000L; // 5 minutes default

    private DuplicateNotificationDetector() {}

    public static synchronized DuplicateNotificationDetector getInstance() {
        if (instance == null) {
            instance = new DuplicateNotificationDetector();
        }
        return instance;
    }

    public void setTimeWindowMs(long timeWindowMs) {
        this.timeWindowMs = timeWindowMs;
    }

    public boolean isDuplicate(String packageName, String title, String message) {
        String safePkg = packageName != null ? packageName : "";
        String safeTitle = title != null ? title : "";
        String safeMsg = message != null ? message : "";

        String signature = (safePkg + "|" + safeTitle + "|" + safeMsg).toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
        long now = System.currentTimeMillis();

        cleanOldEntries(now);

        if (recentNotifications.containsKey(signature)) {
            long lastSeen = recentNotifications.get(signature);
            if ((now - lastSeen) < timeWindowMs) {
                return true;
            }
        }

        recentNotifications.put(signature, now);
        return false;
    }

    private void cleanOldEntries(long now) {
        recentNotifications.entrySet().removeIf(entry -> (now - entry.getValue()) > timeWindowMs);
    }
}
