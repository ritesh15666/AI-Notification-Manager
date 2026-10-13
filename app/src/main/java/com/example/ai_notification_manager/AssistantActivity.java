package com.example.ai_notification_manager;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ai_notification_manager.adapter.AssistantAdapter;
import com.example.ai_notification_manager.model.AssistantMessageModel;
import com.example.ai_notification_manager.utils.AssistantQueryEngine;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class AssistantActivity extends AppCompatActivity {

    private AssistantAdapter adapter;
    private final List<AssistantMessageModel> messageList = new ArrayList<>();
    private RecyclerView rvChat;
    private EditText etMessageInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);

        initViews();
        setupRecyclerView();
        setupListeners();

        // Add welcome message
        messageList.add(new AssistantMessageModel("Hello! I'm your AI notification assistant. Ask me anything about your notification history or tap a suggestion below.", false, null));
        adapter.notifyDataSetChanged();
    }

    private void initViews() {
        ImageView ivBack = findViewById(R.id.ivBack);
        ImageView ivClearChat = findViewById(R.id.ivClearChat);
        rvChat = findViewById(R.id.rvChat);
        etMessageInput = findViewById(R.id.etMessageInput);
        MaterialButton btnSend = findViewById(R.id.btnSend);

        ivBack.setOnClickListener(v -> finish());
        ivClearChat.setOnClickListener(v -> {
            messageList.clear();
            messageList.add(new AssistantMessageModel("Conversation cleared. How can I help you?", false, null));
            adapter.notifyDataSetChanged();
        });

        btnSend.setOnClickListener(v -> sendUserMessage());

        Chip chipUnread = findViewById(R.id.chipUnread);
        Chip chipPayment = findViewById(R.id.chipPayment);
        Chip chipWhatsApp = findViewById(R.id.chipWhatsApp);
        Chip chipHighPriority = findViewById(R.id.chipHighPriority);

        chipUnread.setOnClickListener(v -> submitQuery("Show unread notifications"));
        chipPayment.setOnClickListener(v -> submitQuery("Find payment alerts"));
        chipWhatsApp.setOnClickListener(v -> submitQuery("WhatsApp messages"));
        chipHighPriority.setOnClickListener(v -> submitQuery("High priority items"));
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);
        adapter = new AssistantAdapter(messageList);
        rvChat.setAdapter(adapter);
    }

    private void setupListeners() {
        // Additional setup if needed
    }

    private void sendUserMessage() {
        String text = etMessageInput.getText().toString().trim();
        if (!text.isEmpty()) {
            submitQuery(text);
            etMessageInput.setText("");
        }
    }

    private void submitQuery(String query) {
        // Add user message
        messageList.add(new AssistantMessageModel(query, true, null));
        adapter.notifyItemInserted(messageList.size() - 1);
        rvChat.scrollToPosition(messageList.size() - 1);

        // Process query via engine
        AssistantMessageModel response = AssistantQueryEngine.processQuery(query);
        messageList.add(response);
        adapter.notifyItemInserted(messageList.size() - 1);
        rvChat.scrollToPosition(messageList.size() - 1);
    }
}
