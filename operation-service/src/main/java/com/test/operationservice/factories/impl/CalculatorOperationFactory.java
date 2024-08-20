package com.test.operationservice.factories.impl;

import com.test.operationservice.enums.OperationType;
import com.test.operationservice.service.CalculatorOperation;
import com.test.operationservice.factories.OperationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class CalculatorOperationFactory implements OperationFactory<CalculatorOperation> {
    private final Map<OperationType, CalculatorOperation> operations;

    @Autowired
    public CalculatorOperationFactory(List<CalculatorOperation> operationList) {
        operations = new EnumMap<>(OperationType.class);

        for (CalculatorOperation operation : operationList) {
            operations.put(operation.getOperationType(), operation);
        }
        operations.remove(OperationType.RANDOM_STRING);
    }

    @Override
    public CalculatorOperation getOperation(OperationType operationType) {
        return operations.get(operationType);
    }
}

