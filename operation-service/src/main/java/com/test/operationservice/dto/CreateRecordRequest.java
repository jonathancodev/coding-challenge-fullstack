package com.test.operationservice.dto;

import com.test.operationservice.enums.RecordStatus;
import com.test.operationservice.model.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateRecordRequest(
        @NotEmpty @Max(255) String transactionId,
        @NotNull @Max(1) RecordStatus status,
        @NotNull Operation operation,
        @NotNull Long userId,
        @NotNull Double amount,
        @NotNull Double userBalance,
        @NotEmpty @Max(65535) String operationResponse
) {
}
