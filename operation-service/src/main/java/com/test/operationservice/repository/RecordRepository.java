package com.test.operationservice.repository;

import com.test.operationservice.model.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {
    Optional<Record> findFirstByTransactionIdOrderByDateDesc(String transactionId);

    Optional<Record> findFirstByUserIdOrderByDateDesc(Long userId);
}
