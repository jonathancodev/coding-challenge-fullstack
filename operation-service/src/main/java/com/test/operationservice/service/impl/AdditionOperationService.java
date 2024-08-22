package com.test.operationservice.service.impl;

import com.test.operationservice.enums.OperationType;
import com.test.operationservice.service.CalculatorOperation;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AdditionOperationService implements CalculatorOperation {

    @Override
    public double calculate(double... operands) {
        return Arrays.stream(operands).sum();
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ADDITION;
    }
}
