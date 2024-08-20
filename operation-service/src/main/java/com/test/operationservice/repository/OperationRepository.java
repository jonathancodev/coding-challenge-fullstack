package com.test.operationservice.repository;

import com.test.operationservice.enums.OperationType;
import com.test.operationservice.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperationRepository extends JpaRepository<Operation, Long> {
    Optional<Operation> findByOperationType(OperationType operationType);
}
