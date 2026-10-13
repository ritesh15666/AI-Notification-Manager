package com.example.ai_notification_manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ai_notification_manager.adapter.CategoryAdapter;
import com.example.ai_notification_manager.adapter.NotificationAdapter;
import com.example.ai_notification_manager.model.NotificationModel;
import com.example.ai_notification_manager.repository.NotificationRepository;
import com.example.ai_notification_manager.utils.AppPreferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private NotificationRepository repository;
    private NotificationAdapter priorityAdapter;
    private CategoryAdapter categoryAdapter;

    private TextView tvTotalCount, tvImportantCount, tvOrganizedCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppPreferences prefs = new AppPreferences(this);
        String themeMode = prefs.getThemeMode();
        if (themeMode.equals("light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (themeMode.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        setContentView(R.layout.activity_main);

        NotificationRepository.init(this);
        repository = NotificationRepository.getInstance();

        initViews();
        setupGreeting();
        updateDashboardStats();
        setupPriorityNotifications();
        setupCategories();
        setupBottomNav();
    }

    private void initViews() {
        ImageView ivSettings = findViewById(R.id.ivSettings);
        ivSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        TextView tvViewAllInbox = findViewById(R.id.tvViewAllInbox);
        tvViewAllInbox.setOnClickListener(v -> startActivity(new Intent(this, InboxActivity.class)));

        TextView tvManageCategories = findViewById(R.id.tvManageCategories);
        tvManageCategories.setOnClickListener(v -> startActivity(new Intent(this, CategoryManagementActivity.class)));

        MaterialButton btnViewInsights = findViewById(R.id.btnViewInsights);
        btnViewInsights.setOnClickListener(v -> startActivity(new Intent(this, InsightsActivity.class)));

        View cardAssistant = findViewById(R.id.cardAssistant);
        if (cardAssistant != null) {
            cardAssistant.setOnClickListener(v -> startActivity(new Intent(this, AssistantActivity.class)));
        }

        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvImportantCount = findViewById(R.id.tvImportantCount);
        tvOrganizedCount = findViewById(R.id.tvOrganizedCount);
    }

    private void updateDashboardStats() {
        if (repository == null) return;
        List<NotificationModel> all = repository.getAllNotifications();
        int total = all.size();
        int important = repository.getImportantCount();
        int organized = Math.max(0, total - important);

        if (tvTotalCount != null) tvTotalCount.setText(String.valueOf(total));
        if (tvImportantCount != null) tvImportantCount.setText(String.format(Locale.getDefault(), "%02d", important));
        if (tvOrganizedCount != null) tvOrganizedCount.setText(String.valueOf(organized));
    }

    private void setupGreeting() {
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = "Good morning";
        if (hour >= 12 && hour < 17) {
            greeting = "Good afternoon";
        } else if (hour >= 17) {
            greeting = "Good evening";
        }
        tvGreeting.setText(greeting);
    }

    private void setupPriorityNotifications() {
        RecyclerView rvPriority = findViewById(R.id.rvPriorityNotifications);
        rvPriority.setLayoutManager(new LinearLayoutManager(this));

        List<NotificationModel> all = repository.getAllNotifications();
        List<NotificationModel> priorityList = all.stream()
                .filter(n -> n.isImportant() || n.getPriority().equalsIgnoreCase("High") || n.getPriority().equalsIgnoreCase("Critical"))
                .limit(2)
                .collect(Collectors.toList());

        priorityAdapter = new NotificationAdapter(priorityList, notification -> {
            Intent intent = new Intent(this, NotificationDetailActivity.class);
            intent.putExtra("notification", notification);
            startActivity(intent);
        });
        rvPriority.setAdapter(priorityAdapter);
    }

    private void setupCategories() {
        RecyclerView rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new GridLayoutManager(this, 2));

        categoryAdapter = new CategoryAdapter(repository.getCategories(), category -> {
            Intent intent = new Intent(this, InboxActivity.class);
            intent.putExtra("selected_category", category.getName());
            startActivity(intent);
        });
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
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
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboardStats();
        if (priorityAdapter != null) {
            List<NotificationModel> all = repository.getAllNotifications();
            List<NotificationModel> priorityList = all.stream()
                    .filter(n -> n.isImportant() || n.getPriority().equalsIgnoreCase("High") || n.getPriority().equalsIgnoreCase("Critical"))
                    .limit(2)
                    .collect(Collectors.toList());
            priorityAdapter.updateList(priorityList);
        }
        if (categoryAdapter != null) {
            categoryAdapter.updateList(repository.getCategories());
        }
    }
}
