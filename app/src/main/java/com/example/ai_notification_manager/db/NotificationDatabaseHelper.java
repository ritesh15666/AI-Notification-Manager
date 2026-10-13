package com.example.ai_notification_manager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ai_notification_manager.model.NotificationModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ai_notifications.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NOTIFICATIONS = "notifications";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_SOURCE_APP = "sourceApp";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_IS_READ = "isRead";
    private static final String COLUMN_IS_IMPORTANT = "isImportant";
    private static final String COLUMN_PRIORITY_SCORE = "priorityScore";
    private static final String COLUMN_CLASSIFICATION_REASON = "classificationReason";
    private static final String COLUMN_URGENCY = "urgency";
    private static final String COLUMN_CONFIDENCE = "confidence";
    private static final String COLUMN_CLASSIFICATION_SOURCE = "classificationSource";
    private static final String COLUMN_MODEL_VERSION = "modelVersion";
    private static final String COLUMN_PACKAGE_NAME = "packageName";

    private static NotificationDatabaseHelper instance;

    public static synchronized NotificationDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private NotificationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_MESSAGE + " TEXT, " +
                COLUMN_SOURCE_APP + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_PRIORITY + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT, " +
                COLUMN_IS_READ + " INTEGER, " +
                COLUMN_IS_IMPORTANT + " INTEGER, " +
                COLUMN_PRIORITY_SCORE + " INTEGER, " +
                COLUMN_CLASSIFICATION_REASON + " TEXT, " +
                COLUMN_URGENCY + " TEXT, " +
                COLUMN_CONFIDENCE + " REAL, " +
                COLUMN_CLASSIFICATION_SOURCE + " TEXT, " +
                COLUMN_MODEL_VERSION + " TEXT, " +
                COLUMN_PACKAGE_NAME + " TEXT)";
        db.execSQL(CREATE_TABLE);
        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_NOTIFICATIONS + " ADD COLUMN " + COLUMN_PACKAGE_NAME + " TEXT");
            } catch (Exception ignored) {}
        } else {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
            onCreate(db);
        }
    }

    private void seedInitialData(SQLiteDatabase db) {
        // Important: 8
        insertNotification(db, new NotificationModel("imp_1", "Payment received", "₹2,500 has been credited to your account.", "Banking app", "Important", "High", "2 min ago", false, true, 90, "Financial transaction detected", "Soon", 0.92f, "RULE_ENGINE", "Rule_v1.0", "General", null, "com.bank.app"));
        insertNotification(db, new NotificationModel("imp_2", "Security alert", "New sign-in detected from Chrome on Windows.", "Google", "Important", "Critical", "15 min ago", false, true, 98, "Security alert detected", "Immediate", 0.96f, "RULE_ENGINE", "Rule_v1.0", "General", null, "com.google.android.googlequicksearchbox"));
        insertNotification(db, new NotificationModel("imp_3", "Electricity Bill Due", "Your bill of ₹1,420 is due tomorrow.", "PowerCorp", "Important", "High", "1 hour ago", true, true, 85, "Bill payment reminder", "Soon", 0.90f, "RULE_ENGINE", "Rule_v1.0", "General", null, "com.power.corp"));
        insertNotification(db, new NotificationModel("imp_4", "Server downtime warning", "Scheduled maintenance in production cluster at 2 AM.", "DevOps Bot", "Important", "High", "3 hours ago", false, true, 88, "Critical system warning", "Immediate", 0.91f, "RULE_ENGINE", "Rule_v1.0", "General", null, "com.devops.bot"));
        insertNotification(db, new NotificationModel("imp_5", "Credit Card Statement", "Your monthly statement is ready to view.", "HDFC Bank", "Important", "Normal", "5 hours ago", true, true, 80, "Statement notification", "When convenient", 0.85f, "RULE_ENGINE", "Rule_v1.0", "General", null, "com.hdfc.bank"));
        insertNotification(db, new NotificationModel("imp_6", "Subscription Renewing", "Your cloud storage plan renews in 3 days.", "CloudDrive", "Important", "Normal", "Yesterday", true, true, 75, "Subscription notice", "When convenient", 0.82f, "RULE_ENGINE", "Rule_v1.0", "General", null, "com.clouddrive.app"));
        insertNotification(db, new NotificationModel("imp_7", "Tax Filing Deadline", "Reminder: Submit your quarterly estimates this week.", "TaxAdvisor", "Important", "High", "Yesterday", false, true, 89, "Tax deadline reminder", "Soon", 0.93f, "RULE_ENGINE", "Rule_v1.0", "General", null, "com.tax.advisor"));
        insertNotification(db, new NotificationModel("imp_8", "Package Out for Delivery", "Order #4829 is out for delivery today.", "DeliveryApp", "Important", "Normal", "1 day ago", true, true, 82, "Delivery update", "Soon", 0.88f, "RULE_ENGINE", "Rule_v1.0", "General", null, "com.delivery.app"));

        // Study: 24
        for (int i = 1; i <= 24; i++) {
            String title = i == 1 ? "Assignment submission due tomorrow" : (i == 2 ? "Exam timetable published" : "Study group update #" + i);
            String msg = i == 1 ? "Please upload your Data Structures project report before 11:59 PM." : "Check the portal for room allocation and schedule details.";
            if (i > 2) msg = "New notes and reading materials uploaded for module " + i + ".";
            insertNotification(db, new NotificationModel("study_" + i, title, msg, "University Portal", "Study", i <= 3 ? "High" : "Normal", i + " hrs ago", i > 5, false, 85, "Academic assignment/exam update", "Soon", 0.88f, "RULE_ENGINE", "Rule_v1.0", "General", null, "com.university.portal"));
        }

        // Career: 12
        for (int i = 1; i <= 12; i++) {
            String title = i == 1 ? "Interview scheduled" : (i == 2 ? "Application status changed" : "Job alert match #" + i);
            String msg = i == 1 ? "Your interview is scheduled for tomorrow at 10:30 AM." : "Recruiter viewed your resume and updated application progress.";
            if (i > 2) msg = "New senior software engineer role matching your profile in TechCorp.";
            insertNotification(db, new NotificationModel("career_" + i, title, msg, i % 2 == 0 ? "Email" : "LinkedIn", "Career", i <= 2 ? "High" : "Normal", (i * 2) + " hrs ago", i > 3, i <= 2, 85, "Professional interview or career update", "Soon", 0.89f, "RULE_ENGINE", "Rule_v1.0", "General", null, "com.linkedin.android"));
        }

        // Personal: 35
        for (int i = 1; i <= 35; i++) {
            String title = i == 1 ? "New message from Alex" : (i == 2 ? "Family dinner reminder" : "Chat notification #" + i);
            String msg = i == 1 ? "Hey! Are we still meeting up for coffee at 4 PM?" : "Don't forget mom's birthday gathering this weekend.";
            if (i > 2) msg = "New message in group chat from friend " + i + ".";
            insertNotification(db, new NotificationModel("personal_" + i, title, msg, i % 2 == 0 ? "WhatsApp" : "Telegram", "Personal", "Normal", i + " hrs ago", i > 10, false, 55, "Direct communication message", "When convenient", 0.86f, "RULE_ENGINE", "Rule_v1.0", "General", null, "com.whatsapp"));
        }

        // Shopping: 31
        for (int i = 1; i <= 31; i++) {
            String title = i == 1 ? "Weekend sale is now live" : (i == 2 ? "Special discount available" : "Flash deal #" + i);
            String msg = i == 1 ? "Get up to 50% off on electronics and fashion items this weekend." : "Use coupon SAVE20 for an extra 20% off your cart.";
            if (i > 2) msg = "Price dropped on an item in your wishlist!";
            insertNotification(db, new NotificationModel("shop_" + i, title, msg, i % 2 == 0 ? "Amazon" : "Flipkart", "Shopping", "Low", (i * 3) + " hrs ago", i > 5, false, 20, "Commercial promotional offer", "When convenient", 0.91f, "RULE_ENGINE", "Rule_v1.0", "General", null, "com.amazon.mShop.android.shopping"));
        }

        // Other: 14
        for (int i = 1; i <= 14; i++) {
            String title = i == 1 ? "Application update available" : "System notice #" + i;
            String msg = i == 1 ? "Version 2.4.1 is now available with bug fixes and performance improvements." : "Routine background synchronization completed successfully.";
            insertNotification(db, new NotificationModel("other_" + i, title, msg, "System", "Other", "Low", (i * 4) + " hrs ago", true, false, 25, "General app notification", "When convenient", 0.65f, "RULE_ENGINE", "Rule_v1.0", "General", null, "android"));
        }
    }

    public void insertNotification(SQLiteDatabase db, NotificationModel model) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, model.getId());
        cv.put(COLUMN_TITLE, model.getTitle());
        cv.put(COLUMN_MESSAGE, model.getMessage());
        cv.put(COLUMN_SOURCE_APP, model.getSourceApp());
        cv.put(COLUMN_CATEGORY, model.getCategory());
        cv.put(COLUMN_PRIORITY, model.getPriority());
        cv.put(COLUMN_TIMESTAMP, model.getTimestamp());
        cv.put(COLUMN_IS_READ, model.isRead() ? 1 : 0);
        cv.put(COLUMN_IS_IMPORTANT, model.isImportant() ? 1 : 0);
        cv.put(COLUMN_PRIORITY_SCORE, model.getPriorityScore());
        cv.put(COLUMN_CLASSIFICATION_REASON, model.getClassificationReason());
        cv.put(COLUMN_URGENCY, model.getUrgency());
        cv.put(COLUMN_CONFIDENCE, model.getConfidence());
        cv.put(COLUMN_CLASSIFICATION_SOURCE, model.getClassificationSource());
        cv.put(COLUMN_MODEL_VERSION, model.getModelVersion());
        cv.put(COLUMN_PACKAGE_NAME, model.getPackageName());

        db.insertWithOnConflict(TABLE_NOTIFICATIONS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void upsert(NotificationModel model) {
        SQLiteDatabase db = getWritableDatabase();
        insertNotification(db, model);
    }

    public List<NotificationModel> getAllNotifications() {
        List<NotificationModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " ORDER BY " + COLUMN_TIMESTAMP + " DESC", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToModel(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public List<NotificationModel> getNotificationsByCategory(String category) {
        if (category == null || category.equalsIgnoreCase("All")) {
            return getAllNotifications();
        }
        List<NotificationModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE " + COLUMN_CATEGORY + " COLLATE NOCASE = ? ORDER BY " + COLUMN_TIMESTAMP + " DESC", new String[]{category});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToModel(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public List<NotificationModel> searchNotifications(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllNotifications();
        }
        List<NotificationModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String q = "%" + query.trim() + "%";
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE " + COLUMN_TITLE + " LIKE ? OR " + COLUMN_MESSAGE + " LIKE ? OR " + COLUMN_SOURCE_APP + " LIKE ? ORDER BY " + COLUMN_TIMESTAMP + " DESC", new String[]{q, q, q});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToModel(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public int getCountByCategory(String category) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS + " WHERE " + COLUMN_CATEGORY + " COLLATE NOCASE = ?", new String[]{category});
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    public int getImportantCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS + " WHERE " + COLUMN_IS_IMPORTANT + " = 1 OR " + COLUMN_CATEGORY + " COLLATE NOCASE = 'Important'", null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    public void delete(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NOTIFICATIONS, COLUMN_ID + " = ?", new String[]{id});
    }

    public void clearAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NOTIFICATIONS, null, null);
    }

    public void resetToDefault() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
    }

    private NotificationModel cursorToModel(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
        String message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE));
        String sourceApp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SOURCE_APP));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
        String priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY));
        String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
        boolean isRead = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_READ)) == 1;
        boolean isImportant = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_IMPORTANT)) == 1;
        int priorityScore = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY_SCORE));
        String classificationReason = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASSIFICATION_REASON));
        String urgency = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URGENCY));
        float confidence = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_CONFIDENCE));
        String classificationSource = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASSIFICATION_SOURCE));
        String modelVersion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODEL_VERSION));
        String packageName = "";
        try {
            int pkgIdx = cursor.getColumnIndex(COLUMN_PACKAGE_NAME);
            if (pkgIdx != -1) {
                packageName = cursor.getString(pkgIdx);
            }
        } catch (Exception ignored) {}

        return new NotificationModel(id, title, message, sourceApp, category, priority, timestamp, isRead, isImportant, priorityScore, classificationReason, urgency, confidence, classificationSource, modelVersion, "General", null, packageName != null ? packageName : "");
    }
}
