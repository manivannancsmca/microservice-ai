package com.example.payment.dto.request;

import com.example.payment.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentStatusRequest(
        @NotNull PaymentStatus status,
        String transactionRef,
        String failureReason
) {}