package com.test.apigateway.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(String message) {
}
