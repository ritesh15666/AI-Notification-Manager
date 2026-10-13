package com.example.ai_notification_manager.classifier;

import android.content.Context;

import com.example.ai_notification_manager.db.RuleDatabaseHelper;
import com.example.ai_notification_manager.model.RuleModel;
import com.example.ai_notification_manager.repository.NotificationRepository;

import java.util.List;
import java.util.Locale;

public class NotificationClassifier {

    private static NotificationClassifier instance;

    private NotificationClassifier() {}

    public static synchronized NotificationClassifier getInstance() {
        if (instance == null) {
            instance = new NotificationClassifier();
        }
        return instance;
    }

    public static class ClassificationResult {
        private final String category;
        private final String priority;
        private final String urgency;
        private final float confidence;
        private final int priorityScore;
        private final String reason;
        private final String classificationSource;
        private final String modelVersion;

        public ClassificationResult(String category, String priority, String urgency, float confidence, int priorityScore, String reason, String classificationSource, String modelVersion) {
            this.category = category;
            this.priority = priority;
            this.urgency = urgency;
            this.confidence = confidence;
            this.priorityScore = priorityScore;
            this.reason = reason;
            this.classificationSource = classificationSource;
            this.modelVersion = modelVersion;
        }

        public String getCategory() { return category; }
        public String getPriority() { return priority; }
        public String getUrgency() { return urgency; }
        public float getConfidence() { return confidence; }
        public int getPriorityScore() { return priorityScore; }
        public String getReason() { return reason; }
        public String getClassificationSource() { return classificationSource; }
        public String getModelVersion() { return modelVersion; }
    }

    public ClassificationResult classify(String title, String text, String packageName) {
        String safeTitle = title != null ? title : "";
        String safeText = text != null ? text : "";
        String safePkg = packageName != null ? packageName : "";

        String normalizedTitle = normalizeText(safeTitle);
        String normalizedText = normalizeText(safeText);
        String combined = (safePkg + " " + normalizedTitle + " " + normalizedText).toLowerCase(Locale.ROOT);

        // 1. Check User-Defined Priority Rules first
        try {
            Context ctx = NotificationRepository.getAppContext();
            if (ctx != null) {
                List<RuleModel> rules = RuleDatabaseHelper.getInstance(ctx).getAllRules();
                for (RuleModel rule : rules) {
                    if (rule.isEnabled() && combined.contains(rule.getKeyword().toLowerCase(Locale.ROOT))) {
                        int score = rule.getTargetPriority().equalsIgnoreCase("Critical") ? 98 : (rule.getTargetPriority().equalsIgnoreCase("High") ? 88 : 50);
                        return new ClassificationResult(
                                rule.getTargetCategory(),
                                rule.getTargetPriority(),
                                "Soon",
                                0.99f,
                                score,
                                "Matched user-defined custom rule: " + rule.getRuleName(),
                                "USER_RULE",
                                "Rule_v2.0"
                        );
                    }
                }
            }
        } catch (Exception ignored) {}

        // 2. Run AI ML Model inference
        AIClassificationEngine.MLResult mlResult = AIClassificationEngine.getInstance().runInference(safeTitle, safeText, safePkg);

        if (mlResult.getConfidence() >= 0.80f) {
            return new ClassificationResult(
                    mlResult.getCategory(),
                    mlResult.getPriority(),
                    mlResult.getUrgency(),
                    mlResult.getConfidence(),
                    mlResult.getPriorityScore(),
                    mlResult.getReason() + " (Hybrid ML Inference)",
                    "HYBRID",
                    "ML_v1.0"
            );
        }

        // 3. Safety rule fallback
        if (combined.contains("fraud") || combined.contains("suspicious")) {
            return new ClassificationResult(
                    "Important",
                    "Critical",
                    "Immediate",
                    0.95f,
                    98,
                    "Marked Important by rule engine due to security keywords.",
                    "RULE_ENGINE",
                    "Rule_v1.0"
            );
        }

        return new ClassificationResult(
                mlResult.getCategory(),
                mlResult.getPriority(),
                mlResult.getUrgency(),
                mlResult.getConfidence(),
                mlResult.getPriorityScore(),
                mlResult.getReason() + " (Fallback Engine)",
                "FALLBACK",
                "ML_v1.0"
        );
    }

    public String normalizeText(String text) {
        if (text == null) return "";
        return text.replaceAll("\\s+", " ").trim();
    }
}
