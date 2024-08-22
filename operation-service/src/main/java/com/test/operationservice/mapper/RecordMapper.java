package com.test.operationservice.mapper;

import com.test.operationservice.dto.CreateRecordRequest;
import com.test.operationservice.dto.RecordResponse;
import com.test.operationservice.model.Record;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecordMapper {
    Record toEntity(CreateRecordRequest createRecordRequest);

    RecordResponse toDTO(Record record);
}
