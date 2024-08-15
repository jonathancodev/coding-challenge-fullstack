package com.test.userservice.service;

import com.test.userservice.dto.UserResponse;
import com.test.userservice.mapper.UserMapper;
import com.test.userservice.model.User;
import com.test.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse findByUsername(String username) {
        return userMapper.toDTO(userRepository.findOneByUsername(username));
    }
}
