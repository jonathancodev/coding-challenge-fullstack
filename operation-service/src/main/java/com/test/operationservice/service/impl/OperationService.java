package com.test.operationservice.service.impl;

import com.test.operationservice.client.UserClient;
import com.test.operationservice.dto.OperationRequest;
import com.test.operationservice.dto.OperationResponse;
import com.test.operationservice.dto.UserResponse;
import com.test.operationservice.enums.OperationType;
import com.test.operationservice.factory.impl.CalculatorOperationFactory;
import com.test.operationservice.factory.impl.StringOperationFactory;
import com.test.operationservice.mapper.OperationMapper;
import com.test.operationservice.model.Operation;
import com.test.operationservice.model.Record;
import com.test.operationservice.repository.OperationRepository;
import com.test.operationservice.repository.RecordRepository;
import com.test.operationservice.service.CalculatorOperation;
import com.test.operationservice.service.StringOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;
    private final OperationMapper operationMapper;
    private final CalculatorOperationFactory calculatorOperationFactory;
    private final StringOperationFactory stringOperationFactory;
    private final UserClient userClient;
    private final RecordRepository recordRepository;

    public List<OperationResponse> findAll() {
        return operationMapper.toDTOList(operationRepository.findAll());
    }

    @Transactional
    public OperationResponse execute(String username, OperationRequest operationRequest) {
        Operation operation = operationRepository.findByOperationType(operationRequest.operationType()).orElseThrow(() -> new IllegalArgumentException("Operation not found: " + operationRequest.operationType()));

        var user = userClient.findByUsername(username);
        userBalanceValidation(user, operation.getCost());

        Record record = Record.builder()
                .operation(operation)
                .userId(user.id())
                .amount(operation.getCost())
                .date(LocalDateTime.now())
                .build();

        if (operationRequest.operationType() != OperationType.RANDOM_STRING) {
            CalculatorOperation calculatorOperation = calculatorOperationFactory.getOperation(operationRequest.operationType());
            record.setOperationResponse(String.valueOf(calculatorOperation.calculate(operationRequest.operands())));
        } else {
            StringOperation stringOperation = stringOperationFactory.getOperation(operationRequest.operationType());
        }

        recordRepository.save(record);

        return null;
    }

    private void userBalanceValidation(UserResponse user, double cost) {
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (user.balance() < cost) {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }
}
