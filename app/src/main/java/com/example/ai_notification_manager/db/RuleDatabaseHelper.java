package com.example.ai_notification_manager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ai_notification_manager.model.RuleModel;

import java.util.ArrayList;
import java.util.List;

public class RuleDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ai_rules.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_RULES = "custom_rules";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "ruleName";
    private static final String COLUMN_KEYWORD = "keyword";
    private static final String COLUMN_CATEGORY = "targetCategory";
    private static final String COLUMN_PRIORITY = "targetPriority";
    private static final String COLUMN_ENABLED = "isEnabled";
    private static final String COLUMN_ORDER = "ruleOrder";

    private static RuleDatabaseHelper instance;

    public static synchronized RuleDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new RuleDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private RuleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE = "CREATE TABLE " + TABLE_RULES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_KEYWORD + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_PRIORITY + " TEXT, " +
                COLUMN_ENABLED + " INTEGER, " +
                COLUMN_ORDER + " INTEGER)";
        db.execSQL(CREATE);
        seedDefaultRules(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RULES);
        onCreate(db);
    }

    private void seedDefaultRules(SQLiteDatabase db) {
        insertRule(db, new RuleModel(0, "Fraud Protection", "fraud", "Important", "Critical", true, 1));
        insertRule(db, new RuleModel(0, "Assignment Deadline", "assignment due", "Study", "High", true, 2));
        insertRule(db, new RuleModel(0, "Job Interview", "interview scheduled", "Career", "High", true, 3));
        insertRule(db, new RuleModel(0, "Promotional Sale", "sale", "Shopping", "Low", true, 4));
    }

    public void insertRule(SQLiteDatabase db, RuleModel rule) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, rule.getRuleName());
        cv.put(COLUMN_KEYWORD, rule.getKeyword());
        cv.put(COLUMN_CATEGORY, rule.getTargetCategory());
        cv.put(COLUMN_PRIORITY, rule.getTargetPriority());
        cv.put(COLUMN_ENABLED, rule.isEnabled() ? 1 : 0);
        cv.put(COLUMN_ORDER, rule.getRuleOrder());
        db.insert(TABLE_RULES, null, cv);
    }

    public List<RuleModel> getAllRules() {
        List<RuleModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RULES + " ORDER BY " + COLUMN_ORDER + " ASC", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String keyword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KEYWORD));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
                String priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY));
                boolean enabled = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ENABLED)) == 1;
                int order = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER));
                list.add(new RuleModel(id, name, keyword, category, priority, enabled, order));
            }
            cursor.close();
        }
        return list;
    }
}
