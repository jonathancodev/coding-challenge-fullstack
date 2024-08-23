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
import com.test.operationservice.mapper.OperationMapper;
import com.test.operationservice.model.Operation;
import com.test.operationservice.repository.OperationRepository;
import com.test.operationservice.service.CalculatorOperation;
import com.test.operationservice.service.StringOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;
    private final OperationMapper operationMapper;
    private final CalculatorOperationFactory calculatorOperationFactory;
    private final StringOperationFactory stringOperationFactory;
    private final UserClient userClient;
    private final RecordService recordService;

    @Value("${default.user.balance}")
    private Double defaultUserBalance;

    public List<OperationResponse> findAll() {
        return operationMapper.toDTOList(operationRepository.findAll());
    }

    @Transactional
    public ResultOperationResponse execute(String username, OperationRequest operationRequest) {
        RecordResponse recordResponse = recordService.findByTransactionId(operationRequest.transactionId());

        if (recordResponse != null) return ResultOperationResponse.builder().result(recordResponse.operationResponse()).build();

        var user = userClient.findByUsername(username);
        if (user == null) throw new IllegalArgumentException("User not found");

        var userBalance = defaultUserBalance;
        RecordResponse lastRecord = recordService.findLastRecordByUserId(user.id());
        if (lastRecord != null) userBalance = lastRecord.userBalance();

        Operation operation = operationRepository.findByOperationType(operationRequest.operationType()).orElseThrow(() -> new IllegalArgumentException("Operation not found"));
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
    }

    private void userValidation(UserResponse user, double userBalance, double cost) {
        if (user.status() == UserStatus.INACTIVE) {
            throw new IllegalArgumentException("User is inactive");
        } else if (userBalance < cost) {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }
}
