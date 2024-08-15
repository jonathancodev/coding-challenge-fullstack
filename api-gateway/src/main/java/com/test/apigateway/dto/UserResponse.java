package com.test.apigateway.dto;

import com.test.apigateway.enums.UserStatus;

import java.math.BigDecimal;

public record UserResponse(Long id, String username, String password, UserStatus status, BigDecimal balance) {
}
