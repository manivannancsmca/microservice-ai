package com.example.payment.dto.response;

import com.example.payment.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        String paymentId,
        String orderId,
        String userId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String paymentMethod,
        String transactionRef,
        String failureReason,
        Instant createdAt,
        Instant updatedAt
) {}