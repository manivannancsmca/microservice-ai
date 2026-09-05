package com.microservice.product.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank(message = "SKU is required")
        @Size(max = 50)
        String sku,

        @NotBlank(message = "Name is required")
        @Size(max = 200)
        String name,

        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Price must be >= 0")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @NotBlank(message = "Category is required")
        @Size(max = 100)
        String category,

        @Size(max = 100)
        String brand
) {}