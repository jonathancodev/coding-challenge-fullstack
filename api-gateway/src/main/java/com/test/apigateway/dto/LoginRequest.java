package com.test.apigateway.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
        @NotEmpty @Max(50) String username,
        @NotEmpty @Max(255) String password
) {
}
