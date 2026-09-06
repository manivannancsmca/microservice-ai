package com.example.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateOrderStatusRequest(

        @NotBlank(message = "Status is required")
        @Pattern(regexp = "PENDING|CONFIRMED|CANCELLED|COMPLETED",
                 message = "Status must be PENDING, CONFIRMED, CANCELLED or COMPLETED")
        String status
) {}