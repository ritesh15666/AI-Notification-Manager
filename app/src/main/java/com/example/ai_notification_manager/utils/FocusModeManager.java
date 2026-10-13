package com.example.ai_notification_manager.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class FocusModeManager {
    private static final String PREF_FOCUS = "ai_focus_prefs";
    private static final String KEY_IS_ACTIVE = "focus_is_active";
    private static final String KEY_PROFILE = "focus_profile";
    private static final String KEY_START_TIME = "focus_start_time";
    private static final String KEY_DURATION_MINS = "focus_duration_mins";
    private static final String KEY_RECEIVED_COUNT = "focus_received_count";
    private static final String KEY_ALLOWED_COUNT = "focus_allowed_count";
    private static final String KEY_DEFERRED_COUNT = "focus_deferred_count";

    private final SharedPreferences prefs;

    public FocusModeManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_FOCUS, Context.MODE_PRIVATE);
    }

    public boolean isSessionActive() {
        return prefs.getBoolean(KEY_IS_ACTIVE, false);
    }

    public void startSession(String profile, int durationMins) {
        prefs.edit()
                .putBoolean(KEY_IS_ACTIVE, true)
                .putString(KEY_PROFILE, profile)
                .putLong(KEY_START_TIME, System.currentTimeMillis())
                .putInt(KEY_DURATION_MINS, durationMins)
                .putInt(KEY_RECEIVED_COUNT, 0)
                .putInt(KEY_ALLOWED_COUNT, 0)
                .putInt(KEY_DEFERRED_COUNT, 0)
                .apply();
    }

    public void stopSession() {
        prefs.edit()
                .putBoolean(KEY_IS_ACTIVE, false)
                .apply();
    }

    public String getProfile() {
        return prefs.getString(KEY_PROFILE, "Deep Work");
    }

    public long getStartTime() {
        return prefs.getLong(KEY_START_TIME, 0);
    }

    public int getDurationMins() {
        return prefs.getInt(KEY_DURATION_MINS, 25);
    }

    public int getReceivedCount() {
        return prefs.getInt(KEY_RECEIVED_COUNT, 0);
    }

    public int getAllowedCount() {
        return prefs.getInt(KEY_ALLOWED_COUNT, 0);
    }

    public int getDeferredCount() {
        return prefs.getInt(KEY_DEFERRED_COUNT, 0);
    }

    public void incrementReceived() {
        int count = getReceivedCount() + 1;
        prefs.edit().putInt(KEY_RECEIVED_COUNT, count).apply();
    }

    public void incrementAllowed() {
        int count = getAllowedCount() + 1;
        prefs.edit().putInt(KEY_ALLOWED_COUNT, count).apply();
    }

    public void incrementDeferred() {
        int count = getDeferredCount() + 1;
        prefs.edit().putInt(KEY_DEFERRED_COUNT, count).apply();
    }

    public long getRemainingMillis() {
        if (!isSessionActive()) return 0;
        long elapsed = System.currentTimeMillis() - getStartTime();
        long totalMillis = getDurationMins() * 60L * 1000L;
        long remaining = totalMillis - elapsed;
        return Math.max(0, remaining);
    }
}
