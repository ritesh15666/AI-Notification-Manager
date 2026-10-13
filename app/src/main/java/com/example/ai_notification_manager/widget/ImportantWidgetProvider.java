package com.example.ai_notification_manager.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.ai_notification_manager.InboxActivity;
import com.example.ai_notification_manager.R;
import com.example.ai_notification_manager.classifier.NotificationPriorityEngine;
import com.example.ai_notification_manager.model.NotificationModel;
import com.example.ai_notification_manager.repository.NotificationRepository;
import com.example.ai_notification_manager.utils.AppIconResolver;

import java.util.List;

public class ImportantWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        NotificationRepository.init(context);
        NotificationRepository repo = NotificationRepository.getInstance();
        List<NotificationModel> list = repo != null ? repo.getAllNotifications() : new java.util.ArrayList<>();

        NotificationModel latestImportant = null;
        int importantCount = 0;

        for (NotificationModel n : list) {
            NotificationPriorityEngine.PriorityResult pResult = NotificationPriorityEngine.evaluate(
                    n.getTitle(), n.getMessage(), n.getPackageName(), n.getCategory()
            );
            if (pResult.getLevel().equals("CRITICAL") || pResult.getLevel().equals("HIGH") || n.isImportant()) {
                importantCount++;
                if (latestImportant == null) {
                    latestImportant = n;
                }
            }
        }

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_important);

            views.setTextViewText(R.id.tvWidgetCount, importantCount + " important");

            if (latestImportant != null) {
                CharSequence appLabel = AppIconResolver.getAppLabel(context, latestImportant.getPackageName(), latestImportant.getSourceApp());
                views.setTextViewText(R.id.tvWidgetApp, appLabel);
                views.setTextViewText(R.id.tvWidgetTitle, latestImportant.getTitle());
                views.setTextViewText(R.id.tvWidgetMessage, latestImportant.getMessage());
            } else {
                views.setTextViewText(R.id.tvWidgetApp, "No important notifications");
                views.setTextViewText(R.id.tvWidgetTitle, "Your inbox is clear");
                views.setTextViewText(R.id.tvWidgetMessage, "Tap to open AI Notification Manager");
            }

            Intent intent = new Intent(context, InboxActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            views.setOnClickPendingIntent(R.id.widgetImportantRoot, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
