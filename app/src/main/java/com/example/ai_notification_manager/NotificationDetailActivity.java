package com.example.ai_notification_manager;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ai_notification_manager.model.NotificationModel;
import com.example.ai_notification_manager.repository.NotificationRepository;
import com.example.ai_notification_manager.utils.TimestampUtil;
import com.google.android.material.button.MaterialButton;

public class NotificationDetailActivity extends AppCompatActivity {

    private NotificationModel notification;
    private NotificationRepository repository;

    private TextView tvDetailCategory, tvDetailTimestamp, tvDetailSource, tvDetailTitle, tvDetailMessage, tvDetailScore, tvDetailReason, tvDetailUrgency, tvDetailConfidence, tvDetailSourceAI;
    private MaterialButton btnToggleRead, btnToggleImportant, btnChangeCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        repository = NotificationRepository.getInstance();
        notification = (NotificationModel) getIntent().getSerializableExtra("notification");

        if (notification == null) {
            finish();
            return;
        }

        initViews();
        bindData();
        setupActions();
    }

    private void initViews() {
        ImageView ivBack = findViewById(R.id.ivBack);
        ImageView ivDelete = findViewById(R.id.ivDelete);

        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailTimestamp = findViewById(R.id.tvDetailTimestamp);
        tvDetailSource = findViewById(R.id.tvDetailSource);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailMessage = findViewById(R.id.tvDetailMessage);
        tvDetailScore = findViewById(R.id.tvDetailScore);
        tvDetailReason = findViewById(R.id.tvDetailReason);
        tvDetailUrgency = findViewById(R.id.tvDetailUrgency);
        tvDetailConfidence = findViewById(R.id.tvDetailConfidence);
        tvDetailSourceAI = findViewById(R.id.tvDetailSourceAI);

        btnToggleRead = findViewById(R.id.btnToggleRead);
        btnToggleImportant = findViewById(R.id.btnToggleImportant);
        btnChangeCategory = findViewById(R.id.btnChangeCategory);

        ivBack.setOnClickListener(v -> finish());
        ivDelete.setOnClickListener(v -> {
            repository.deleteNotification(notification.getId());
            Toast.makeText(this, "Notification deleted", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void bindData() {
        tvDetailCategory.setText(notification.getCategory());
        tvDetailTimestamp.setText(TimestampUtil.format(notification.getTimestamp()));
        tvDetailSource.setText(notification.getSourceApp());
        tvDetailTitle.setText(notification.getTitle());
        tvDetailMessage.setText(notification.getMessage());
        tvDetailScore.setText("Score: " + notification.getPriorityScore() + "/100");
        tvDetailReason.setText(notification.getClassificationReason() != null ? notification.getClassificationReason() : "Rule-based and ML hybrid classification engine processed this notification.");
        tvDetailUrgency.setText("Urgency: " + (notification.getUrgency() != null ? notification.getUrgency() : "When convenient"));
        tvDetailConfidence.setText(String.format("Confidence: %d%%", (int)(notification.getConfidence() * 100)));
        tvDetailSourceAI.setText(notification.getClassificationSource() != null ? notification.getClassificationSource() : "HYBRID");

        btnToggleRead.setText(notification.isRead() ? "Mark as Unread" : "Mark as Read");
        btnToggleImportant.setText(notification.isImportant() ? "Remove Important Status" : "Mark as Important");
    }

    private void setupActions() {
        btnToggleRead.setOnClickListener(v -> {
            notification.setRead(!notification.isRead());
            repository.updateNotification(notification);
            bindData();
            Toast.makeText(this, notification.isRead() ? "Marked as read" : "Marked as unread", Toast.LENGTH_SHORT).show();
        });

        btnToggleImportant.setOnClickListener(v -> {
            boolean newImp = !notification.isImportant();
            notification.setImportant(newImp);
            if (newImp) {
                notification.setCategory("Important");
            }
            repository.updateNotification(notification);
            bindData();
            Toast.makeText(this, newImp ? "Marked as important" : "Removed important status", Toast.LENGTH_SHORT).show();
        });

        btnChangeCategory.setOnClickListener(v -> {
            String[] categories = {"Important", "Study", "Career", "Personal", "Shopping", "Other"};
            new AlertDialog.Builder(this)
                    .setTitle("Change Category")
                    .setItems(categories, (dialog, which) -> {
                        String selected = categories[which];
                        notification.setCategory(selected);
                        if (selected.equalsIgnoreCase("Important")) {
                            notification.setImportant(true);
                        }
                        repository.updateNotification(notification);
                        bindData();
                        Toast.makeText(this, "Moved to " + selected, Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });
    }
}
