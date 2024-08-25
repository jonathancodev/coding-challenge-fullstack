package com.test.operationservice.dto;

import com.test.operationservice.enums.RecordStatus;
import com.test.operationservice.model.Operation;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateRecordRequest(
        @NotEmpty @Size(max = 255) String transactionId,
        @NotNull @Size(max = 1) RecordStatus status,
        @NotNull Operation operation,
        @NotNull Long userId,
        @NotNull Double amount,
        @NotNull Double userBalance,
        @NotEmpty @Size(max = 65535) String operationResponse
) {
}
