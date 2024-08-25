package com.test.operationservice.service.impl;

import com.test.operationservice.enums.OperationType;
import com.test.operationservice.service.CalculatorOperation;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class MultiplicationOperationService implements CalculatorOperation {

    @Override
    public double calculate(double... operands) {
        return Arrays.stream(operands, 1, operands.length).reduce(operands[0], (a, b) -> a * b);
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.MULTIPLICATION;
    }
}
