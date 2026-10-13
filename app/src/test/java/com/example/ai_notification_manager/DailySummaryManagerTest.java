package com.example.ai_notification_manager;

import com.example.ai_notification_manager.model.NotificationModel;
import com.example.ai_notification_manager.utils.DailySummaryManager;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DailySummaryManagerTest {

    @Test
    public void testDailySummaryStats_emptyList() {
        List<NotificationModel> list = new ArrayList<>();
        DailySummaryManager.SummaryStats stats = DailySummaryManager.generateStats(list);
        assertEquals(0, stats.getTotalCount());
        assertEquals(0, stats.getUnreadCount());
        assertEquals(0, stats.getCriticalOrHighCount());
    }

    @Test
    public void testDailySummaryStats_populatedList() {
        List<NotificationModel> list = new ArrayList<>();
        list.add(new NotificationModel("1", "Payment", "Rs 500 credited", "Bank", "Important", "High", "1m ago", false, true));
        list.add(new NotificationModel("2", "Assignment", "Due tomorrow", "Portal", "Study", "Normal", "10m ago", true, false));

        DailySummaryManager.SummaryStats stats = DailySummaryManager.generateStats(list);
        assertEquals(2, stats.getTotalCount());
        assertEquals(1, stats.getUnreadCount());
        assertEquals(1, stats.getImportantCount());
        assertEquals(1, stats.getStudyCount());
    }
}
