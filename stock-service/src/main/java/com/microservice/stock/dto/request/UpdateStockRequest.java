package com.microservice.stock.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateStockRequest(

        @NotNull(message = "Available quantity is required")
        @Min(value = 0, message = "Available quantity cannot be negative")
        Integer availableQuantity,

        @NotNull(message = "Reserved quantity is required")
        @Min(value = 0, message = "Reserved quantity cannot be negative")
        Integer reservedQuantity
) {}