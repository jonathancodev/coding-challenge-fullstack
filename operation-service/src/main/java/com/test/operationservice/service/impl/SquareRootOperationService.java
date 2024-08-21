package com.test.operationservice.service.impl;

import com.test.operationservice.enums.OperationType;
import com.test.operationservice.model.Operation;
import com.test.operationservice.repository.OperationRepository;
import com.test.operationservice.service.CalculatorOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SquareRootOperationService implements CalculatorOperation {
    private final OperationRepository operationRepository;

    @Override
    public double calculate(double... operands) {
        return Math.sqrt(operands[0]);
    }

    @Override
    public Operation getDbData() {
        return operationRepository.findByOperationType(getOperationType())
                .orElseThrow(() -> new IllegalArgumentException("Cost not found for operation: " + getOperationType()));
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.SQUARE_ROOT;
    }
}
