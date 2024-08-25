package com.test.operationservice;

import com.test.operationservice.controller.RecordController;
import com.test.operationservice.dto.PaginationRecordRequest;
import com.test.operationservice.dto.RecordResponse;
import com.test.operationservice.service.impl.RecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecordController.class)
class RecordControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RecordService recordService;

	@Test
	void testSearch_Success() throws Exception {
		PaginationRecordRequest paginationRecordRequest = PaginationRecordRequest.builder()
				.page(1)
				.size(10)
				.sortDirection("DESC")
				.sortBy("date")
				.term("")
				.build();

		Page<RecordResponse> expectedResponse = Page.empty();
		when(recordService.search(paginationRecordRequest)).thenReturn(expectedResponse);

		mockMvc.perform(get("/api/v1/records")
						.param("page", "1")
						.param("size", "10")
						.param("sortDirection", "DESC")
						.param("sortBy", "date")
						.param("term", "")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isEmpty());

		verify(recordService, times(1)).search(any(PaginationRecordRequest.class));
	}

	@Test
	void testSearch_InvalidPageNumber() throws Exception {
		mockMvc.perform(get("/api/v1/records")
						.param("page", "-1")
						.param("size", "10")
						.param("sortDirection", "DESC")
						.param("sortBy", "date")
						.param("term", "")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testSearch_InvalidSize() throws Exception {
		mockMvc.perform(get("/api/v1/records")
						.param("page", "1")
						.param("size", "-10")
						.param("sortDirection", "DESC")
						.param("sortBy", "date")
						.param("term", "")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testLogicalDelete_Success() throws Exception {
		String username = "testuser";
		Long id = 1L;

		doNothing().when(recordService).logicalDelete(username, id);

		mockMvc.perform(patch("/api/v1/records/{id}", id)
						.header("X-Username", username)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verify(recordService, times(1)).logicalDelete(username, id);
	}

	@Test
	void execute_UsernameHeaderRequired_ShouldReturnBadRequest() throws Exception {
		Long id = 1L;
		mockMvc.perform(patch("/api/v1/records/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(status().reason("Required header 'X-Username' is not present."));
	}

	@Test
	void execute_ReacordIdRequired_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(patch("/api/v1/records/{id}", "")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
}


