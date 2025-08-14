package com.ekart.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@RedisHash("notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private String recipient;
    
    private String subject;
    
    private String message;
    
    private NotificationType type;
    
    private NotificationStatus status;
    
    private String orderId;
    
    private String sagaId;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime sentAt;
    
    private String errorMessage;
    
    private int retryCount = 0;
    
    public enum NotificationType {
        EMAIL,
        SMS,
        PUSH,
        IN_APP
    }
    
    public enum NotificationStatus {
        PENDING,
        SENT,
        FAILED,
        DELIVERED
    }
}
