package com.ekart.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class PushNotificationService {
    
    private final Random random = new Random();

    public boolean sendPushNotification(String userId, String title, String message) {
        try {
            log.info("Sending push notification to user: {} with title: {}", userId, title);
            
            // Simulate push notification sending delay
            Thread.sleep(200 + random.nextInt(500));
            
            // Simulate 98% success rate
            if (random.nextDouble() < 0.98) {
                log.info("Push notification sent successfully to user: {}", userId);
                return true;
            } else {
                log.error("Push notification sending failed for user: {}", userId);
                return false;
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Push notification sending interrupted for user: {}", userId);
            return false;
        } catch (Exception e) {
            log.error("Failed to send push notification to user: {}", userId, e);
            return false;
        }
    }

    public boolean sendOrderStatusPushNotification(String userId, String orderId, String status) {
        String title = "Order Update";
        String message = String.format("Your order %s is now %s", orderId, status.toLowerCase());
        return sendPushNotification(userId, title, message);
    }

    public boolean sendPaymentStatusPushNotification(String userId, String orderId, String status) {
        String title = "Payment Update";
        String message = String.format("Payment for order %s has been %s", orderId, status.toLowerCase());
        return sendPushNotification(userId, title, message);
    }

    public boolean sendPromotionalPushNotification(String userId, String title, String message) {
        return sendPushNotification(userId, title, message);
    }
}
