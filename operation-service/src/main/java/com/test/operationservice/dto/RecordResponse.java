package com.test.operationservice.dto;

import com.test.operationservice.enums.OperationType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RecordResponse(Long id, OperationType operationType, Double amount, Double userBalance, String operationResponse, LocalDateTime date) {
}
