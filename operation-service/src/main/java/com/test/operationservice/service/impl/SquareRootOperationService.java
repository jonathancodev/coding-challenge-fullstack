package com.test.operationservice.service.impl;

import com.test.operationservice.enums.OperationType;
import com.test.operationservice.service.CalculatorOperation;
import org.springframework.stereotype.Service;

@Service
public class SquareRootOperationService implements CalculatorOperation {

    @Override
    public double calculate(double... operands) {
        return Math.sqrt(operands[0]);
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.SQUARE_ROOT;
    }
}
