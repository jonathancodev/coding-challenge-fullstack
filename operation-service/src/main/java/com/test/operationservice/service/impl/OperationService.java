package com.test.operationservice.service.impl;

import com.test.operationservice.dto.OperationRequest;
import com.test.operationservice.dto.OperationResponse;
import com.test.operationservice.enums.OperationType;
import com.test.operationservice.factory.impl.CalculatorOperationFactory;
import com.test.operationservice.factory.impl.StringOperationFactory;
import com.test.operationservice.mapper.OperationMapper;
import com.test.operationservice.model.Operation;
import com.test.operationservice.model.Record;
import com.test.operationservice.repository.OperationRepository;
import com.test.operationservice.service.CalculatorOperation;
import com.test.operationservice.service.StringOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;
    private final OperationMapper operationMapper;
    private final CalculatorOperationFactory calculatorOperationFactory;
    private final StringOperationFactory stringOperationFactory;

    public List<OperationResponse> findAll() {
        return operationMapper.toDTOList(operationRepository.findAll());
    }

    public OperationResponse execute(OperationRequest operationRequest) {
        Record record = new Record();
        if (operationRequest.operationType() != OperationType.RANDOM_STRING) {
            CalculatorOperation calculatorOperation = calculatorOperationFactory.getOperation(operationRequest.operationType());
            Operation operation = calculatorOperation.getDbData();
            record.setOperation(calculatorOperation.getDbData());
            record.setAmount(operation.getCost());
            record.setOperationResponse(String.valueOf(calculatorOperation.calculate(operationRequest.operands())));
        } else {
            StringOperation stringOperation = stringOperationFactory.getOperation(operationRequest.operationType());
        }

        record.setDate(LocalDateTime.now());
        return null;
    }
}
