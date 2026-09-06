package com.example.order.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateOrderItemRequest(

        @NotNull(message = "Product ID is required")
        Long productId,

        @NotBlank(message = "Product name is required")
        @Size(max = 200)
        String productName,

        @NotBlank(message = "Product SKU is required")
        @Size(max = 50)
        String productSku,

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal unitPrice,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {}