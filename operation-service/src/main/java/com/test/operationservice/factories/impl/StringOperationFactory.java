package com.test.operationservice.factories.impl;

import com.test.operationservice.enums.OperationType;
import com.test.operationservice.factories.OperationFactory;
import com.test.operationservice.service.StringOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class StringOperationFactory implements OperationFactory<StringOperation> {
    private final Map<OperationType, StringOperation> operations;

    @Autowired
    public StringOperationFactory(List<StringOperation> generatorList) {
        operations = new EnumMap<>(OperationType.class);

        for (StringOperation generator : generatorList) {
            operations.put(generator.getOperationType(), generator);
        }
    }

    @Override
    public StringOperation getOperation(OperationType operationType) {
        return operations.get(operationType);
    }
}

