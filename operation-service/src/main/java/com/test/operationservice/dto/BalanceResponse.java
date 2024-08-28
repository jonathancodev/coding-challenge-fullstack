package com.test.operationservice.dto;

import lombok.Builder;

@Builder
public record BalanceResponse(Double balance) {
}
