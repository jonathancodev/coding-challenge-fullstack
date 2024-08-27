package com.test.operationservice;

import com.test.operationservice.controller.OperationController;
import com.test.operationservice.dto.OperationRequest;
import com.test.operationservice.dto.OperationResponse;
import com.test.operationservice.dto.PaginationRecordRequest;
import com.test.operationservice.dto.RecordResponse;
import com.test.operationservice.dto.ResultOperationResponse;
import com.test.operationservice.enums.OperationType;
import com.test.operationservice.service.impl.OperationService;
import com.test.operationservice.service.impl.RecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OperationController.class)
class OperationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OperationService operationService;

	@MockBean
	private RecordService recordService;

	private static final String DEFAULT_USERNAME = "testuser";
	private static final String DEFAULT_TRANSACTION_ID = "testtransaction";
	private static final OperationType DEFAULT_OPERATION_TYPE = OperationType.ADDITION;

	@Test
	void findAll_ShouldReturnEmptyList() throws Exception {
		when(operationService.findAll()).thenReturn(new ArrayList<>());

		mockMvc.perform(get("/api/v1/operations")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}

	@Test
	void findAll_ShouldReturnList() throws Exception {
		OperationResponse operationResponse = OperationResponse.builder().operationType(OperationType.ADDITION).build();

		when(operationService.findAll()).thenReturn(List.of(operationResponse));

		mockMvc.perform(get("/api/v1/operations")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json("[{\"operationType\":\"" + OperationType.ADDITION + "\"}]"));
	}

	@Test
	void execute_UsernameHeaderRequired_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/operations")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(status().reason("Required header 'X-Username' is not present."));
	}

	@Test
	void execute_TransactionIdRequired_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/operations")
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-Username", DEFAULT_USERNAME)
						.content("{ \"operationType\": \"" + DEFAULT_OPERATION_TYPE + "\", \"operands\": [1.0, 2.0] }"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("transactionId: must not be blank"));
	}

	@Test
	void execute_TransactionNotEmpty_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/operations")
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-Username", DEFAULT_USERNAME)
						.content("{ \"transactionId\": \"\", \"operationType\": \"" + DEFAULT_OPERATION_TYPE + "\", \"operands\": [1.0, 2.0] }"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("transactionId: must not be blank"));
	}

	@Test
	void execute_TransactionMax_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/operations")
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-Username", DEFAULT_USERNAME)
						.content("{ \"transactionId\": \"" + DEFAULT_TRANSACTION_ID.repeat(40) + "\", \"operationType\": \"ADDITION\", \"operands\": [1.0, 2.0] }"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("transactionId: size must be between 0 and 255"));
	}

	@Test
	void execute_OperationTypeIdRequired_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/operations")
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-Username", DEFAULT_USERNAME)
						.content("{ \"transactionId\": \"" + DEFAULT_TRANSACTION_ID + "\", \"operands\": [1.0, 2.0] }"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("operationType: must not be null"));
	}

	@Test
	void execute_OperandsRequired_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/operations")
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-Username", DEFAULT_USERNAME)
						.content("{ \"transactionId\": \"" + DEFAULT_TRANSACTION_ID + "\", \"operationType\": \"" + DEFAULT_OPERATION_TYPE + "\" }"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("operands: must not be empty"));
	}

	@Test
	void execute_OperandsNotEmpty_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(post("/api/v1/operations")
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-Username", DEFAULT_USERNAME)
						.content("{ \"transactionId\": \"" + DEFAULT_TRANSACTION_ID + "\", \"operationType\": \"" + DEFAULT_OPERATION_TYPE + "\", \"operands\": [] }"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("operands: must not be empty"));
	}

	@Test
	void execute_Addition_ReturnsCreated() throws Exception {
		ResultOperationResponse resultOperationResponse = ResultOperationResponse.builder().result("3.0").build();

		when(operationService.execute(eq(DEFAULT_USERNAME), any(OperationRequest.class))).thenReturn(resultOperationResponse);
		mockMvc.perform(post("/api/v1/operations")
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-Username", DEFAULT_USERNAME)
						.content("{ \"transactionId\": \"" + DEFAULT_TRANSACTION_ID + "\", \"operationType\": \"" + DEFAULT_OPERATION_TYPE + "\", \"operands\": [1.0, 2.0] }"))
				.andExpect(status().isCreated())
				.andExpect(content().json("{\"result\":\"3.0\"}"));
	}

	@Test
	void execute_Subtraction_ReturnsCreated() throws Exception {
		ResultOperationResponse resultOperationResponse = ResultOperationResponse.builder().result("-1.0").build();

		when(operationService.execute(eq(DEFAULT_USERNAME), any(OperationRequest.class))).thenReturn(resultOperationResponse);
		mockMvc.perform(post("/api/v1/operations")
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-Username", DEFAULT_USERNAME)
						.content("{ \"transactionId\": \"" + DEFAULT_TRANSACTION_ID + "\", \"operationType\": \"SUBTRACTION\", \"operands\": [1.0, 2.0] }"))
				.andExpect(status().isCreated())
				.andExpect(content().json("{\"result\":\"-1.0\"}"));
	}

	@Test
	void execute_Multiplication_ReturnsCreated() throws Exception {
		ResultOperationResponse resultOperationResponse = ResultOperationResponse.builder().result("2.0").build();

		when(operationService.execute(eq(DEFAULT_USERNAME), any(OperationRequest.class))).thenReturn(resultOperationResponse);
		mockMvc.perform(post("/api/v1/operations")
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-Username", DEFAULT_USERNAME)
						.content("{ \"transactionId\": \"" + DEFAULT_TRANSACTION_ID + "\", \"operationType\": \"MULTIPLICATION\", \"operands\": [1.0, 2.0] }"))
				.andExpect(status().isCreated())
				.andExpect(content().json("{\"result\":\"2.0\"}"));
	}

	@Test
	void execute_Division_ReturnsCreated() throws Exception {
		ResultOperationResponse resultOperationResponse = ResultOperationResponse.builder().result("1.0").build();

		when(operationService.execute(eq(DEFAULT_USERNAME), any(OperationRequest.class))).thenReturn(resultOperationResponse);
		mockMvc.perform(post("/api/v1/operations")
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-Username", DEFAULT_USERNAME)
						.content("{ \"transactionId\": \"" + DEFAULT_TRANSACTION_ID + "\", \"operationType\": \"DIVISION\", \"operands\": [2.0, 2.0] }"))
				.andExpect(status().isCreated())
				.andExpect(content().json("{\"result\":\"1.0\"}"));
	}

	@Test
	void execute_SquareRoot_ReturnsCreated() throws Exception {
		ResultOperationResponse resultOperationResponse = ResultOperationResponse.builder().result("5.0").build();

		when(operationService.execute(eq(DEFAULT_USERNAME), any(OperationRequest.class))).thenReturn(resultOperationResponse);
		mockMvc.perform(post("/api/v1/operations")
						.contentType(MediaType.APPLICATION_JSON)
						.header("X-Username", DEFAULT_USERNAME)
						.content("{ \"transactionId\": \"" + DEFAULT_TRANSACTION_ID + "\", \"operationType\": \"SQUARE_ROOT\", \"operands\": [25.0] }"))
				.andExpect(status().isCreated())
				.andExpect(content().json("{\"result\":\"5.0\"}"));
	}

	@Test
	void testSearch_UsernameHeaderRequired_ShouldReturnBadRequest() throws Exception {
		Long id = 1L;
		mockMvc.perform(get("/api/v1/operations/records", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(status().reason("Required header 'X-Username' is not present."));
	}

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
		when(recordService.search(DEFAULT_USERNAME, paginationRecordRequest)).thenReturn(expectedResponse);

		mockMvc.perform(get("/api/v1/operations/records")
						.header("X-Username", DEFAULT_USERNAME)
						.param("page", "1")
						.param("size", "10")
						.param("sortDirection", "DESC")
						.param("sortBy", "date")
						.param("term", "")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isEmpty());

		verify(recordService, times(1)).search(eq(DEFAULT_USERNAME), any(PaginationRecordRequest.class));
	}

	@Test
	void testSearch_InvalidPageNumber() throws Exception {
		mockMvc.perform(get("/api/v1/operations/records")
						.header("X-Username", DEFAULT_USERNAME)
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
		mockMvc.perform(get("/api/v1/operations/records")
						.header("X-Username", DEFAULT_USERNAME)
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

		mockMvc.perform(patch("/api/v1/operations/records/{id}", id)
						.header("X-Username", username)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verify(recordService, times(1)).logicalDelete(username, id);
	}

	@Test
	void execute_RecordsUsernameHeaderRequired_ShouldReturnBadRequest() throws Exception {
		Long id = 1L;
		mockMvc.perform(patch("/api/v1/operations/records/{id}", id)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(status().reason("Required header 'X-Username' is not present."));
	}

	@Test
	void execute_RecordIdRequired_ShouldReturnBadRequest() throws Exception {
		mockMvc.perform(patch("/api/v1/operations/records/{id}", "")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
}


