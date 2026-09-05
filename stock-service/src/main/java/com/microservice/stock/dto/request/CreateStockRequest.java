package com.microservice.stock.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateStockRequest(

        @NotNull(message = "Product ID is required")
        Long productId,

        @NotNull(message = "Available quantity is required")
        @Min(value = 0, message = "Available quantity cannot be negative")
        Integer availableQuantity
) {}