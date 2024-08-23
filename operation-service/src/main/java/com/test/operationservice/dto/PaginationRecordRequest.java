package com.test.operationservice.dto;

import lombok.Builder;

@Builder
public record PaginationRecordRequest(int page, int size, String sortDirection, String sortBy, String term) {
}
