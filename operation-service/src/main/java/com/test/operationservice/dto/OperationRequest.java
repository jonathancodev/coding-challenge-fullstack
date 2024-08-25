package com.test.operationservice.dto;

import com.test.operationservice.enums.OperationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OperationRequest(
        @NotEmpty @Size(max = 255) String transactionId,
        @NotNull @Size(max = 1) OperationType operationType,
        @NotEmpty double[] operands
) {
}
