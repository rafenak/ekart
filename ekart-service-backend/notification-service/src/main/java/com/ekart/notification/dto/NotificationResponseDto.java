package com.ekart.notification.dto;

import com.ekart.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    
    private String id;
    private String userId;
    private String recipient;
    private String subject;
    private String message;
    private Notification.NotificationType type;
    private Notification.NotificationStatus status;
    private String orderId;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private String errorMessage;
    private int retryCount;
}
