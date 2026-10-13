package com.example.ai_notification_manager;

import com.example.ai_notification_manager.classifier.NotificationPriorityEngine;

import org.junit.Test;

import static org.junit.Assert.*;

public class NotificationPriorityEngineTest {

    @Test
    public void testCriticalPriority_fraudAlert() {
        NotificationPriorityEngine.PriorityResult result = NotificationPriorityEngine.evaluate(
                "Security Alert",
                "Suspicious login detected from an unauthorized device.",
                "com.google.android.googlequicksearchbox",
                "Important"
        );
        assertEquals("CRITICAL", result.getLevel());
        assertTrue(result.getScore() >= 90);
    }

    @Test
    public void testHighPriority_paymentConfirmation() {
        NotificationPriorityEngine.PriorityResult result = NotificationPriorityEngine.evaluate(
                "Payment Received",
                "₹5,000 has been credited to your account.",
                "com.bank.app",
                "Important"
        );
        assertEquals("HIGH", result.getLevel());
        assertTrue(result.getScore() >= 70 && result.getScore() < 90);
    }

    @Test
    public void testLowPriority_promotionalOffer() {
        NotificationPriorityEngine.PriorityResult result = NotificationPriorityEngine.evaluate(
                "Weekend Sale",
                "Get 50% off on all fashion items with coupon SAVE50.",
                "com.amazon.mShop.android.shopping",
                "Shopping"
        );
        assertEquals("LOW", result.getLevel());
        assertTrue(result.getScore() < 40);
    }

    @Test
    public void testMediumPriority_personalMessage() {
        NotificationPriorityEngine.PriorityResult result = NotificationPriorityEngine.evaluate(
                "Alex",
                "Hey! Are we meeting up for coffee?",
                "com.whatsapp",
                "Personal"
        );
        assertEquals("MEDIUM", result.getLevel());
        assertTrue(result.getScore() >= 40 && result.getScore() < 70);
    }
}
