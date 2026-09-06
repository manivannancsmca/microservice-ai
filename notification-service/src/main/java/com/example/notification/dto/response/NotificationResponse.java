// NotificationResponse.java
package com.example.notification.dto.response;

import com.example.notification.entity.NotificationChannel;
import com.example.notification.entity.NotificationStatus;
import com.example.notification.entity.NotificationType;
import java.time.Instant;

public record NotificationResponse(
        String notificationId,
        String userId,
        String orderId,
        NotificationType type,
        NotificationChannel channel,
        String recipient,
        String subject,
        String content,
        NotificationStatus status,
        String failureReason,
        Instant sentAt,
        Instant createdAt,
        Instant updatedAt
) {}