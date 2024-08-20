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
public class DivisionOperationService implements CalculatorOperation {
    private final OperationRepository operationRepository;

    @Override
    public double calculate(double... operands) {
        return Arrays.stream(operands).reduce(0, (a, b) -> a / b);
    }

    @Override
    public double getCost() {
        return operationRepository.findByOperationType(getOperationType())
                .map(Operation::getCost)
                .orElseThrow(() -> new IllegalArgumentException("Cost not found for operation: " + getOperationType()));
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.DIVISION;
    }
}
