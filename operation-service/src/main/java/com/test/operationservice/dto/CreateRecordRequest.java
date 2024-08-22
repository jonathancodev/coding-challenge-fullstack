package com.test.operationservice.dto;

import com.test.operationservice.model.Operation;
import lombok.Builder;

@Builder
public record CreateRecordRequest(Operation operation, Long userId, Double amount, Double userBalance, String operationResponse) {
}
