package com.test.operationservice.service.impl;

import com.test.operationservice.enums.OperationType;
import com.test.operationservice.service.CalculatorOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class DivisionOperationService implements CalculatorOperation {
    private final MessageSource messageSource;

    @Override
    public double calculate(double... operands) {
        return Arrays.stream(operands).reduce(0, (a, b) -> {
            if (b == 0) throw new IllegalArgumentException(messageSource.getMessage("operation.division.by.zero", null, null));
            return a / b;
        });
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.DIVISION;
    }
}
