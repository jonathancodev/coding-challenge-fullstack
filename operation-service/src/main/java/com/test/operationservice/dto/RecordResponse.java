package com.test.operationservice.dto;

import com.test.operationservice.model.Operation;

import java.time.LocalDateTime;

public record RecordResponse(Long id, Operation operation, Long userId, Double amount, Double userBalance, String operationResponse, LocalDateTime date) {
}
