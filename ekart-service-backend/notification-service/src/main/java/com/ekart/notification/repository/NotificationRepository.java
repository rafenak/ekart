package com.ekart.notification.repository;

import com.ekart.notification.entity.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, String> {
    
    List<Notification> findByUserId(String userId);
    
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    
    List<Notification> findByStatus(Notification.NotificationStatus status);
    
    List<Notification> findByType(Notification.NotificationType type);
    
    List<Notification> findByOrderId(String orderId);
    
    List<Notification> findBySagaId(String sagaId);
    
    List<Notification> findByStatusAndRetryCountLessThan(Notification.NotificationStatus status, int maxRetries);
}
