package com.test.operationservice.service.impl;

import com.test.operationservice.enums.OperationType;
import com.test.operationservice.service.CalculatorOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SquareRootOperationService implements CalculatorOperation {
    private final MessageSource messageSource;

    @Override
    public double calculate(double... operands) {
        if (operands[0] < 0) throw new IllegalArgumentException(messageSource.getMessage("operation.square.root.negative", null, null));
        return Math.sqrt(operands[0]);
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.SQUARE_ROOT;
    }
}
