package com.test.operationservice.factory;

import com.test.operationservice.enums.OperationType;

public interface OperationFactory<T> {
    T getOperation(OperationType operationType);
}
