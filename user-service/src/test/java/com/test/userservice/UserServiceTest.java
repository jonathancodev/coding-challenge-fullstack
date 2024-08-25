package com.test.userservice;

import com.test.userservice.dto.UserResponse;
import com.test.userservice.mapper.UserMapper;
import com.test.userservice.model.User;
import com.test.userservice.repository.UserRepository;
import com.test.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private UserMapper userMapper;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() {
		userService = new UserService(userRepository, userMapper);
	}

	@Test
	void testFindByUsername() {
		String username = "testuser";
		User user = new User();
		user.setUsername(username);
		UserResponse userResponse = UserResponse.builder().username(username).build();

		when(userRepository.findOneByUsername(username)).thenReturn(user);
		when(userMapper.toDTO(user)).thenReturn(userResponse);

		UserResponse result = userService.findByUsername(username);

		assertEquals(userResponse, result);
		verify(userRepository).findOneByUsername(username);
		verify(userMapper).toDTO(user);
	}
}

