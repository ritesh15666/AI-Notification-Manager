package com.example.ai_notification_manager.utils;

import com.example.ai_notification_manager.classifier.NotificationPriorityEngine;
import com.example.ai_notification_manager.model.NotificationModel;

import java.util.List;

public class DailySummaryManager {

    public static class SummaryStats {
        private final int totalCount;
        private final int unreadCount;
        private final int criticalOrHighCount;
        private final int importantCount;
        private final int studyCount;
        private final int careerCount;
        private final int personalCount;
        private final int shoppingCount;
        private final int otherCount;

        public SummaryStats(List<NotificationModel> list) {
            int total = 0;
            int unread = 0;
            int critHigh = 0;
            int imp = 0, study = 0, career = 0, personal = 0, shopping = 0, other = 0;

            if (list != null) {
                total = list.size();
                for (NotificationModel n : list) {
                    if (!n.isRead()) {
                        unread++;
                    }
                    NotificationPriorityEngine.PriorityResult pResult = NotificationPriorityEngine.evaluate(
                            n.getTitle(), n.getMessage(), n.getPackageName(), n.getCategory()
                    );
                    if (pResult.getLevel().equals("CRITICAL") || pResult.getLevel().equals("HIGH")) {
                        critHigh++;
                    }

                    String cat = n.getCategory() != null ? n.getCategory().toLowerCase() : "other";
                    switch (cat) {
                        case "important": imp++; break;
                        case "study": study++; break;
                        case "career": career++; break;
                        case "personal": personal++; break;
                        case "shopping": shopping++; break;
                        default: other++; break;
                    }
                }
            }

            this.totalCount = total;
            this.unreadCount = unread;
            this.criticalOrHighCount = critHigh;
            this.importantCount = imp;
            this.studyCount = study;
            this.careerCount = career;
            this.personalCount = personal;
            this.shoppingCount = shopping;
            this.otherCount = other;
        }

        public int getTotalCount() { return totalCount; }
        public int getUnreadCount() { return unreadCount; }
        public int getCriticalOrHighCount() { return criticalOrHighCount; }
        public int getImportantCount() { return importantCount; }
        public int getStudyCount() { return studyCount; }
        public int getCareerCount() { return careerCount; }
        public int getPersonalCount() { return personalCount; }
        public int getShoppingCount() { return shoppingCount; }
        public int getOtherCount() { return otherCount; }
    }

    public static SummaryStats generateStats(List<NotificationModel> notifications) {
        return new SummaryStats(notifications);
    }
}
