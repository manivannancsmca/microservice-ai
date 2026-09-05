package com.microservice.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record StatusUpdateRequest(

        @NotBlank(message = "Status is required")
        @Pattern(regexp = "ACTIVE|INACTIVE|DISCONTINUED",
                 message = "Status must be ACTIVE, INACTIVE or DISCONTINUED")
        String status
) {}