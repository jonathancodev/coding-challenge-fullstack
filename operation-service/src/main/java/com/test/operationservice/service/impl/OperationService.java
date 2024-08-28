package com.test.operationservice.service.impl;

import com.test.operationservice.client.UserClient;
import com.test.operationservice.dto.CreateRecordRequest;
import com.test.operationservice.dto.OperationRequest;
import com.test.operationservice.dto.OperationResponse;
import com.test.operationservice.dto.RecordResponse;
import com.test.operationservice.dto.ResultOperationResponse;
import com.test.operationservice.dto.UserResponse;
import com.test.operationservice.enums.OperationType;
import com.test.operationservice.enums.RecordStatus;
import com.test.operationservice.enums.UserStatus;
import com.test.operationservice.factory.impl.CalculatorOperationFactory;
import com.test.operationservice.factory.impl.StringOperationFactory;
import com.test.operationservice.model.Operation;
import com.test.operationservice.repository.OperationRepository;
import com.test.operationservice.service.CalculatorOperation;
import com.test.operationservice.service.StringOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;
    private final CalculatorOperationFactory calculatorOperationFactory;
    private final StringOperationFactory stringOperationFactory;
    private final UserClient userClient;
    private final RecordService recordService;
    private final MessageSource messageSource;

    private final Set<String> activeTransactions = ConcurrentHashMap.newKeySet();

    @Value("${default.user.balance}")
    private Double defaultUserBalance;

    @Transactional(readOnly = true)
    public List<OperationResponse> findAll() {
        List<Operation> operations = operationRepository.findAll();
        return operations.stream().map(operation -> OperationResponse
                .builder()
                .id(operation.getId())
                .operationType(operation.getOperationType())
                .cost(operation.getCost())
                .build()).toList();
    }

    @Transactional
    public ResultOperationResponse execute(String username, OperationRequest operationRequest) {
        try {
            //TODO Idempotency: we can check if transaction is already started in server memory,
            // however it has to store and check into a memory DB(Redis), when we have many replicas of the backend
            if (!activeTransactions.add(operationRequest.transactionId())) {
                throw new IllegalStateException(messageSource.getMessage("transaction.already.started", null, null));
            }

            var user = userClient.findByUsername(username);
            if (user == null)
                throw new IllegalArgumentException(messageSource.getMessage("user.not.found", null, null));

            var userBalance = defaultUserBalance;
            RecordResponse lastRecord = recordService.findLastRecordByUserId(user.id());
            if (lastRecord != null) userBalance = lastRecord.userBalance();

            Operation operation = operationRepository.findByOperationType(operationRequest.operationType()).orElseThrow(() -> new IllegalArgumentException(messageSource.getMessage("operation.not.found", null, null)));
            userValidation(user, userBalance, operation.getCost());
            String operationResponse;

            if (operationRequest.operationType() != OperationType.RANDOM_STRING) {
                CalculatorOperation calculatorOperation = calculatorOperationFactory.getOperation(operationRequest.operationType());
                operationResponse = String.valueOf(calculatorOperation.calculate(operationRequest.operands()));
            } else {
                StringOperation stringOperation = stringOperationFactory.getOperation(operationRequest.operationType());
                operationResponse = stringOperation.generate();
            }

            CreateRecordRequest createRecordRequest = CreateRecordRequest.builder()
                    .transactionId(operationRequest.transactionId())
                    .status(RecordStatus.ACTIVE)
                    .operation(operation)
                    .userId(user.id())
                    .amount(operation.getCost())
                    .userBalance(userBalance - operation.getCost())
                    .operationResponse(operationResponse)
                    .build();

            recordService.create(createRecordRequest);

            return ResultOperationResponse.builder().result(operationResponse).build();
        } finally {
            activeTransactions.remove(operationRequest.transactionId());
        }
    }

    private void userValidation(UserResponse user, double userBalance, double cost) {
        if (user.status() == UserStatus.INACTIVE) {
            throw new IllegalArgumentException(messageSource.getMessage("user.inactive", null, null));
        } else if (userBalance < cost) {
            throw new IllegalArgumentException(messageSource.getMessage("user.insufficient.balance", null, null));
        }
    }
}
