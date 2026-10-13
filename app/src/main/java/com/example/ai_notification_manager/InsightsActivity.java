package com.example.ai_notification_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ai_notification_manager.repository.NotificationRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InsightsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insights);

        NotificationRepository repository = NotificationRepository.getInstance();

        TextView tvStatTotal = findViewById(R.id.tvStatTotal);
        TextView tvStatImportant = findViewById(R.id.tvStatImportant);
        TextView tvStatPromotional = findViewById(R.id.tvStatPromotional);

        int total = repository.getAllNotifications().size();
        int important = repository.getCountByCategory("Important");
        int shopping = repository.getCountByCategory("Shopping");

        tvStatTotal.setText(String.valueOf(total));
        tvStatImportant.setText(String.valueOf(important));
        tvStatPromotional.setText(String.valueOf(shopping));

        setupBottomNav();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_insights);

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
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}
