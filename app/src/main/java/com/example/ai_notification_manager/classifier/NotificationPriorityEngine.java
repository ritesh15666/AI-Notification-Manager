package com.example.ai_notification_manager.classifier;

import java.util.Locale;

public class NotificationPriorityEngine {

    public static class PriorityResult {
        private final String level; // CRITICAL, HIGH, MEDIUM, LOW
        private final int score; // 0 - 100
        private final String reason;

        public PriorityResult(String level, int score, String reason) {
            this.level = level;
            this.score = score;
            this.reason = reason;
        }

        public String getLevel() { return level; }
        public int getScore() { return score; }
        public String getReason() { return reason; }
    }

    public static PriorityResult evaluate(String title, String message, String packageName, String category) {
        String safeTitle = title != null ? title.toLowerCase(Locale.ROOT) : "";
        String safeMsg = message != null ? message.toLowerCase(Locale.ROOT) : "";
        String safePkg = packageName != null ? packageName.toLowerCase(Locale.ROOT) : "";
        String safeCat = category != null ? category.toLowerCase(Locale.ROOT) : "";
        String combined = safePkg + " " + safeTitle + " " + safeMsg;

        // 1. CRITICAL: Fraud warnings, unauthorized transactions, security alerts
        if (combined.contains("fraud") || combined.contains("unauthorized") || combined.contains("suspicious activity") || combined.contains("scam") || combined.contains("security alert") || combined.contains("suspicious login") || combined.contains("account locked")) {
            return new PriorityResult("CRITICAL", 95, "Critical priority: matched security or fraud alert rule.");
        }

        // 2. HIGH: Payment transactions, OTP / authentication, deadlines, interviews, important category
        if (combined.contains("debited") || combined.contains("credited") || combined.contains("payment received") || combined.contains("paid") || combined.contains("refund") || combined.contains("statement") || combined.contains("otp") || combined.contains("verification code") || combined.contains("exam") || combined.contains("assignment due") || combined.contains("deadline") || combined.contains("interview") || combined.contains("job offer")) {
            return new PriorityResult("HIGH", 85, "High priority: payment, OTP, deadline, or career notification.");
        }
        if (safeCat.equals("important")) {
            return new PriorityResult("HIGH", 80, "High priority: designated important category.");
        }

        // 3. LOW: Promotional offers, marketing, discounts, deals
        if (combined.contains("discount") || combined.contains("sale") || combined.contains("offer") || combined.contains("coupon") || combined.contains("save") || combined.contains("cashback") || combined.contains("deal") || safeCat.equals("shopping")) {
            return new PriorityResult("LOW", 25, "Low priority: promotional or commercial offer content.");
        }

        // 4. MEDIUM: Personal messages, routine updates, study, career, delivery
        if (safeCat.equals("personal") || safeCat.equals("study") || safeCat.equals("career") || combined.contains("message") || combined.contains("chat") || combined.contains("delivery")) {
            return new PriorityResult("MEDIUM", 60, "Medium priority: personal communication or routine update.");
        }

        // Default fallback: Medium
        return new PriorityResult("MEDIUM", 50, "Medium priority: standard notification update.");
    }
}
