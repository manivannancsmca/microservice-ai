// CreateDeliveryRequest.java
package com.example.delivery.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateDeliveryRequest(
        @NotBlank String orderId,
        @NotBlank String userId,
        @NotBlank String shippingAddress,
        String carrier,
        String trackingNumber
) {}