package com.test.apigateway;

import com.test.apigateway.client.UserClient;
import com.test.apigateway.dto.UserResponse;
import com.test.apigateway.service.AuthService;
import com.test.apigateway.service.PasswordService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

@SpringBootTest
class AuthServiceTest {

	@InjectMocks
	private AuthService authService;

	@Mock
	private PasswordService passwordService;

	@Mock
	private UserClient userClient;

	@Autowired
	private MessageSource messageSource;

	private static final String SECRET_KEY = "test_secret_key";
	private static final String DEFAULT_USERNAME = "user";
	private static final String DEFAULT_PASSWORD = "password";

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		authService = new AuthService(passwordService, userClient, messageSource);
		try {
			var field = AuthService.class.getDeclaredField("SECRET_KEY");
			field.setAccessible(true);
			field.set(authService, SECRET_KEY);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void login_UserNotFound_ShouldThrowException() {
		when(userClient.findByUsername(DEFAULT_USERNAME)).thenReturn(null);

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			authService.login(DEFAULT_USERNAME, DEFAULT_PASSWORD);
		});

		assertEquals(messageSource.getMessage("auth.wrong.user.password", null, null), exception.getMessage());
	}

	@Test
	void login_InvalidPassword_ShouldThrowException() {
		UserResponse user = UserResponse.builder().password("hashed_password").build();

		when(userClient.findByUsername(DEFAULT_USERNAME)).thenReturn(user);
		when(passwordService.matches(DEFAULT_PASSWORD, "hashed_password")).thenReturn(false);

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			authService.login(DEFAULT_USERNAME, DEFAULT_PASSWORD);
		});

		assertEquals(messageSource.getMessage("auth.wrong.user.password", null, null), exception.getMessage());
	}

	@Test
	void login_ValidCredentials_ShouldReturnJwtToken() {
		UserResponse user = UserResponse.builder().password("hashed_password").build();

		when(userClient.findByUsername(DEFAULT_USERNAME)).thenReturn(user);
		when(passwordService.matches(DEFAULT_PASSWORD, "hashed_password")).thenReturn(true);

		String token = authService.login(DEFAULT_USERNAME, DEFAULT_PASSWORD);

		assertNotNull(token);
		assertTrue(token.length() > 0);
	}

	@Test
	void validateToken_ValidToken_ShouldReturnTrue() {
		String token = Jwts.builder()
				.setSubject(DEFAULT_USERNAME)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 86400000))
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY)
				.compact();

		boolean isValid = authService.validateToken(token);

		assertTrue(isValid);
	}

	@Test
	void validateToken_InvalidToken_ShouldReturnFalse() {
		String invalidToken = "invalid_token";

		boolean isValid = authService.validateToken(invalidToken);

		assertFalse(isValid);
	}

	@Test
	void getUserNameFromJwtToken_ShouldReturnUsername() {
		String token = Jwts.builder()
				.setSubject(DEFAULT_USERNAME)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 86400000))
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY)
				.compact();

		String username = authService.getUserNameFromJwtToken(token);

		assertEquals(DEFAULT_USERNAME, username);
	}
}

