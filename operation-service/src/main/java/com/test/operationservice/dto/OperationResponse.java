package com.test.operationservice.dto;

import com.test.operationservice.enums.OperationType;
import lombok.Builder;

@Builder
public record OperationResponse(Long id, OperationType operationType, Double cost) {
}
