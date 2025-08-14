package com.ekart.notification.controller;

import com.ekart.common.dto.ApiResponse;
import com.ekart.notification.dto.NotificationRequestDto;
import com.ekart.notification.dto.NotificationResponseDto;
import com.ekart.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponseDto>> sendNotification(
            @Valid @RequestBody NotificationRequestDto requestDto) {
        
        log.info("Send notification request for user: {}", requestDto.getUserId());
        
        NotificationResponseDto notification = notificationService.sendNotification(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(notification, "Notification sent successfully"));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getUserNotifications(@PathVariable String userId) {
        log.info("Get notifications request for user: {}", userId);
        
        List<NotificationResponseDto> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success(notifications, "Notifications retrieved successfully"));
    }

    @GetMapping("/{notificationId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponseDto>> getNotificationById(@PathVariable String notificationId) {
        log.info("Get notification request for ID: {}", notificationId);
        
        NotificationResponseDto notification = notificationService.getNotificationById(notificationId);
        return ResponseEntity.ok(ApiResponse.success(notification, "Notification retrieved successfully"));
    }

    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponseDto>> markAsRead(@PathVariable String notificationId) {
        log.info("Mark notification as read request for ID: {}", notificationId);
        
        NotificationResponseDto notification = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success(notification, "Notification marked as read"));
    }
}
