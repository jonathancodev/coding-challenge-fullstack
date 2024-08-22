package com.test.operationservice.service.impl;

import com.test.operationservice.dto.CreateRecordRequest;
import com.test.operationservice.dto.RecordResponse;
import com.test.operationservice.mapper.RecordMapper;
import com.test.operationservice.model.Record;
import com.test.operationservice.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;
    private final RecordMapper recordMapper;

    public RecordResponse findByTransactionId(String transactionId) {
        return recordMapper.toDTO(recordRepository.findFirstByTransactionIdOrderByDateDesc(transactionId).orElse(null));
    }

    public RecordResponse findLastRecordByUserId(Long userId) {
        return recordMapper.toDTO(recordRepository.findFirstByUserIdOrderByDateDesc(userId).orElse(null));
    }

    @Transactional
    public RecordResponse create(CreateRecordRequest createRecordRequest) {
        Record record = recordMapper.toEntity(createRecordRequest);
        record.setDate(LocalDateTime.now());
        return recordMapper.toDTO(recordRepository.save(record));
    }
}
