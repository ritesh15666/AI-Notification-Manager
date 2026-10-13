package com.example.ai_notification_manager.classifier;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AIClassificationEngine {

    private static AIClassificationEngine instance;

    private AIClassificationEngine() {}

    public static synchronized AIClassificationEngine getInstance() {
        if (instance == null) {
            instance = new AIClassificationEngine();
        }
        return instance;
    }

    public static class MLResult {
        private final String category;
        private final String priority;
        private final String urgency;
        private final float confidence;
        private final int priorityScore;
        private final String reason;

        public MLResult(String category, String priority, String urgency, float confidence, int priorityScore, String reason) {
            this.category = category;
            this.priority = priority;
            this.urgency = urgency;
            this.confidence = confidence;
            this.priorityScore = priorityScore;
            this.reason = reason;
        }

        public String getCategory() { return category; }
        public String getPriority() { return priority; }
        public String getUrgency() { return urgency; }
        public float getConfidence() { return confidence; }
        public int getPriorityScore() { return priorityScore; }
        public String getReason() { return reason; }
    }

    public MLResult runInference(String title, String text, String packageName) {
        String safeTitle = title != null ? title : "";
        String safeText = text != null ? text : "";
        String safePkg = packageName != null ? packageName : "";

        String combined = (safePkg + " " + safeTitle + " " + safeText).toLowerCase(Locale.ROOT);

        // Multi-signal evidence accumulation
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Important", 0);
        scores.put("Study", 0);
        scores.put("Career", 0);
        scores.put("Personal", 0);
        scores.put("Shopping", 0);
        scores.put("Other", 10); // baseline fallback

        StringBuilder reasonBuilder = new StringBuilder();

        // 1. Important signals (Security, Fraud, Critical failure)
        if (combined.contains("fraud") || combined.contains("unauthorized") || combined.contains("suspicious transaction") || combined.contains("security alert: new sign-in")) {
            scores.put("Important", scores.getOrDefault("Important", 0) + 50);
            reasonBuilder.append("Security or fraud warning detected. ");
        }
        if (combined.contains("payment failed") || combined.contains("transaction failed") || combined.contains("debit failed")) {
            scores.put("Important", scores.getOrDefault("Important", 0) + 40);
            reasonBuilder.append("Critical transaction failure identified. ");
        }
        if (combined.contains("otp") || combined.contains("one time password") || combined.contains("verification code")) {
            scores.put("Important", scores.getOrDefault("Important", 0) + 35);
            reasonBuilder.append("Authentication OTP identified. ");
        }

        // 2. Career signals (Job offer letter, interview scheduled, shortlisted)
        if (combined.contains("job offer letter") || combined.contains("interview scheduled") || combined.contains("application shortlisted") || combined.contains("hiring") || combined.contains("placement drive")) {
            scores.put("Career", scores.getOrDefault("Career", 0) + 50);
            reasonBuilder.append("Professional recruitment or interview signal detected. ");
        } else if (combined.contains("interview") || combined.contains("job application") || combined.contains("resume") || combined.contains("offer letter")) {
            scores.put("Career", scores.getOrDefault("Career", 0) + 30);
            reasonBuilder.append("Career opportunity keyword matched. ");
        }

        // 3. Study signals (Assignment submission deadline, exam timetable)
        if (combined.contains("assignment due") || combined.contains("assignment submission") || combined.contains("exam timetable") || combined.contains("semester exam") || combined.contains("course deadline")) {
            scores.put("Study", scores.getOrDefault("Study", 0) + 50);
            reasonBuilder.append("Academic deadline or exam update matched. ");
        } else if (combined.contains("assignment") || combined.contains("exam") || combined.contains("homework") || combined.contains("lecture") || combined.contains("syllabus")) {
            scores.put("Study", scores.getOrDefault("Study", 0) + 30);
            reasonBuilder.append("Study-related coursework keyword matched. ");
        }

        // 4. Shopping signals (Order delivered, refund processed, limited-time sale)
        if (combined.contains("order delivered") || combined.contains("out for delivery") || combined.contains("refund processed") || combined.contains("refund initiated")) {
            scores.put("Shopping", scores.getOrDefault("Shopping", 0) + 40);
            reasonBuilder.append("Order fulfillment or delivery/refund update identified. ");
        } else if (combined.contains("sale") || combined.contains("discount") || combined.contains("offer") || combined.contains("deal") || combined.contains("coupon") || combined.contains("promo")) {
            // Context check: ensure it's not a job offer
            if (!combined.contains("job offer") && !combined.contains("career offer")) {
                scores.put("Shopping", scores.getOrDefault("Shopping", 0) + 35);
                reasonBuilder.append("Commercial promotional offer or discount identified. ");
            }
        }

        // 5. Personal signals (Direct message, missed call, chat)
        if (combined.contains("missed call") || combined.contains("incoming call") || combined.contains("new message from") || combined.contains("chat notification")) {
            scores.put("Personal", scores.getOrDefault("Personal", 0) + 40);
            reasonBuilder.append("Direct personal communication or call detected. ");
        } else if (combined.contains("message") || combined.contains("chat") || combined.contains("dinner") || combined.contains("birthday")) {
            scores.put("Personal", scores.getOrDefault("Personal", 0) + 25);
            reasonBuilder.append("Personal interaction keyword matched. ");
        }

        // Find best category
        String bestCategory = "Other";
        int maxScore = -1;
        int secondMaxScore = -1;

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            int score = entry.getValue();
            if (score > maxScore) {
                secondMaxScore = maxScore;
                maxScore = score;
                bestCategory = entry.getKey();
            } else if (score > secondMaxScore) {
                secondMaxScore = score;
            }
        }

        if (maxScore <= 10) {
            bestCategory = "Other";
            reasonBuilder.append("No strong contextual pattern matched; classified as Other. ");
        }

        // Calculate dynamic confidence based on score margin
        float confidence;
        if (maxScore > secondMaxScore + 20) {
            confidence = 0.94f;
        } else if (maxScore > secondMaxScore + 10) {
            confidence = 0.85f;
        } else if (maxScore > secondMaxScore) {
            confidence = 0.75f;
        } else {
            confidence = 0.60f;
        }

        // Determine priority & urgency
        String priority;
        String urgency;
        int priorityScore;

        switch (bestCategory) {
            case "Important":
                if (combined.contains("fraud") || combined.contains("unauthorized") || combined.contains("critical")) {
                    priority = "Critical";
                    urgency = "Immediate";
                    priorityScore = 98;
                } else {
                    priority = "High";
                    urgency = "Soon";
                    priorityScore = 90;
                }
                break;
            case "Career":
            case "Study":
                priority = "High";
                urgency = "Soon";
                priorityScore = 85;
                break;
            case "Personal":
                priority = "Medium";
                urgency = "When convenient";
                priorityScore = 55;
                break;
            case "Shopping":
                if (combined.contains("delivered") || combined.contains("refund")) {
                    priority = "Medium";
                    urgency = "When convenient";
                    priorityScore = 50;
                } else {
                    priority = "Low";
                    urgency = "When convenient";
                    priorityScore = 20;
                }
                break;
            default:
                priority = "Low";
                urgency = "When convenient";
                priorityScore = 25;
                break;
        }

        String finalReason = reasonBuilder.length() > 0 ? reasonBuilder.toString().trim() : ("Classified as " + bestCategory + " based on multi-signal text analysis.");

        return new MLResult(bestCategory, priority, urgency, confidence, priorityScore, finalReason);
    }
}
