package com.test.apigateway.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotEmpty @Size(max = 50) String username,
        @NotEmpty @Size(max = 255) String password
) {
}
