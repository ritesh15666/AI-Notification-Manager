package com.example.ai_notification_manager.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class AppIconResolver {
    private static final String TAG = "AppIconResolver";
    private static final Map<String, Drawable> iconCache = new HashMap<>();
    private static final Map<String, CharSequence> labelCache = new HashMap<>();
    private static final Map<String, String> knownPackageLabels = new HashMap<>();

    static {
        // Messaging & Social
        knownPackageLabels.put("com.snapchat.android", "Snapchat");
        knownPackageLabels.put("com.whatsapp", "WhatsApp");
        knownPackageLabels.put("com.instagram.android", "Instagram");
        knownPackageLabels.put("org.telegram.messenger", "Telegram");
        knownPackageLabels.put("com.twitter.android", "X");
        knownPackageLabels.put("com.facebook.katana", "Facebook");
        knownPackageLabels.put("com.linkedin.android", "LinkedIn");
        knownPackageLabels.put("com.google.android.gm", "Gmail");
        knownPackageLabels.put("com.google.android.apps.messaging", "Messages");
        knownPackageLabels.put("com.google.android.youtube", "YouTube");

        // Payments & Shopping
        knownPackageLabels.put("com.phonepe.app", "PhonePe");
        knownPackageLabels.put("com.google.android.apps.nbu.paisa.user", "Google Pay");
        knownPackageLabels.put("net.one97.paytm", "Paytm");
        knownPackageLabels.put("in.meesho.supply", "Meesho");
        knownPackageLabels.put("com.flipkart.android", "Flipkart");
        knownPackageLabels.put("com.amazon.mShop.android.shopping", "Amazon");
        knownPackageLabels.put("com.zepto.customer.app", "Zepto");
        knownPackageLabels.put("com.grofers.customerapp", "Blinkit");
        knownPackageLabels.put("in.swiggy.android", "Swiggy");

        // Google & Utilities
        knownPackageLabels.put("com.google.android.googlequicksearchbox", "Google");
        knownPackageLabels.put("com.android.chrome", "Chrome");
        knownPackageLabels.put("com.google.android.dialer", "Phone");
        knownPackageLabels.put("com.google.android.apps.nbu.files", "Files");
        knownPackageLabels.put("com.google.android.apps.weather", "Weather");

        // Telecom
        knownPackageLabels.put("com.airtel.thanks", "Airtel");
        knownPackageLabels.put("com.jio.myjio", "Jio");
        knownPackageLabels.put("com.mobicart.vi", "Vi");
        knownPackageLabels.put("com.myvi.user", "Vi");
    }

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
            if (icon != null) {
                iconCache.put(packageName, icon);
                Log.d(TAG, "Resolved installed app icon for package: " + packageName);
                return icon;
            }
        } catch (Exception e) {
            Log.d(TAG, "App not installed or icon unavailable for package: " + packageName);
        }

        Drawable fallback = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_agenda);
        iconCache.put(packageName, fallback);
        return fallback;
    }

    public static CharSequence getAppLabel(Context context, String packageName, String defaultLabel) {
        if (packageName == null || packageName.isEmpty()) {
            return formatDefaultLabel(defaultLabel);
        }
        if (labelCache.containsKey(packageName)) {
            return labelCache.get(packageName);
        }
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            CharSequence label = pm.getApplicationLabel(ai);
            if (label != null && label.length() > 0) {
                labelCache.put(packageName, label);
                Log.d(TAG, "Resolved installed app label '" + label + "' for package: " + packageName);
                return label;
            }
        } catch (Exception e) {
            // Check exact mapping dictionary
            if (knownPackageLabels.containsKey(packageName)) {
                String mappedLabel = knownPackageLabels.get(packageName);
                labelCache.put(packageName, mappedLabel);
                Log.d(TAG, "Using known mapped label '" + mappedLabel + "' for package: " + packageName);
                return mappedLabel;
            }
        }

        CharSequence formatted = formatDefaultLabel(defaultLabel);
        labelCache.put(packageName, formatted);
        return formatted;
    }

    private static String formatDefaultLabel(String defaultLabel) {
        if (defaultLabel == null || defaultLabel.isEmpty()) {
            return "App";
        }
        if (defaultLabel.startsWith("App (")) {
            int start = defaultLabel.indexOf('(') + 1;
            int end = defaultLabel.indexOf(')');
            if (start > 0 && end > start) {
                String pkgPart = defaultLabel.substring(start, end);
                if (knownPackageLabels.containsKey(pkgPart)) {
                    return knownPackageLabels.get(pkgPart);
                }
                int lastDot = pkgPart.lastIndexOf('.');
                if (lastDot != -1 && lastDot < pkgPart.length() - 1) {
                    String name = pkgPart.substring(lastDot + 1);
                    return name.substring(0, 1).toUpperCase() + name.substring(1);
                }
                return pkgPart;
            }
        }
        return defaultLabel;
    }
}
