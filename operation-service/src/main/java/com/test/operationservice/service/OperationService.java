package com.test.operationservice.service;

import com.test.operationservice.model.Operation;
import com.test.operationservice.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;

    public List<Operation> findAll() {
        return operationRepository.findAll();
    }
}
