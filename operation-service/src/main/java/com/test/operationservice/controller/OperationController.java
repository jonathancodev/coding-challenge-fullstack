package com.test.operationservice.controller;

import com.test.operationservice.dto.BalanceResponse;
import com.test.operationservice.dto.OperationRequest;
import com.test.operationservice.dto.OperationResponse;
import com.test.operationservice.dto.PaginationRecordRequest;
import com.test.operationservice.dto.RecordResponse;
import com.test.operationservice.dto.ResultOperationResponse;
import com.test.operationservice.service.impl.OperationService;
import com.test.operationservice.service.impl.RecordService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operations")
@RequiredArgsConstructor
@Slf4j
public class OperationController {
    private final OperationService operationService;
    private final RecordService recordService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OperationResponse> findAll() {
        return operationService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultOperationResponse execute(@RequestHeader("X-Username") String username, @Valid @RequestBody OperationRequest operationRequest) {
        return operationService.execute(username, operationRequest);
    }

    @GetMapping("/records")
    @ResponseStatus(HttpStatus.OK)
    public Page<RecordResponse> search(
            @RequestHeader("X-Username") String username,
            @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
            @RequestParam(required = false, defaultValue = "10") @Min(1) int size,
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "") String term
    ) {
        PaginationRecordRequest paginationRecordRequest =
                PaginationRecordRequest.builder()
                        .page(page)
                        .size(size)
                        .sortDirection(sortDirection)
                        .sortBy(sortBy)
                        .term(term)
                        .build();
        return recordService.search(username, paginationRecordRequest);
    }

    @GetMapping("/records/balance")
    @ResponseStatus(HttpStatus.OK)
    public BalanceResponse getCurrentBalance(@RequestHeader("X-Username") String username) {
        return recordService.getCurrentBalance(username);
    }

    @PatchMapping("/records/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void logicalDelete(@RequestHeader("X-Username") String username, @PathVariable("id") Long id) {
        recordService.logicalDelete(username, id);
    }
}
