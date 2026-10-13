package com.example.ai_notification_manager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ai_notification_manager.adapter.NotificationAdapter;
import com.example.ai_notification_manager.classifier.NotificationPriorityEngine;
import com.example.ai_notification_manager.model.FilterOptions;
import com.example.ai_notification_manager.model.NotificationModel;
import com.example.ai_notification_manager.repository.NotificationRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.List;
import java.util.stream.Collectors;

public class InboxActivity extends AppCompatActivity {

    private NotificationRepository repository;
    private NotificationAdapter adapter;
    private RecyclerView rvNotifications;
    private LinearLayout layoutEmpty;
    private EditText etSearch;
    private TextView btnFilter;

    private String currentCategory = "All";
    private String currentQuery = "";
    private final FilterOptions filterOptions = new FilterOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        NotificationRepository.init(this);
        repository = NotificationRepository.getInstance();

        initViews();
        handleIncomingIntent();
        setupRecyclerView();
        setupSearch();
        setupFilterButton();
        setupChips();
        setupBottomNav();
    }

    private void initViews() {
        rvNotifications = findViewById(R.id.rvNotifications);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        etSearch = findViewById(R.id.etSearch);
        btnFilter = findViewById(R.id.btnFilter);
    }

    private void handleIncomingIntent() {
        String category = getIntent().getStringExtra("selected_category");
        if (category != null && !category.isEmpty()) {
            currentCategory = category;
            selectChipByCategory(category);
        }
    }

    private void selectChipByCategory(String category) {
        Chip chip;
        switch (category.toLowerCase()) {
            case "important": chip = findViewById(R.id.chipImportant); break;
            case "study": chip = findViewById(R.id.chipStudy); break;
            case "career": chip = findViewById(R.id.chipCareer); break;
            case "personal": chip = findViewById(R.id.chipPersonal); break;
            case "shopping": chip = findViewById(R.id.chipShopping); break;
            case "other": chip = findViewById(R.id.chipOther); break;
            default: chip = findViewById(R.id.chipAll); break;
        }
        if (chip != null) {
            chip.setChecked(true);
        }
    }

    private void setupRecyclerView() {
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        List<NotificationModel> list = filterNotifications();
        adapter = new NotificationAdapter(list, notification -> {
            Intent intent = new Intent(this, NotificationDetailActivity.class);
            intent.putExtra("notification", notification);
            startActivity(intent);
        });
        rvNotifications.setAdapter(adapter);
        updateEmptyState(list);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString();
                refreshList();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterButton() {
        if (btnFilter != null) {
            btnFilter.setOnClickListener(v -> showAdvancedFilterDialog());
        }
    }

    private void showAdvancedFilterDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_advanced_filter, null);

        Spinner spinnerPriority = dialogView.findViewById(R.id.spinnerPriority);
        Spinner spinnerReadStatus = dialogView.findViewById(R.id.spinnerReadStatus);
        Spinner spinnerDateRange = dialogView.findViewById(R.id.spinnerDateRange);
        Spinner spinnerSortBy = dialogView.findViewById(R.id.spinnerSortBy);

        MaterialButton btnResetFilters = dialogView.findViewById(R.id.btnResetFilters);
        MaterialButton btnApplyFilters = dialogView.findViewById(R.id.btnApplyFilters);

        String[] priorities = {"All", "CRITICAL", "HIGH", "MEDIUM", "LOW"};
        String[] readStatuses = {"All", "Read", "Unread"};
        String[] dateRanges = {"All", "Today", "Yesterday", "Last 7 Days", "Last 30 Days"};
        String[] sortOptions = {"Newest First", "Oldest First", "Highest Priority First", "Lowest Priority First", "Alphabetical"};

        spinnerPriority.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorities));
        spinnerReadStatus.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, readStatuses));
        spinnerDateRange.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dateRanges));
        spinnerSortBy.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sortOptions));

        // Set current selections
        setSpinnerSelection(spinnerPriority, priorities, filterOptions.getPriority());
        setSpinnerSelection(spinnerReadStatus, readStatuses, filterOptions.getReadStatus());
        setSpinnerSelection(spinnerDateRange, dateRanges, filterOptions.getDateRange());
        setSpinnerSelection(spinnerSortBy, sortOptions, filterOptions.getSortBy());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnResetFilters.setOnClickListener(v -> {
            filterOptions.reset();
            dialog.dismiss();
            refreshList();
            Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
        });

        btnApplyFilters.setOnClickListener(v -> {
            filterOptions.setPriority(spinnerPriority.getSelectedItem().toString());
            filterOptions.setReadStatus(spinnerReadStatus.getSelectedItem().toString());
            filterOptions.setDateRange(spinnerDateRange.getSelectedItem().toString());
            filterOptions.setSortBy(spinnerSortBy.getSelectedItem().toString());
            dialog.dismiss();
            refreshList();
            Toast.makeText(this, "Filters applied", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void setSpinnerSelection(Spinner spinner, String[] items, String value) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setupChips() {
        Chip chipAll = findViewById(R.id.chipAll);
        Chip chipImportant = findViewById(R.id.chipImportant);
        Chip chipStudy = findViewById(R.id.chipStudy);
        Chip chipCareer = findViewById(R.id.chipCareer);
        Chip chipPersonal = findViewById(R.id.chipPersonal);
        Chip chipShopping = findViewById(R.id.chipShopping);
        Chip chipOther = findViewById(R.id.chipOther);

        View.OnClickListener chipListener = v -> {
            Chip c = (Chip) v;
            if (c.isChecked()) {
                currentCategory = c.getText().toString();
                filterOptions.setCategory(currentCategory);
                refreshList();
            }
        };

        chipAll.setOnClickListener(chipListener);
        chipImportant.setOnClickListener(chipListener);
        chipStudy.setOnClickListener(chipListener);
        chipCareer.setOnClickListener(chipListener);
        chipPersonal.setOnClickListener(chipListener);
        chipShopping.setOnClickListener(chipListener);
        chipOther.setOnClickListener(chipListener);
    }

    private void refreshList() {
        List<NotificationModel> filtered = filterNotifications();
        adapter.updateList(filtered);
        updateEmptyState(filtered);
    }

    private List<NotificationModel> filterNotifications() {
        List<NotificationModel> base = repository.getNotificationsByCategory(currentCategory);
        List<NotificationModel> filtered = base;

        // 1. Text Search Query
        if (currentQuery != null && !currentQuery.trim().isEmpty()) {
            String q = currentQuery.toLowerCase().trim();
            filtered = filtered.stream().filter(n ->
                    n.getTitle().toLowerCase().contains(q) ||
                            n.getMessage().toLowerCase().contains(q) ||
                            n.getSourceApp().toLowerCase().contains(q)
            ).collect(Collectors.toList());
        }

        // 2. Priority Filter
        if (!filterOptions.getPriority().equalsIgnoreCase("All")) {
            filtered = filtered.stream().filter(n -> {
                NotificationPriorityEngine.PriorityResult res = NotificationPriorityEngine.evaluate(n.getTitle(), n.getMessage(), n.getPackageName(), n.getCategory());
                return res.getLevel().equalsIgnoreCase(filterOptions.getPriority());
            }).collect(Collectors.toList());
        }

        // 3. Read Status Filter
        if (!filterOptions.getReadStatus().equalsIgnoreCase("All")) {
            boolean wantRead = filterOptions.getReadStatus().equalsIgnoreCase("Read");
            filtered = filtered.stream().filter(n -> n.isRead() == wantRead).collect(Collectors.toList());
        }

        // 4. Sorting
        filtered.sort((n1, n2) -> {
            String sortBy = filterOptions.getSortBy();
            if (sortBy.equalsIgnoreCase("Oldest First")) {
                return n1.getTimestamp().compareTo(n2.getTimestamp());
            } else if (sortBy.equalsIgnoreCase("Highest Priority First")) {
                NotificationPriorityEngine.PriorityResult r1 = NotificationPriorityEngine.evaluate(n1.getTitle(), n1.getMessage(), n1.getPackageName(), n1.getCategory());
                NotificationPriorityEngine.PriorityResult r2 = NotificationPriorityEngine.evaluate(n2.getTitle(), n2.getMessage(), n2.getPackageName(), n2.getCategory());
                return Integer.compare(r2.getScore(), r1.getScore());
            } else if (sortBy.equalsIgnoreCase("Lowest Priority First")) {
                NotificationPriorityEngine.PriorityResult r1 = NotificationPriorityEngine.evaluate(n1.getTitle(), n1.getMessage(), n1.getPackageName(), n1.getCategory());
                NotificationPriorityEngine.PriorityResult r2 = NotificationPriorityEngine.evaluate(n2.getTitle(), n2.getMessage(), n2.getPackageName(), n2.getCategory());
                return Integer.compare(r1.getScore(), r2.getScore());
            } else if (sortBy.equalsIgnoreCase("Alphabetical")) {
                return n1.getSourceApp().compareToIgnoreCase(n2.getSourceApp());
            } else {
                // Default: Newest First
                return n2.getTimestamp().compareTo(n1.getTimestamp());
            }
        });

        return filtered;
    }

    private void updateEmptyState(List<NotificationModel> list) {
        if (list.isEmpty()) {
            rvNotifications.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvNotifications.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_inbox);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_inbox) {
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
        refreshList();
    }
}
