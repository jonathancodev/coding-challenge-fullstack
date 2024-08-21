package com.test.operationservice.dto;

import com.test.operationservice.enums.OperationType;

public record OperationResponse(Long id, OperationType operationType, Double cost) {
}
