package com.example.ai_notification_manager;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.ai_notification_manager.repository.NotificationRepository;
import com.example.ai_notification_manager.utils.AppPreferences;
import com.example.ai_notification_manager.utils.NotificationPermissionUtil;
import com.example.ai_notification_manager.utils.NotificationShortcutHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private AppPreferences preferences;
    private MaterialButton btnOpenNotificationSettings, btnAppNotificationSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = new AppPreferences(this);

        initViews();
        setupBottomNav();
    }

    private void initViews() {
        btnOpenNotificationSettings = findViewById(R.id.btnOpenNotificationSettings);
        btnAppNotificationSettings = findViewById(R.id.btnAppNotificationSettings);
        SwitchMaterial switchSmartClassification = findViewById(R.id.switchSmartClassification);
        SwitchMaterial switchHighlightImportant = findViewById(R.id.switchHighlightImportant);
        SwitchMaterial switchQuickEntry = findViewById(R.id.switchQuickEntry);
        SwitchMaterial switchCustomNotifications = findViewById(R.id.switchCustomNotifications);

        SwitchMaterial switchCatImportant = findViewById(R.id.switchCatImportant);
        SwitchMaterial switchCatStudy = findViewById(R.id.switchCatStudy);
        SwitchMaterial switchCatCareer = findViewById(R.id.switchCatCareer);
        SwitchMaterial switchCatPersonal = findViewById(R.id.switchCatPersonal);
        SwitchMaterial switchCatShopping = findViewById(R.id.switchCatShopping);
        SwitchMaterial switchCatOther = findViewById(R.id.switchCatOther);

        MaterialButton btnThemeLight = findViewById(R.id.btnThemeLight);
        MaterialButton btnThemeDark = findViewById(R.id.btnThemeDark);
        MaterialButton btnThemeSystem = findViewById(R.id.btnThemeSystem);

        TextView tvClearHistory = findViewById(R.id.tvClearHistory);
        TextView tvPrivacyPolicy = findViewById(R.id.tvPrivacyPolicy);
        MaterialButton btnDailySummary = findViewById(R.id.btnDailySummary);
        MaterialButton btnFocusMode = findViewById(R.id.btnFocusMode);
        MaterialButton btnCustomRules = findViewById(R.id.btnCustomRules);

        switchSmartClassification.setChecked(preferences.isSmartClassificationEnabled());
        switchHighlightImportant.setChecked(preferences.isHighlightImportantEnabled());
        if (switchQuickEntry != null) {
            switchQuickEntry.setChecked(preferences.isQuickEntryEnabled());
        }
        if (switchCustomNotifications != null) {
            switchCustomNotifications.setChecked(preferences.isIncomingAlertsEnabled());
        }

        if (switchCatImportant != null) {
            switchCatImportant.setChecked(preferences.isCategoryEnabled("Important"));
            switchCatImportant.setOnCheckedChangeListener((b, isChecked) -> preferences.setCategoryEnabled("Important", isChecked));
        }
        if (switchCatStudy != null) {
            switchCatStudy.setChecked(preferences.isCategoryEnabled("Study"));
            switchCatStudy.setOnCheckedChangeListener((b, isChecked) -> preferences.setCategoryEnabled("Study", isChecked));
        }
        if (switchCatCareer != null) {
            switchCatCareer.setChecked(preferences.isCategoryEnabled("Career"));
            switchCatCareer.setOnCheckedChangeListener((b, isChecked) -> preferences.setCategoryEnabled("Career", isChecked));
        }
        if (switchCatPersonal != null) {
            switchCatPersonal.setChecked(preferences.isCategoryEnabled("Personal"));
            switchCatPersonal.setOnCheckedChangeListener((b, isChecked) -> preferences.setCategoryEnabled("Personal", isChecked));
        }
        if (switchCatShopping != null) {
            switchCatShopping.setChecked(preferences.isCategoryEnabled("Shopping"));
            switchCatShopping.setOnCheckedChangeListener((b, isChecked) -> preferences.setCategoryEnabled("Shopping", isChecked));
        }
        if (switchCatOther != null) {
            switchCatOther.setChecked(preferences.isCategoryEnabled("Other"));
            switchCatOther.setOnCheckedChangeListener((b, isChecked) -> preferences.setCategoryEnabled("Other", isChecked));
        }

        if (btnThemeLight != null) {
            btnThemeLight.setOnClickListener(v -> {
                preferences.setThemeMode("light");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
            });
        }
        if (btnThemeDark != null) {
            btnThemeDark.setOnClickListener(v -> {
                preferences.setThemeMode("dark");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                recreate();
            });
        }
        if (btnThemeSystem != null) {
            btnThemeSystem.setOnClickListener(v -> {
                preferences.setThemeMode("system");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                recreate();
            });
        }

        switchSmartClassification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.setSmartClassificationEnabled(isChecked);
            Toast.makeText(this, isChecked ? "Smart classification enabled" : "Smart classification disabled", Toast.LENGTH_SHORT).show();
        });

        switchHighlightImportant.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.setHighlightImportantEnabled(isChecked);
            Toast.makeText(this, isChecked ? "Important alerts highlighted" : "Highlighting disabled", Toast.LENGTH_SHORT).show();
        });

        if (switchQuickEntry != null) {
            switchQuickEntry.setOnCheckedChangeListener((buttonView, isChecked) -> {
                preferences.setQuickEntryEnabled(isChecked);
                if (isChecked) {
                    NotificationShortcutHelper.showShortcut(this);
                    Toast.makeText(this, "Quick-entry notification enabled", Toast.LENGTH_SHORT).show();
                } else {
                    NotificationShortcutHelper.hideShortcut(this);
                    Toast.makeText(this, "Quick-entry notification disabled", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (switchCustomNotifications != null) {
            switchCustomNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
                preferences.setIncomingAlertsEnabled(isChecked);
                Toast.makeText(this, isChecked ? "Custom lock screen alerts enabled" : "Custom lock screen alerts disabled", Toast.LENGTH_SHORT).show();
            });
        }

        btnOpenNotificationSettings.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            } catch (Exception e) {
                Toast.makeText(this, "Unable to open notification settings", Toast.LENGTH_SHORT).show();
            }
        });

        if (btnAppNotificationSettings != null) {
            btnAppNotificationSettings.setOnClickListener(v -> {
                try {
                    Intent intent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    } else {
                        intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                    }
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Unable to open app notification settings", Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnDailySummary.setOnClickListener(v -> {
            startActivity(new Intent(this, DailySummaryActivity.class));
        });

        if (btnFocusMode != null) {
            btnFocusMode.setOnClickListener(v -> {
                startActivity(new Intent(this, FocusModeActivity.class));
            });
        }

        if (btnCustomRules != null) {
            btnCustomRules.setOnClickListener(v -> {
                startActivity(new Intent(this, RulesActivity.class));
            });
        }

        tvClearHistory.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Clear Local History")
                    .setMessage("Are you sure you want to delete all locally stored notification history and restore defaults?")
                    .setPositiveButton("Clear", (dialog, which) -> {
                        NotificationRepository.getInstance().resetToDefault();
                        Toast.makeText(this, "Local notification history cleared", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        tvPrivacyPolicy.setOnClickListener(v -> startActivity(new Intent(this, PrivacyActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NotificationPermissionUtil.isNotificationServiceEnabled(this)) {
            btnOpenNotificationSettings.setText("Status: Enabled ✓");
        } else {
            btnOpenNotificationSettings.setText("Open System Settings");
        }
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_settings);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_inbox) {
                startActivity(new Intent(this, InboxActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_insights) {
                startActivity(new Intent(this, InsightsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                return true;
            }
            return false;
        });
    }
}
