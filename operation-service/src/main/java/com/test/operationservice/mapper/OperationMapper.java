package com.test.operationservice.mapper;

import com.test.operationservice.dto.OperationResponse;
import com.test.operationservice.model.Operation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OperationMapper {

    OperationResponse toDTO(Operation operation);

    List<OperationResponse> toDTOList(List<Operation> operations);
}
