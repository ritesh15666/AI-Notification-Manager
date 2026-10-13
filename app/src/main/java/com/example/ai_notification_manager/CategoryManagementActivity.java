package com.example.ai_notification_manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ai_notification_manager.adapter.CategoryAdapter;
import com.example.ai_notification_manager.repository.NotificationRepository;

public class CategoryManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvCategoryManagement);
        rv.setLayoutManager(new LinearLayoutManager(this));

        NotificationRepository repository = NotificationRepository.getInstance();
        CategoryAdapter adapter = new CategoryAdapter(repository.getCategories(), category -> {
            Intent intent = new Intent(this, InboxActivity.class);
            intent.putExtra("selected_category", category.getName());
            startActivity(intent);
        });
        rv.setAdapter(adapter);
    }
}
