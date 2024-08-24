package com.test.operationservice.dto;

import com.test.operationservice.enums.OperationType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OperationRequest(
        @NotEmpty @Max(255) String transactionId,
        @NotNull @Max(1) OperationType operationType,
        @NotEmpty double[] operands
) {
}
