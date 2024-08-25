package com.test.apigateway.dto;

import com.test.apigateway.enums.UserStatus;
import lombok.Builder;

@Builder
public record UserResponse(Long id, String username, String password, UserStatus status, Double balance) {
}
