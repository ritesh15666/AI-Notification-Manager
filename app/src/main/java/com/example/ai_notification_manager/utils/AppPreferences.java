package com.example.ai_notification_manager.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    private static final String PREF_NAME = "ai_notification_prefs";
    private static final String KEY_ONBOARDING_COMPLETED = "onboarding_completed";
    private static final String KEY_SMART_CLASSIFICATION = "smart_classification";
    private static final String KEY_HIGHLIGHT_IMPORTANT = "highlight_important";
    private static final String KEY_PROMOTIONAL_HANDLING = "promotional_handling";
    private static final String KEY_QUICK_ENTRY = "quick_entry_enabled";
    private static final String KEY_INCOMING_ALERTS = "incoming_alerts_enabled";
    private static final String KEY_THEME_MODE = "theme_mode"; // "light", "dark", "system"

    private final SharedPreferences prefs;

    public AppPreferences(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isOnboardingCompleted() {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false);
    }

    public void setOnboardingCompleted(boolean completed) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply();
    }

    public boolean isSmartClassificationEnabled() {
        return prefs.getBoolean(KEY_SMART_CLASSIFICATION, true);
    }

    public void setSmartClassificationEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SMART_CLASSIFICATION, enabled).apply();
    }

    public boolean isHighlightImportantEnabled() {
        return prefs.getBoolean(KEY_HIGHLIGHT_IMPORTANT, true);
    }

    public void setHighlightImportantEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_HIGHLIGHT_IMPORTANT, enabled).apply();
    }

    public boolean isPromotionalHandlingEnabled() {
        return prefs.getBoolean(KEY_PROMOTIONAL_HANDLING, true);
    }

    public void setPromotionalHandlingEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_PROMOTIONAL_HANDLING, enabled).apply();
    }

    public boolean isQuickEntryEnabled() {
        return prefs.getBoolean(KEY_QUICK_ENTRY, false);
    }

    public void setQuickEntryEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_QUICK_ENTRY, enabled).apply();
    }

    public boolean isIncomingAlertsEnabled() {
        return prefs.getBoolean(KEY_INCOMING_ALERTS, true);
    }

    public void setIncomingAlertsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_INCOMING_ALERTS, enabled).apply();
    }

    public boolean isCategoryEnabled(String category) {
        return prefs.getBoolean("cat_enabled_" + (category != null ? category.toLowerCase() : "other"), true);
    }

    public void setCategoryEnabled(String category, boolean enabled) {
        prefs.edit().putBoolean("cat_enabled_" + (category != null ? category.toLowerCase() : "other"), enabled).apply();
    }

    public String getThemeMode() {
        return prefs.getString(KEY_THEME_MODE, "system");
    }

    public void setThemeMode(String mode) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply();
    }

    public void clearAll(Context context) {
        prefs.edit().clear().apply();
    }
}
