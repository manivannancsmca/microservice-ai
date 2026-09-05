package com.microservice.product.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        String category,
        String brand,
        String status,
        Instant createdAt,
        Instant updatedAt
) {}