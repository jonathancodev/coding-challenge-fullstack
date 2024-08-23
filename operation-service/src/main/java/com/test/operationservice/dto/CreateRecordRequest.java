package com.test.operationservice.dto;

import com.test.operationservice.enums.RecordStatus;
import com.test.operationservice.model.Operation;
import lombok.Builder;

@Builder
public record CreateRecordRequest(String transactionId, RecordStatus status, Operation operation, Long userId, Double amount, Double userBalance, String operationResponse) {
}
