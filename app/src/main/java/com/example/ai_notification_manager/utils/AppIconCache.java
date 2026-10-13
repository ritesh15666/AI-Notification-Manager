package com.example.ai_notification_manager.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class AppIconCache {
    private static final Map<String, Drawable> iconCache = new HashMap<>();
    private static final Map<String, CharSequence> labelCache = new HashMap<>();

    public static Drawable getAppIcon(Context context, String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return ContextCompat.getDrawable(context, android.R.drawable.ic_menu_agenda);
        }
        if (iconCache.containsKey(packageName)) {
            return iconCache.get(packageName);
        }
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            Drawable icon = pm.getApplicationIcon(ai);
            iconCache.put(packageName, icon);
            return icon;
        } catch (Exception e) {
            Drawable fallback = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_agenda);
            iconCache.put(packageName, fallback);
            return fallback;
        }
    }

    public static CharSequence getAppLabel(Context context, String packageName, String defaultLabel) {
        if (packageName == null || packageName.isEmpty()) {
            return defaultLabel;
        }
        if (labelCache.containsKey(packageName)) {
            return labelCache.get(packageName);
        }
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            CharSequence label = pm.getApplicationLabel(ai);
            labelCache.put(packageName, label);
            return label;
        } catch (Exception e) {
            labelCache.put(packageName, defaultLabel);
            return defaultLabel;
        }
    }
}
