package com.example.ai_notification_manager.utils;

import android.app.Notification;
import android.os.Bundle;
import android.os.Parcelable;
import android.service.notification.StatusBarNotification;

public class NotificationContentExtractor {

    public static class ExtractedData {
        private final String title;
        private final String message;
        private final String summaryText;
        private final String conversationTitle;
        private final boolean hasReadableText;

        public ExtractedData(String title, String message, String summaryText, String conversationTitle, boolean hasReadableText) {
            this.title = title;
            this.message = message;
            this.summaryText = summaryText;
            this.conversationTitle = conversationTitle;
            this.hasReadableText = hasReadableText;
        }

        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public String getSummaryText() { return summaryText; }
        public String getConversationTitle() { return conversationTitle; }
        public boolean isHasReadableText() { return hasReadableText; }
    }

    public static ExtractedData extract(StatusBarNotification sbn) {
        if (sbn == null || sbn.getNotification() == null) {
            return new ExtractedData("Notification", "New notification received", null, null, false);
        }

        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        if (extras == null) {
            return new ExtractedData("Notification", "New notification received", null, null, false);
        }

        CharSequence titleSeq = extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence textSeq = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence bigTextSeq = extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
        CharSequence summarySeq = extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT);
        CharSequence convTitleSeq = extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE);

        String title = titleSeq != null ? titleSeq.toString().trim() : "";
        if (title.isEmpty()) {
            title = convTitleSeq != null ? convTitleSeq.toString().trim() : "Notification";
        }

        String message = "";
        if (bigTextSeq != null && !bigTextSeq.toString().trim().isEmpty()) {
            message = bigTextSeq.toString().trim();
        } else if (textSeq != null && !textSeq.toString().trim().isEmpty()) {
            message = textSeq.toString().trim();
        }

        // Check InboxStyle text lines if message is still empty
        if (message.isEmpty()) {
            CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
            if (lines != null && lines.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (CharSequence line : lines) {
                    if (line != null && !line.toString().trim().isEmpty()) {
                        if (sb.length() > 0) sb.append("\n");
                        sb.append(line.toString().trim());
                    }
                }
                message = sb.toString();
            }
        }

        // Check MessagingStyle messages
        if (message.isEmpty()) {
            try {
                Parcelable[] messages = extras.getParcelableArray(Notification.EXTRA_MESSAGES);
                if (messages != null && messages.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (Parcelable p : messages) {
                        if (p instanceof Bundle) {
                            Bundle msgBundle = (Bundle) p;
                            String sender = msgBundle.getString("sender");
                            CharSequence text = msgBundle.getCharSequence("text");
                            if (text != null && !text.toString().trim().isEmpty()) {
                                if (sb.length() > 0) sb.append("\n");
                                if (sender != null && !sender.trim().isEmpty()) {
                                    sb.append(sender).append(": ");
                                }
                                sb.append(text.toString().trim());
                            }
                        }
                    }
                    message = sb.toString();
                }
            } catch (Exception ignored) {}
        }

        boolean hasReadableText = !message.isEmpty() || !title.equals("Notification");
        if (message.isEmpty()) {
            message = summarySeq != null ? summarySeq.toString().trim() : "This notification has no readable text.";
        }

        String summaryText = summarySeq != null ? summarySeq.toString().trim() : null;
        String convTitle = convTitleSeq != null ? convTitleSeq.toString().trim() : null;

        return new ExtractedData(title, message, summaryText, convTitle, hasReadableText);
    }
}
