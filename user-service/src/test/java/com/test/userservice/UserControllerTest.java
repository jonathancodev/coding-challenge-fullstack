package com.test.userservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.userservice.controller.UserController;
import com.test.userservice.dto.UserResponse;
import com.test.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	private ObjectMapper objectMapper;

	@Test
	void findByUsername() throws Exception {
		String username = "testuser";
		UserResponse userResponse = UserResponse.builder().username(username).build();
		when(userService.findByUsername(username)).thenReturn(userResponse);

		mockMvc.perform(get("/api/v1/users")
						.contentType(MediaType.APPLICATION_JSON)
						.param("username", username))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value(username));
	}

	@Test
	void findByUsername_UsernameRequired() throws Exception {
		mockMvc.perform(get("/api/v1/users")
						.contentType(MediaType.APPLICATION_JSON)
						.param("test", ""))
				.andExpect(status().isBadRequest())
				.andExpect(status().reason("Required parameter 'username' is not present."));
	}
}

