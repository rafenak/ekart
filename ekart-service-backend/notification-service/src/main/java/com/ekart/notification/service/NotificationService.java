package com.ekart.notification.service;

import com.ekart.common.events.notification.NotificationEvent;
import com.ekart.notification.dto.NotificationRequestDto;
import com.ekart.notification.dto.NotificationResponseDto;
import com.ekart.notification.entity.Notification;
import com.ekart.notification.repository.NotificationRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final PushNotificationService pushNotificationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Async
    @CircuitBreaker(name = "notification-service", fallbackMethod = "sendNotificationFallback")
    @Retry(name = "notification-service")
    public NotificationResponseDto sendNotification(NotificationRequestDto requestDto) {
        log.info("Sending notification to: {} with type: {}", requestDto.getRecipient(), requestDto.getType());
        
        // Create notification record
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID().toString());
        notification.setUserId(requestDto.getUserId());
        notification.setRecipient(requestDto.getRecipient());
        notification.setSubject(requestDto.getSubject());
        notification.setMessage(requestDto.getMessage());
        notification.setType(requestDto.getType());
        notification.setStatus(Notification.NotificationStatus.PENDING);
        notification.setOrderId(requestDto.getOrderId());
        
        notification = notificationRepository.save(notification);
        
        try {
            // Send notification based on type
            boolean sent = false;
            switch (requestDto.getType()) {
                case EMAIL:
                    sent = emailService.sendEmail(requestDto.getRecipient(), requestDto.getSubject(), requestDto.getMessage());
                    break;
                case SMS:
                    sent = smsService.sendSms(requestDto.getRecipient(), requestDto.getMessage());
                    break;
                case PUSH:
                    sent = pushNotificationService.sendPushNotification(requestDto.getUserId(), requestDto.getSubject(), requestDto.getMessage());
                    break;
                case IN_APP:
                    sent = true; // In-app notifications are stored in Redis
                    break;
            }
            
            if (sent) {
                notification.setStatus(Notification.NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                log.info("Notification sent successfully to: {}", requestDto.getRecipient());
            } else {
                notification.setStatus(Notification.NotificationStatus.FAILED);
                notification.setErrorMessage("Failed to send notification");
                log.error("Failed to send notification to: {}", requestDto.getRecipient());
            }
            
        } catch (Exception e) {
            log.error("Error sending notification to: {}", requestDto.getRecipient(), e);
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
        }
        
        notification = notificationRepository.save(notification);
        return convertToDto(notification);
    }

    @KafkaListener(topics = "notification-topic")
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("Received notification event for user: {} with type: {}", event.getUserId(), event.getType());
        
        try {
            // Create notification request
            NotificationRequestDto requestDto = new NotificationRequestDto();
            requestDto.setUserId(event.getUserId());
            requestDto.setRecipient(event.getRecipient());
            requestDto.setSubject(event.getSubject());
            requestDto.setMessage(event.getMessage());
            requestDto.setType(Notification.NotificationType.valueOf(event.getType()));
            requestDto.setOrderId(event.getOrderId());
            
            // Create notification record
            Notification notification = new Notification();
            notification.setId(UUID.randomUUID().toString());
            notification.setUserId(event.getUserId());
            notification.setRecipient(event.getRecipient());
            notification.setSubject(event.getSubject());
            notification.setMessage(event.getMessage());
            notification.setType(Notification.NotificationType.valueOf(event.getType()));
            notification.setStatus(Notification.NotificationStatus.PENDING);
            notification.setOrderId(event.getOrderId());
            notification.setSagaId(event.getSagaId());
            
            notification = notificationRepository.save(notification);
            
            // Send notification
            boolean sent = false;
            switch (notification.getType()) {
                case EMAIL:
                    sent = emailService.sendEmail(notification.getRecipient(), notification.getSubject(), notification.getMessage());
                    break;
                case SMS:
                    sent = smsService.sendSms(notification.getRecipient(), notification.getMessage());
                    break;
                case PUSH:
                    sent = pushNotificationService.sendPushNotification(notification.getUserId(), notification.getSubject(), notification.getMessage());
                    break;
                case IN_APP:
                    sent = true; // In-app notifications are stored in Redis
                    break;
            }
            
            if (sent) {
                notification.setStatus(Notification.NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                
                // Publish notification sent event for saga
                if (notification.getSagaId() != null) {
                    NotificationEvent sentEvent = new NotificationEvent();
                    sentEvent.setEventId(UUID.randomUUID().toString());
                    sentEvent.setEventType("NOTIFICATION_SENT");
                    sentEvent.setTimestamp(LocalDateTime.now());
                    sentEvent.setSagaId(notification.getSagaId());
                    sentEvent.setUserId(notification.getUserId());
                    sentEvent.setOrderId(notification.getOrderId());
                    sentEvent.setRecipient(notification.getRecipient());
                    sentEvent.setType(notification.getType().name());
                    
                    kafkaTemplate.send("notification-sent-topic", sentEvent);
                }
                
                log.info("Notification sent successfully to: {}", notification.getRecipient());
            } else {
                notification.setStatus(Notification.NotificationStatus.FAILED);
                notification.setErrorMessage("Failed to send notification");
                log.error("Failed to send notification to: {}", notification.getRecipient());
            }
            
            notificationRepository.save(notification);
            
        } catch (Exception e) {
            log.error("Error processing notification event", e);
        }
    }

    @CircuitBreaker(name = "notification-service", fallbackMethod = "getUserNotificationsFallback")
    @Retry(name = "notification-service")
    public List<NotificationResponseDto> getUserNotifications(String userId) {
        log.info("Retrieving notifications for user: {}", userId);
        
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(this::convertToDto).toList();
    }

    @CircuitBreaker(name = "notification-service", fallbackMethod = "getNotificationByIdFallback")
    @Retry(name = "notification-service")
    public NotificationResponseDto getNotificationById(String notificationId) {
        log.info("Retrieving notification with ID: {}", notificationId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        
        return convertToDto(notification);
    }

    @CircuitBreaker(name = "notification-service", fallbackMethod = "markAsReadFallback")
    @Retry(name = "notification-service")
    public NotificationResponseDto markAsRead(String notificationId) {
        log.info("Marking notification as read: {}", notificationId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        
        notification.setStatus(Notification.NotificationStatus.DELIVERED);
        notification = notificationRepository.save(notification);
        
        return convertToDto(notification);
    }

    @Scheduled(fixedDelay = 300000) // Every 5 minutes
    public void retryFailedNotifications() {
        log.info("Retrying failed notifications...");
        
        List<Notification> failedNotifications = notificationRepository
                .findByStatusAndRetryCountLessThan(Notification.NotificationStatus.FAILED, 3);
        
        for (Notification notification : failedNotifications) {
            try {
                log.info("Retrying notification: {}", notification.getId());
                
                boolean sent = false;
                switch (notification.getType()) {
                    case EMAIL:
                        sent = emailService.sendEmail(notification.getRecipient(), notification.getSubject(), notification.getMessage());
                        break;
                    case SMS:
                        sent = smsService.sendSms(notification.getRecipient(), notification.getMessage());
                        break;
                    case PUSH:
                        sent = pushNotificationService.sendPushNotification(notification.getUserId(), notification.getSubject(), notification.getMessage());
                        break;
                    case IN_APP:
                        sent = true;
                        break;
                }
                
                notification.setRetryCount(notification.getRetryCount() + 1);
                
                if (sent) {
                    notification.setStatus(Notification.NotificationStatus.SENT);
                    notification.setSentAt(LocalDateTime.now());
                    notification.setErrorMessage(null);
                    log.info("Notification retry successful: {}", notification.getId());
                } else {
                    notification.setErrorMessage("Retry failed");
                    log.error("Notification retry failed: {}", notification.getId());
                }
                
                notificationRepository.save(notification);
                
            } catch (Exception e) {
                log.error("Error retrying notification: {}", notification.getId(), e);
                notification.setRetryCount(notification.getRetryCount() + 1);
                notification.setErrorMessage(e.getMessage());
                notificationRepository.save(notification);
            }
        }
    }

    private NotificationResponseDto convertToDto(Notification notification) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setRecipient(notification.getRecipient());
        dto.setSubject(notification.getSubject());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setStatus(notification.getStatus());
        dto.setOrderId(notification.getOrderId());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setSentAt(notification.getSentAt());
        dto.setErrorMessage(notification.getErrorMessage());
        dto.setRetryCount(notification.getRetryCount());
        return dto;
    }

    // Fallback methods
    public NotificationResponseDto sendNotificationFallback(NotificationRequestDto requestDto, Exception ex) {
        log.error("Circuit breaker activated for send notification: {}", ex.getMessage());
        throw new RuntimeException("Notification service is temporarily unavailable");
    }

    public List<NotificationResponseDto> getUserNotificationsFallback(String userId, Exception ex) {
        log.error("Circuit breaker activated for get user notifications: {}", ex.getMessage());
        throw new RuntimeException("Notification retrieval service is temporarily unavailable");
    }

    public NotificationResponseDto getNotificationByIdFallback(String notificationId, Exception ex) {
        log.error("Circuit breaker activated for get notification by ID: {}", ex.getMessage());
        throw new RuntimeException("Notification retrieval service is temporarily unavailable");
    }

    public NotificationResponseDto markAsReadFallback(String notificationId, Exception ex) {
        log.error("Circuit breaker activated for mark as read: {}", ex.getMessage());
        throw new RuntimeException("Notification update service is temporarily unavailable");
    }
}
