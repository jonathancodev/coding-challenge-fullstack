package com.test.operationservice.factories;

import com.test.operationservice.enums.OperationType;

public interface OperationFactory<T> {
    T getOperation(OperationType operationType);
}
