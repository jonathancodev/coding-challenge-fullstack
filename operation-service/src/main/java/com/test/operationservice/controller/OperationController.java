package com.test.operationservice.controller;

import com.test.operationservice.model.Operation;
import com.test.operationservice.service.impl.OperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/operations")
@RequiredArgsConstructor
@Slf4j
public class OperationController {
    private final OperationService operationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Operation> findAll() {
        return operationService.findAll();
    }
}
