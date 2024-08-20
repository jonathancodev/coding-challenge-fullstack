package com.test.operationservice.service.impl;

import com.test.operationservice.enums.OperationType;
import com.test.operationservice.model.Operation;
import com.test.operationservice.repository.OperationRepository;
import com.test.operationservice.service.CalculatorOperation;
import com.test.operationservice.service.StringOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RandomStringOperationService implements StringOperation {
    private final OperationRepository operationRepository;

    @Override
    public String generate() {
        return "";
    }

    @Override
    public double getCost() {
        return operationRepository.findByOperationType(getOperationType())
                .map(Operation::getCost)
                .orElseThrow(() -> new IllegalArgumentException("Cost not found for operation: " + getOperationType()));
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.RANDOM_STRING;
    }
}
