package com.test.apigateway;

import com.test.apigateway.config.SecurityConfig;
import com.test.apigateway.controller.AuthController;
import com.test.apigateway.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@Autowired
	private MessageSource messageSource;

	private static final String DEFAULT_USERNAME = "user";
	private static final String DEFAULT_PASSWORD = "password";

	@Test
	void login_ValidCredentials_ShouldReturnToken() throws Exception {
		String token = "mocked-jwt-token";

		when(authService.login(DEFAULT_USERNAME, DEFAULT_PASSWORD)).thenReturn(token);

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"username\": \"" + DEFAULT_USERNAME + "\", \"password\": \"" + DEFAULT_PASSWORD + "\" }"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value(token));
	}

	@Test
	void login_UsernameRequired_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"password\": \"" + DEFAULT_PASSWORD + "\" }"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("username: must not be empty"));
	}

	@Test
	void login_UsernameNotEmpty_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"username\": \"" + "\", \"password\": \"" + DEFAULT_PASSWORD + "\" }"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("username: must not be empty"));
	}

	@Test
	void login_UsernameMax_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"username\": \"" + DEFAULT_USERNAME.repeat(15) + "\", \"password\": \"" + DEFAULT_PASSWORD + "\" }"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("username: size must be between 0 and 50"));
	}

	@Test
	void login_PasswordRequired_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"username\": \"" + DEFAULT_USERNAME + "\" }"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("password: must not be empty"));
	}

	@Test
	void login_PasswordNotEmpty_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"username\": \"" + DEFAULT_USERNAME + "\", \"password\": \"" + "\" }"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("password: must not be empty"));
	}

	@Test
	void login_PasswordMax_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"username\": \"" + DEFAULT_USERNAME + "\", \"password\": \"" + DEFAULT_PASSWORD.repeat(40) + "\" }"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("password: size must be between 0 and 255"));
	}

	@Test
	void login_InvalidCredentials_ShouldReturnBadRequest() throws Exception {
		String username = "user";
		String password = "wrong-password";

		when(authService.login(username, password)).thenThrow(new IllegalArgumentException(messageSource.getMessage("auth.wrong.user.password", null, null)));

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(messageSource.getMessage("auth.wrong.user.password", null, null)));
	}
}


