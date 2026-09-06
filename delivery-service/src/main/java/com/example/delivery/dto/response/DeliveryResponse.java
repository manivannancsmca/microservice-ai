// DeliveryResponse.java
package com.example.delivery.dto.response;

import com.example.delivery.entity.DeliveryStatus;
import java.time.Instant;

public record DeliveryResponse(
        String deliveryId,
        String orderId,
        String userId,
        DeliveryStatus status,
        String carrier,
        String trackingNumber,
        String shippingAddress,
        Instant estimatedDelivery,
        Instant deliveredAt,
        String failureReason,
        Instant createdAt,
        Instant updatedAt
) {}