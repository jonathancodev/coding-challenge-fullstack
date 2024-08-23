package com.test.operationservice.service.impl;

import com.test.operationservice.client.RandomClient;
import com.test.operationservice.enums.OperationType;
import com.test.operationservice.service.StringOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RandomStringOperationService implements StringOperation {
    private final RandomClient randomClient;

    @Override
    public String generate() {
        return randomClient.generateRandomString();
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.RANDOM_STRING;
    }
}
