// UpdateNotificationStatusRequest.java
package com.example.notification.dto.request;

import com.example.notification.entity.NotificationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateNotificationStatusRequest(
        @NotNull NotificationStatus status,
        String failureReason
) {}