package com.example.payment.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotBlank String orderId,
        @NotBlank String userId,
        @NotNull @Positive BigDecimal amount,
        @NotBlank @Size(min = 3, max = 3) String currency,
        String paymentMethod
) {}