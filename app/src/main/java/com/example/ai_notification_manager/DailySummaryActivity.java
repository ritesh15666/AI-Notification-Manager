package com.example.ai_notification_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ai_notification_manager.model.NotificationModel;
import com.example.ai_notification_manager.repository.NotificationRepository;
import com.example.ai_notification_manager.utils.DailySummaryManager;

import java.util.List;

public class DailySummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_summary);

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        NotificationRepository repo = NotificationRepository.getInstance(this);
        List<NotificationModel> notifications = repo.getAllNotifications();
        DailySummaryManager.SummaryStats stats = DailySummaryManager.generateStats(notifications);

        TextView tvSummaryOverviewText = findViewById(R.id.tvSummaryOverviewText);
        TextView tvTotalCount = findViewById(R.id.tvTotalCount);
        TextView tvUnreadCount = findViewById(R.id.tvUnreadCount);
        TextView tvCritHighCount = findViewById(R.id.tvCritHighCount);

        TextView tvCatImportant = findViewById(R.id.tvCatImportant);
        TextView tvCatStudy = findViewById(R.id.tvCatStudy);
        TextView tvCatCareer = findViewById(R.id.tvCatCareer);
        TextView tvCatPersonal = findViewById(R.id.tvCatPersonal);
        TextView tvCatShopping = findViewById(R.id.tvCatShopping);
        TextView tvCatOther = findViewById(R.id.tvCatOther);

        tvTotalCount.setText(String.valueOf(stats.getTotalCount()));
        tvUnreadCount.setText(String.valueOf(stats.getUnreadCount()));
        tvCritHighCount.setText(String.valueOf(stats.getCriticalOrHighCount()));

        tvSummaryOverviewText.setText(String.format("You have received %d notifications (%d unread). Your smart inbox has identified %d critical or high priority items.",
                stats.getTotalCount(), stats.getUnreadCount(), stats.getCriticalOrHighCount()));

        tvCatImportant.setText(String.format("• Important: %02d", stats.getImportantCount()));
        tvCatStudy.setText(String.format("• Study: %02d", stats.getStudyCount()));
        tvCatCareer.setText(String.format("• Career: %02d", stats.getCareerCount()));
        tvCatPersonal.setText(String.format("• Personal: %02d", stats.getPersonalCount()));
        tvCatShopping.setText(String.format("• Shopping: %02d", stats.getShoppingCount()));
        tvCatOther.setText(String.format("• Other: %02d", stats.getOtherCount()));

        // Tap actions to open filtered Inbox
        tvCatImportant.setOnClickListener(v -> openInbox("Important"));
        tvCatStudy.setOnClickListener(v -> openInbox("Study"));
        tvCatCareer.setOnClickListener(v -> openInbox("Career"));
        tvCatPersonal.setOnClickListener(v -> openInbox("Personal"));
        tvCatShopping.setOnClickListener(v -> openInbox("Shopping"));
        tvCatOther.setOnClickListener(v -> openInbox("Other"));
    }

    private void openInbox(String category) {
        Intent intent = new Intent(this, InboxActivity.class);
        intent.putExtra("selected_category", category);
        startActivity(intent);
    }
}
