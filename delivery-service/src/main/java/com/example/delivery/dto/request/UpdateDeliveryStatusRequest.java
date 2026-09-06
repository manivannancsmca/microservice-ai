// UpdateDeliveryStatusRequest.java
package com.example.delivery.dto.request;

import com.example.delivery.entity.DeliveryStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateDeliveryStatusRequest(
        @NotNull DeliveryStatus status,
        String trackingNumber,
        String failureReason
) {}