package com.test.apigateway.dto;

import com.test.apigateway.enums.UserStatus;

public record UserResponse(Long id, String username, String password, UserStatus status, Double balance) {
}
