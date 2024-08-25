package com.test.operationservice.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(String message) {
}
