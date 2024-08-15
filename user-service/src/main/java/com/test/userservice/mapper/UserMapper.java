package com.test.userservice.mapper;

import com.test.userservice.dto.UserResponse;
import com.test.userservice.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toDTO(User user);

    User toEntity(UserResponse userResponse);
}
