package com.example.ai_notification_manager.repository;

import android.content.Context;

import com.example.ai_notification_manager.R;
import com.example.ai_notification_manager.db.NotificationDatabaseHelper;
import com.example.ai_notification_manager.model.CategoryModel;
import com.example.ai_notification_manager.model.NotificationModel;

import java.util.List;

public class NotificationRepository {
    private static NotificationRepository instance;
    private static Context appContext;
    private final NotificationDatabaseHelper dbHelper;

    public static void init(Context context) {
        if (context != null && appContext == null) {
            appContext = context.getApplicationContext();
        }
    }

    public static Context getAppContext() {
        return appContext;
    }

    private NotificationRepository(Context context) {
        dbHelper = NotificationDatabaseHelper.getInstance(context);
    }

    public static synchronized NotificationRepository getInstance(Context context) {
        init(context);
        if (instance == null) {
            instance = new NotificationRepository(appContext != null ? appContext : context);
        }
        return instance;
    }

    public static synchronized NotificationRepository getInstance() {
        if (instance == null && appContext != null) {
            instance = new NotificationRepository(appContext);
        }
        return instance;
    }

    public void resetToDefault() {
        if (dbHelper != null) {
            dbHelper.resetToDefault();
        }
    }

    public List<NotificationModel> getAllNotifications() {
        if (dbHelper == null) return new java.util.ArrayList<>();
        return dbHelper.getAllNotifications();
    }

    public List<NotificationModel> getNotificationsByCategory(String category) {
        if (dbHelper == null) return new java.util.ArrayList<>();
        return dbHelper.getNotificationsByCategory(category);
    }

    public List<NotificationModel> searchNotifications(String query) {
        if (dbHelper == null) return new java.util.ArrayList<>();
        return dbHelper.searchNotifications(query);
    }

    public int getCountByCategory(String category) {
        if (dbHelper == null) return 0;
        return dbHelper.getCountByCategory(category);
    }

    public int getImportantCount() {
        if (dbHelper == null) return 0;
        return dbHelper.getImportantCount();
    }

    public List<CategoryModel> getCategories() {
        List<CategoryModel> categories = new java.util.ArrayList<>();
        categories.add(new CategoryModel("Important", "Important", getCountByCategory("Important"), R.color.categoryImportant, true));
        categories.add(new CategoryModel("Study", "Study", getCountByCategory("Study"), R.color.categoryStudy, true));
        categories.add(new CategoryModel("Career", "Career", getCountByCategory("Career"), R.color.categoryCareer, true));
        categories.add(new CategoryModel("Personal", "Personal", getCountByCategory("Personal"), R.color.categoryPersonal, true));
        categories.add(new CategoryModel("Shopping", "Shopping", getCountByCategory("Shopping"), R.color.categoryShopping, true));
        categories.add(new CategoryModel("Other", "Other", getCountByCategory("Other"), R.color.categoryOther, true));
        return categories;
    }

    public void updateNotification(NotificationModel updated) {
        if (dbHelper != null) {
            dbHelper.upsert(updated);
        }
    }

    public void addCapturedNotification(NotificationModel model) {
        if (dbHelper != null) {
            dbHelper.upsert(model);
        }
    }

    public void deleteNotification(String id) {
        if (dbHelper != null) {
            dbHelper.delete(id);
        }
    }
}
