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

import java.util.List;

public class OverviewWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        NotificationRepository.init(context);
        NotificationRepository repo = NotificationRepository.getInstance();
        List<NotificationModel> list = repo != null ? repo.getAllNotifications() : new java.util.ArrayList<>();

        int total = list.size();
        int unread = 0;
        int critHigh = 0;

        for (NotificationModel n : list) {
            if (!n.isRead()) {
                unread++;
            }
            NotificationPriorityEngine.PriorityResult pResult = NotificationPriorityEngine.evaluate(
                    n.getTitle(), n.getMessage(), n.getPackageName(), n.getCategory()
            );
            if (pResult.getLevel().equals("CRITICAL") || pResult.getLevel().equals("HIGH")) {
                critHigh++;
            }
        }

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_overview);
            views.setTextViewText(R.id.tvWidgetTotal, String.valueOf(total));
            views.setTextViewText(R.id.tvWidgetUnread, String.valueOf(unread));
            views.setTextViewText(R.id.tvWidgetCritHigh, String.valueOf(critHigh));

            Intent intent = new Intent(context, InboxActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            views.setOnClickPendingIntent(R.id.widgetOverviewRoot, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
