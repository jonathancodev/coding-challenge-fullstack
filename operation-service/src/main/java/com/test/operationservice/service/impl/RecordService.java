package com.test.operationservice.service.impl;

import com.test.operationservice.client.UserClient;
import com.test.operationservice.dto.BalanceResponse;
import com.test.operationservice.dto.CreateRecordRequest;
import com.test.operationservice.dto.PaginationRecordRequest;
import com.test.operationservice.dto.RecordResponse;
import com.test.operationservice.enums.RecordStatus;
import com.test.operationservice.mapper.RecordMapper;
import com.test.operationservice.model.Record;
import com.test.operationservice.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;
    private final RecordMapper recordMapper;
    private final UserClient userClient;
    private final MessageSource messageSource;

    @Value("${default.user.balance}")
    private Double defaultUserBalance;

    @Transactional
    public RecordResponse create(CreateRecordRequest createRecordRequest) {
        Record record = Record
                .builder()
                .transactionId(createRecordRequest.transactionId())
                .status(createRecordRequest.status())
                .operation(createRecordRequest.operation())
                .userId(createRecordRequest.userId())
                .amount(createRecordRequest.amount())
                .userBalance(createRecordRequest.userBalance())
                .operationResponse(createRecordRequest.operationResponse())
                .build();

        record.setDate(LocalDateTime.now());
        return recordMapper.toDTO(recordRepository.save(record));
    }

    @Transactional
    public void logicalDelete(String username, Long id) {
        Record record = recordRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(messageSource.getMessage("record.not.found", null, null)));
        var user = userClient.findByUsername(username);
        if (user == null) throw new IllegalArgumentException(messageSource.getMessage("user.not.found", null, null));
        if (!record.getUserId().equals(user.id())) throw new IllegalArgumentException(messageSource.getMessage("record.wrong.user", null, null));
        record.setStatus(RecordStatus.INACTIVE);
        recordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public RecordResponse findLastRecordByUserId(Long userId) {
        Record record = recordRepository.findFirstByUserIdOrderByDateDesc(userId).orElse(null);
        if (record == null) return null;
        return RecordResponse
                .builder()
                .id(record.getId())
                .operationType(record.getOperation().getOperationType())
                .userBalance(record.getUserBalance())
                .amount(record.getOperation().getCost())
                .operationResponse(record.getOperationResponse())
                .date(record.getDate())
                .build();
    }

    @Transactional(readOnly = true)
    public BalanceResponse getCurrentBalance(String username) {
        var user = userClient.findByUsername(username);
        if (user == null) throw new IllegalArgumentException(messageSource.getMessage("user.not.found", null, null));
        var balance = defaultUserBalance;
        RecordResponse lastRecord = findLastRecordByUserId(user.id());
        if (lastRecord != null) balance = lastRecord.userBalance();
        return BalanceResponse
                .builder()
                .balance(balance)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<RecordResponse> search(String username, PaginationRecordRequest paginationRecordRequest) {
        var user = userClient.findByUsername(username);
        if (user == null) throw new IllegalArgumentException(messageSource.getMessage("user.not.found", null, null));
        Pageable pageable = PageRequest.of(
                paginationRecordRequest.page(),
                paginationRecordRequest.size(),
                Sort.Direction.fromString(paginationRecordRequest.sortDirection()),
                paginationRecordRequest.sortBy()
        );

        Page<Record> records = recordRepository.search(paginationRecordRequest.term(), user.id(), pageable);

        return records.map(record -> RecordResponse.builder()
                .id(record.getId())
                .operationType(record.getOperation().getOperationType())
                .amount(record.getOperation().getCost())
                .userBalance(record.getUserBalance())
                .operationResponse(record.getOperationResponse())
                .date(record.getDate())
                .build()
        );
    }
}
