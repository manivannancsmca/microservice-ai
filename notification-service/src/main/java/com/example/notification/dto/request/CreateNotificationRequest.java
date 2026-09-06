// CreateNotificationRequest.java
package com.example.notification.dto.request;

import com.example.notification.entity.NotificationChannel;
import com.example.notification.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateNotificationRequest(
        @NotBlank String userId,
        String orderId,
        @NotNull NotificationType type,
        @NotNull NotificationChannel channel,
        @NotBlank String recipient,
        String subject,
        @NotBlank String content
) {}