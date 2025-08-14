package com.ekart.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class SmsService {
    
    private final Random random = new Random();

    public boolean sendSms(String phoneNumber, String message) {
        try {
            log.info("Sending SMS to: {} with message: {}", phoneNumber, message);
            
            // Simulate SMS sending delay
            Thread.sleep(500 + random.nextInt(1000));
            
            // Simulate 95% success rate
            if (random.nextDouble() < 0.95) {
                log.info("SMS sent successfully to: {}", phoneNumber);
                return true;
            } else {
                log.error("SMS sending failed for: {}", phoneNumber);
                return false;
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("SMS sending interrupted for: {}", phoneNumber);
            return false;
        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", phoneNumber, e);
            return false;
        }
    }

    public boolean sendOrderConfirmationSms(String phoneNumber, String orderId, String customerName) {
        String message = String.format("Hi %s, your order %s has been confirmed. Thank you for shopping with E-Kart!", 
                customerName, orderId);
        return sendSms(phoneNumber, message);
    }

    public boolean sendPaymentConfirmationSms(String phoneNumber, String orderId, String amount) {
        String message = String.format("Payment of $%s for order %s has been processed successfully.", 
                amount, orderId);
        return sendSms(phoneNumber, message);
    }

    public boolean sendOrderCancellationSms(String phoneNumber, String orderId) {
        String message = String.format("Your order %s has been cancelled. If you have any questions, please contact our support.", 
                orderId);
        return sendSms(phoneNumber, message);
    }
}
