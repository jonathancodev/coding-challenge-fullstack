package com.test.operationservice.dto;

import com.test.operationservice.enums.UserStatus;

public record UserResponse(Long id, String username, String password, UserStatus status) {
}
