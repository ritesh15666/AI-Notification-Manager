package com.example.ai_notification_manager.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimestampUtil {

    public static String format(String rawTimestamp) {
        if (rawTimestamp == null || rawTimestamp.isEmpty()) {
            return "Just now";
        }
        try {
            long timeMillis = Long.parseLong(rawTimestamp);
            return format(timeMillis);
        } catch (NumberFormatException e) {
            // Fallback for legacy string representation
            return rawTimestamp;
        }
    }

    public static String format(long timeMillis) {
        if (timeMillis <= 0) {
            return "Just now";
        }
        long diff = System.currentTimeMillis() - timeMillis;
        if (diff < 0) {
            return "Just now";
        }
        if (diff < 60000) {
            return "Just now";
        } else if (diff < 3600000) {
            long mins = diff / 60000;
            return mins + (mins == 1 ? " min ago" : " mins ago");
        } else if (diff < 86400000) {
            long hrs = diff / 3600000;
            return hrs + (hrs == 1 ? " hr ago" : " hrs ago");
        } else if (diff < 172800000) {
            return "Yesterday";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());
            return sdf.format(new Date(timeMillis));
        }
    }
}
