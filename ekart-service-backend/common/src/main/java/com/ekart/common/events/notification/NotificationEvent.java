package com.ekart.common.events.notification;

import com.ekart.common.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationEvent extends BaseEvent {
    private String recipient;
    private String subject;
    private String message;
    private String type; // EMAIL, SMS, PUSH
    private String orderId;
}
