package com.example.ai_notification_manager.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.ai_notification_manager.InboxActivity;

public class IncomingAlertHelper {

    private static final String CHANNEL_ID = "ai_incoming_alerts_channel";

    public static void postSummaryAlert(Context context, String category, String title, String message, String sourceApp) {
        AppPreferences prefs = new AppPreferences(context);
        if (!prefs.isIncomingAlertsEnabled()) {
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Smart Category Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Summaries of categorized incoming notifications.");
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, InboxActivity.class);
        intent.putExtra("selected_category", category);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                category.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String contentTitle = "[" + category + "] " + sourceApp + ": " + title;
        String contentText = message != null && !message.isEmpty() ? message : "New notification received";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        int notificationId = (int) System.currentTimeMillis();
        try {
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException ignored) {
            // Permission not granted on Android 13+
        }
    }
}
