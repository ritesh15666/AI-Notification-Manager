package com.example.ai_notification_manager.service;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.ai_notification_manager.classifier.NotificationClassifier;
import com.example.ai_notification_manager.model.NotificationModel;
import com.example.ai_notification_manager.repository.NotificationRepository;
import com.example.ai_notification_manager.utils.AppIconResolver;
import com.example.ai_notification_manager.utils.AppPreferences;
import com.example.ai_notification_manager.utils.CustomNotificationPublisher;
import com.example.ai_notification_manager.utils.NotificationContentExtractor;
import com.example.ai_notification_manager.widget.ImportantWidgetProvider;
import com.example.ai_notification_manager.widget.OverviewWidgetProvider;

public class NotificationCaptureService extends NotificationListenerService {

    private static final String TAG = "NotificationCapture";

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationRepository.init(getApplicationContext());
        Log.d(TAG, "NotificationCaptureService created and initialized.");
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG, "NotificationListenerService successfully connected to system notification shade.");
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.d(TAG, "NotificationListenerService disconnected from system notification shade.");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null) return;

        NotificationRepository.init(getApplicationContext());

        // Avoid processing notifications posted by our own app or our own custom publisher tag
        if (getPackageName().equals(sbn.getPackageName())) {
            return;
        }
        if (sbn.getTag() != null && sbn.getTag().equals("AI_CUSTOM_PUB")) {
            return;
        }

        NotificationContentExtractor.ExtractedData data = NotificationContentExtractor.extract(sbn);
        String title = data.getTitle();
        String message = data.getMessage();
        String packageName = sbn.getPackageName();
        String key = sbn.getKey();

        Log.d(TAG, "Captured notification from package: " + packageName + ", title: " + title);

        // Store original post time as epoch milliseconds string
        long postTime = sbn.getPostTime();
        if (postTime <= 0) {
            postTime = System.currentTimeMillis();
        }
        String timeStr = String.valueOf(postTime);

        // Run hybrid classifier
        NotificationClassifier.ClassificationResult result = NotificationClassifier.getInstance().classify(title, message, packageName);

        String category = result.getCategory();
        String priority = result.getPriority();
        int score = result.getPriorityScore();
        String reason = result.getReason();
        boolean isImportant = category.equalsIgnoreCase("Important") || priority.equalsIgnoreCase("Critical") || priority.equalsIgnoreCase("High");

        // Check if category is enabled by user preference
        AppPreferences prefs = new AppPreferences(this);
        if (!prefs.isCategoryEnabled(category)) {
            Log.d(TAG, "Category " + category + " is disabled in preferences. Skipping custom alert.");
            return;
        }

        String sourceApp = AppIconResolver.getAppLabel(this, packageName, packageName).toString();

        NotificationModel model = new NotificationModel(
                key,
                title,
                message,
                sourceApp,
                category,
                priority,
                timeStr,
                false,
                isImportant,
                score,
                reason,
                result.getUrgency(),
                result.getConfidence(),
                result.getClassificationSource(),
                result.getModelVersion(),
                "General",
                null,
                packageName
        );

        NotificationRepository.getInstance().addCapturedNotification(model);
        Log.d(TAG, "Notification successfully stored in local database under category: " + category + ", sourceApp: " + sourceApp);

        // Publish custom categorized notification for lock screen and notification shade
        CustomNotificationPublisher.publishCustomNotification(this, category, title, message, sourceApp);

        // Trigger widget updates
        try {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] overviewIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, OverviewWidgetProvider.class));
            if (overviewIds.length > 0) {
                new OverviewWidgetProvider().onUpdate(this, appWidgetManager, overviewIds);
            }
            int[] importantIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ImportantWidgetProvider.class));
            if (importantIds.length > 0) {
                new ImportantWidgetProvider().onUpdate(this, appWidgetManager, importantIds);
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // We retain history in inbox when dismissed from system shade, per requirements.
    }
}
