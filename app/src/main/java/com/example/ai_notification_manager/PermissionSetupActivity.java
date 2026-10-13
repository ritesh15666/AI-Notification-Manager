package com.example.ai_notification_manager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ai_notification_manager.utils.AppPreferences;
import com.example.ai_notification_manager.utils.NotificationPermissionUtil;
import com.google.android.material.button.MaterialButton;

public class PermissionSetupActivity extends AppCompatActivity {

    private MaterialButton btnGrant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_setup);

        btnGrant = findViewById(R.id.btnGrant);
        TextView btnMaybeLater = findViewById(R.id.btnMaybeLater);

        btnGrant.setOnClickListener(v -> {
            if (NotificationPermissionUtil.isNotificationServiceEnabled(this)) {
                proceedToMain();
            } else {
                try {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    startActivity(intent);
                    Toast.makeText(this, "Please enable AI Notification Manager in settings", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Unable to open notification settings", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnMaybeLater.setOnClickListener(v -> proceedToMain());
    }

    private void proceedToMain() {
        AppPreferences prefs = new AppPreferences(this);
        prefs.setOnboardingCompleted(true);
        Intent intent = new Intent(PermissionSetupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NotificationPermissionUtil.isNotificationServiceEnabled(this)) {
            btnGrant.setText("Access Granted - Continue →");
            btnGrant.setOnClickListener(v -> proceedToMain());
        } else {
            btnGrant.setText("Grant Notification Access");
        }
    }
}
