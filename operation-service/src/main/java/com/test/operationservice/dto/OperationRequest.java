package com.test.operationservice.dto;

import com.test.operationservice.enums.OperationType;

public record OperationRequest(String transactionId, OperationType operationType, double[] operands) {
}
