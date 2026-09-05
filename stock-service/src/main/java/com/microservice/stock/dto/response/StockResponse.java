package com.microservice.stock.dto.response;

import java.time.Instant;

public record StockResponse(
        Long id,
        Long productId,
        Integer availableQuantity,
        Integer reservedQuantity,
        Instant createdAt,
        Instant updatedAt
) {}