package com.test.userservice.dto;

import com.test.userservice.enums.UserStatus;
import lombok.Builder;

@Builder
public record UserResponse(Long id, String username, String password, UserStatus status) {
}
