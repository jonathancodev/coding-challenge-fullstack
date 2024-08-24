package com.test.operationservice.service.impl;

import com.test.operationservice.client.UserClient;
import com.test.operationservice.dto.CreateRecordRequest;
import com.test.operationservice.dto.PaginationRecordRequest;
import com.test.operationservice.dto.RecordResponse;
import com.test.operationservice.enums.RecordStatus;
import com.test.operationservice.mapper.RecordMapper;
import com.test.operationservice.model.Record;
import com.test.operationservice.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public RecordResponse create(CreateRecordRequest createRecordRequest) {
        Record record = recordMapper.toEntity(createRecordRequest);
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

    public RecordResponse findLastRecordByUserId(Long userId) {
        return recordMapper.toDTO(recordRepository.findFirstByUserIdOrderByDateDesc(userId).orElse(null));
    }

    public Page<RecordResponse> search(PaginationRecordRequest paginationRecordRequest) {
        Pageable pageable = PageRequest.of(
                paginationRecordRequest.page(),
                paginationRecordRequest.size(),
                Sort.Direction.fromString(paginationRecordRequest.sortDirection()),
                paginationRecordRequest.sortBy()
        );

        Page<Record> records = recordRepository.search(paginationRecordRequest.term(), pageable);

        return records.map(recordMapper::toDTO);
    }
}
