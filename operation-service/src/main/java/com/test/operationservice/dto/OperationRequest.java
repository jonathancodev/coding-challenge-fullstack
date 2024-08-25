package com.test.operationservice.dto;

import com.test.operationservice.enums.OperationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OperationRequest(
        @NotBlank @Size(max = 255) String transactionId,
        @NotNull OperationType operationType,
        @NotEmpty double[] operands
) {
}
