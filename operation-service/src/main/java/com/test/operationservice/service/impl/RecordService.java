package com.test.operationservice.service.impl;

import com.test.operationservice.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;
}
