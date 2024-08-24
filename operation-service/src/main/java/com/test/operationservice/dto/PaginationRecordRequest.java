package com.test.operationservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record PaginationRecordRequest(
        @Min(1) int page,
        @Min(1) int size,
        @Max(4) String sortDirection,
        @Max(255) String sortBy,
        @Max(255) String term
) {
}
