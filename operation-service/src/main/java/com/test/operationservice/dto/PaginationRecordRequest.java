package com.test.operationservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PaginationRecordRequest(
        @Min(1) int page,
        @Min(1) int size,
        @Size(max = 4) String sortDirection,
        @Size(max = 255) String sortBy,
        @Size(max = 255) String term
) {
}
