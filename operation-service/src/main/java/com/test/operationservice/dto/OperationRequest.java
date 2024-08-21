package com.test.operationservice.dto;

import com.test.operationservice.enums.OperationType;

public record OperationRequest(OperationType operationType, double[] operands) {
}
