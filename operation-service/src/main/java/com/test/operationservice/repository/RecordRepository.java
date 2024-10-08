package com.test.operationservice.repository;

import com.test.operationservice.model.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {
    Optional<Record> findFirstByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT r FROM Record r WHERE r.userId = :userId AND r.status = 1 AND " +
            "(LOWER(STR(r.amount)) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(r.operationResponse) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(STR(r.operation.operationType)) LIKE LOWER(CONCAT('%', :term, '%')))")
    Page<Record> search(@Param("term") String term, @Param("userId") Long userId, Pageable pageable);
}