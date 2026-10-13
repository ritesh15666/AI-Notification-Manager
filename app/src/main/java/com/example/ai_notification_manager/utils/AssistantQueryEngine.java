package com.example.ai_notification_manager.utils;

import com.example.ai_notification_manager.classifier.NotificationPriorityEngine;
import com.example.ai_notification_manager.model.AssistantMessageModel;
import com.example.ai_notification_manager.model.NotificationModel;
import com.example.ai_notification_manager.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AssistantQueryEngine {

    public static AssistantMessageModel processQuery(String queryText) {
        if (queryText == null || queryText.trim().isEmpty()) {
            return new AssistantMessageModel("Please ask a question about your notifications (e.g., 'Show unread notifications', 'Did I receive payment updates?', 'WhatsApp').", false, null);
        }

        String q = queryText.toLowerCase(Locale.ROOT);
        NotificationRepository repo = NotificationRepository.getInstance();
        List<NotificationModel> allNotifications = repo != null ? repo.getAllNotifications() : new ArrayList<>();

        List<NotificationModel> results = new ArrayList<>(allNotifications);

        // Intent: Unread
        if (q.contains("unread")) {
            results = results.stream().filter(n -> !n.isRead()).collect(Collectors.toList());
        }

        // Intent: Priority (high / critical)
        if (q.contains("high priority") || q.contains("critical") || q.contains("important")) {
            results = results.stream().filter(n -> {
                NotificationPriorityEngine.PriorityResult p = NotificationPriorityEngine.evaluate(n.getTitle(), n.getMessage(), n.getPackageName(), n.getCategory());
                return p.getLevel().equals("CRITICAL") || p.getLevel().equals("HIGH") || n.isImportant();
            }).collect(Collectors.toList());
        }

        // Intent: App filters (WhatsApp, Gmail, YouTube, etc.)
        if (q.contains("whatsapp")) {
            results = results.stream().filter(n -> n.getSourceApp().toLowerCase(Locale.ROOT).contains("whatsapp") || (n.getPackageName() != null && n.getPackageName().contains("whatsapp"))).collect(Collectors.toList());
        } else if (q.contains("gmail") || q.contains("email")) {
            results = results.stream().filter(n -> n.getSourceApp().toLowerCase(Locale.ROOT).contains("gmail") || n.getSourceApp().toLowerCase(Locale.ROOT).contains("email") || (n.getPackageName() != null && n.getPackageName().contains("gm"))).collect(Collectors.toList());
        } else if (q.contains("youtube")) {
            results = results.stream().filter(n -> n.getSourceApp().toLowerCase(Locale.ROOT).contains("youtube") || (n.getPackageName() != null && n.getPackageName().contains("youtube"))).collect(Collectors.toList());
        } else if (q.contains("payment") || q.contains("transaction") || q.contains("credited") || q.contains("debited")) {
            results = results.stream().filter(n -> n.getTitle().toLowerCase(Locale.ROOT).contains("payment") || n.getMessage().toLowerCase(Locale.ROOT).contains("credited") || n.getMessage().toLowerCase(Locale.ROOT).contains("debited") || n.getMessage().toLowerCase(Locale.ROOT).contains("rs") || n.getMessage().toLowerCase(Locale.ROOT).contains("₹")).collect(Collectors.toList());
        } else if (q.contains("assignment") || q.contains("study") || q.contains("exam")) {
            results = results.stream().filter(n -> n.getCategory().equalsIgnoreCase("Study") || n.getTitle().toLowerCase(Locale.ROOT).contains("assignment") || n.getTitle().toLowerCase(Locale.ROOT).contains("exam")).collect(Collectors.toList());
        } else if (q.contains("career") || q.contains("job") || q.contains("interview")) {
            results = results.stream().filter(n -> n.getCategory().equalsIgnoreCase("Career") || n.getTitle().toLowerCase(Locale.ROOT).contains("interview") || n.getTitle().toLowerCase(Locale.ROOT).contains("job")).collect(Collectors.toList());
        }

        // If generic text search query
        if (!q.contains("unread") && !q.contains("high priority") && !q.contains("whatsapp") && !q.contains("gmail") && !q.contains("youtube") && !q.contains("payment") && !q.contains("assignment") && !q.contains("career")) {
            results = results.stream().filter(n -> n.getTitle().toLowerCase(Locale.ROOT).contains(q) || n.getMessage().toLowerCase(Locale.ROOT).contains(q) || n.getSourceApp().toLowerCase(Locale.ROOT).contains(q)).collect(Collectors.toList());
        }

        if (results.isEmpty()) {
            return new AssistantMessageModel("I couldn't find any matching notifications in your history.", false, null);
        }

        String responseText = String.format("Found %d matching notification(s) based on your request:", results.size());
        return new AssistantMessageModel(responseText, false, results);
    }
}
