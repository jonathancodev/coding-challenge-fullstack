package com.test.operationservice;

import com.test.operationservice.client.UserClient;
import com.test.operationservice.dto.CreateRecordRequest;
import com.test.operationservice.dto.OperationRequest;
import com.test.operationservice.dto.OperationResponse;
import com.test.operationservice.dto.RecordResponse;
import com.test.operationservice.dto.ResultOperationResponse;
import com.test.operationservice.dto.UserResponse;
import com.test.operationservice.enums.OperationType;
import com.test.operationservice.enums.UserStatus;
import com.test.operationservice.factory.impl.CalculatorOperationFactory;
import com.test.operationservice.factory.impl.StringOperationFactory;
import com.test.operationservice.mapper.OperationMapper;
import com.test.operationservice.model.Operation;
import com.test.operationservice.repository.OperationRepository;
import com.test.operationservice.service.impl.OperationService;
import com.test.operationservice.service.impl.RecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class OperationServiceTest {

	@InjectMocks
	private OperationService operationService;

	@Mock
	private OperationRepository operationRepository;

	@Mock
	private OperationMapper operationMapper;

	@Autowired
	private CalculatorOperationFactory calculatorOperationFactory;

	@Mock
	private StringOperationFactory stringOperationFactory;

	@Mock
	private UserClient userClient;

	@Mock
	private RecordService recordService;

	@Autowired
	private MessageSource messageSource;

	@BeforeEach
	void setUp() {
		operationService = new OperationService(operationRepository, operationMapper,
				calculatorOperationFactory, stringOperationFactory, userClient,
				recordService, messageSource);
	}

	@Test
	void testFindAll() {
		Operation operation = new Operation();
		OperationResponse response = OperationResponse.builder().build();
		when(operationRepository.findAll()).thenReturn(List.of(operation));
		when(operationMapper.toDTOList(anyList())).thenReturn(List.of(response));

		List<OperationResponse> result = operationService.findAll();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(response, result.get(0));
	}

	@Test
	void testExecute_UserNotFound() {
		String username = "testuser";
		OperationRequest request = new OperationRequest("transactionId", OperationType.ADDITION, new double[]{ 1.0, 2.0 });
		when(userClient.findByUsername(username)).thenReturn(null);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
				operationService.execute(username, request)
		);
		assertEquals(messageSource.getMessage("user.not.found", null, Locale.ENGLISH), thrown.getMessage());
	}

	@Test
	void testExecute_UserInactive() {
		String username = "testuser";
		OperationRequest request = new OperationRequest("transactionId", OperationType.ADDITION, new double[]{ 1.0, 2.0 });
		UserResponse userResponse = UserResponse.builder().id(1L).status(UserStatus.INACTIVE).build();
		Operation operation = new Operation();
		operation.setCost(100.0);

		when(userClient.findByUsername(username)).thenReturn(userResponse);
		when(recordService.findLastRecordByUserId(anyLong())).thenReturn(RecordResponse.builder().userBalance(50.0).build());
		when(operationRepository.findByOperationType(any(OperationType.class))).thenReturn(Optional.of(operation));

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
				operationService.execute(username, request)
		);
		assertEquals(messageSource.getMessage("user.inactive", null, Locale.ENGLISH), thrown.getMessage());
	}

	@Test
	void testExecute_InsufficientBalance() {
		String username = "testuser";
		OperationRequest request = new OperationRequest("transactionId", OperationType.ADDITION, new double[]{ 1.0, 2.0 });
		UserResponse userResponse = UserResponse.builder().id(1L).status(UserStatus.ACTIVE).build();
		Operation operation = new Operation();
		operation.setCost(100.0);

		when(userClient.findByUsername(username)).thenReturn(userResponse);
		when(recordService.findLastRecordByUserId(anyLong())).thenReturn(RecordResponse.builder().userBalance(50.0).build());
		when(operationRepository.findByOperationType(any(OperationType.class))).thenReturn(Optional.of(operation));

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
				operationService.execute(username, request)
		);
		assertEquals(messageSource.getMessage("user.insufficient.balance", null, Locale.ENGLISH), thrown.getMessage());
	}

	@Test
	void testExecute_DivideByZero() {
		String username = "testuser";
		OperationRequest request = new OperationRequest("transactionId", OperationType.DIVISION, new double[]{ 1.0, 0.0 });
		UserResponse userResponse = UserResponse.builder().id(1L).status(UserStatus.ACTIVE).build();
		Operation operation = new Operation();
		operation.setCost(1.0);

		when(userClient.findByUsername(username)).thenReturn(userResponse);
		when(recordService.findLastRecordByUserId(anyLong())).thenReturn(RecordResponse.builder().userBalance(50.0).build());
		when(operationRepository.findByOperationType(any(OperationType.class))).thenReturn(Optional.of(operation));

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
				operationService.execute(username, request)
		);
		assertEquals(messageSource.getMessage("operation.division.by.zero", null, Locale.ENGLISH), thrown.getMessage());
	}

	@Test
	void testExecute_SquareRootByNegativeNumber() {
		String username = "testuser";
		OperationRequest request = new OperationRequest("transactionId", OperationType.SQUARE_ROOT, new double[]{ -1.0 });
		UserResponse userResponse = UserResponse.builder().id(1L).status(UserStatus.ACTIVE).build();
		Operation operation = new Operation();
		operation.setCost(1.0);

		when(userClient.findByUsername(username)).thenReturn(userResponse);
		when(recordService.findLastRecordByUserId(anyLong())).thenReturn(RecordResponse.builder().userBalance(50.0).build());
		when(operationRepository.findByOperationType(any(OperationType.class))).thenReturn(Optional.of(operation));

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
				operationService.execute(username, request)
		);
		assertEquals(messageSource.getMessage("operation.square.root.negative", null, Locale.ENGLISH), thrown.getMessage());
	}

	@Test
	void testExecute_AdditionSuccessful() {
		String username = "testuser";
		OperationRequest request = new OperationRequest("transactionId", OperationType.ADDITION, new double[]{ 1.0, 2.0 });
		UserResponse userResponse = UserResponse.builder().id(1L).status(UserStatus.ACTIVE).build();
		Operation operation = new Operation();
		operation.setCost(1.0);
		ResultOperationResponse expectedResponse = ResultOperationResponse.builder().result("3.0").build();

		when(userClient.findByUsername(username)).thenReturn(userResponse);
		when(recordService.findLastRecordByUserId(anyLong())).thenReturn(RecordResponse.builder().userBalance(50.0).build());
		when(operationRepository.findByOperationType(any(OperationType.class))).thenReturn(Optional.of(operation));
		when(recordService.create(any(CreateRecordRequest.class))).thenReturn(null);

		ResultOperationResponse result = operationService.execute(username, request);

		assertNotNull(result);
		assertEquals(expectedResponse.result(), result.result());
		verify(recordService).create(any(CreateRecordRequest.class));
	}

	@Test
	void testExecute_SubtractionSuccessful() {
		String username = "testuser";
		OperationRequest request = new OperationRequest("transactionId", OperationType.SUBTRACTION, new double[]{ 1.0, 2.0 });
		UserResponse userResponse = UserResponse.builder().id(1L).status(UserStatus.ACTIVE).build();
		Operation operation = new Operation();
		operation.setCost(1.0);
		ResultOperationResponse expectedResponse = ResultOperationResponse.builder().result("-1.0").build();

		when(userClient.findByUsername(username)).thenReturn(userResponse);
		when(recordService.findLastRecordByUserId(anyLong())).thenReturn(RecordResponse.builder().userBalance(50.0).build());
		when(operationRepository.findByOperationType(any(OperationType.class))).thenReturn(Optional.of(operation));
		when(recordService.create(any(CreateRecordRequest.class))).thenReturn(null);

		ResultOperationResponse result = operationService.execute(username, request);

		assertNotNull(result);
		assertEquals(expectedResponse.result(), result.result());
		verify(recordService).create(any(CreateRecordRequest.class));
	}

	@Test
	void testExecute_MultiplicationSuccessful() {
		String username = "testuser";
		OperationRequest request = new OperationRequest("transactionId", OperationType.MULTIPLICATION, new double[]{ 1.0, 2.0 });
		UserResponse userResponse = UserResponse.builder().id(1L).status(UserStatus.ACTIVE).build();
		Operation operation = new Operation();
		operation.setCost(1.0);
		ResultOperationResponse expectedResponse = ResultOperationResponse.builder().result("2.0").build();

		when(userClient.findByUsername(username)).thenReturn(userResponse);
		when(recordService.findLastRecordByUserId(anyLong())).thenReturn(RecordResponse.builder().userBalance(50.0).build());
		when(operationRepository.findByOperationType(any(OperationType.class))).thenReturn(Optional.of(operation));
		when(recordService.create(any(CreateRecordRequest.class))).thenReturn(null);

		ResultOperationResponse result = operationService.execute(username, request);

		assertNotNull(result);
		assertEquals(expectedResponse.result(), result.result());
		verify(recordService).create(any(CreateRecordRequest.class));
	}

	@Test
	void testExecute_DivisionSuccessful() {
		String username = "testuser";
		OperationRequest request = new OperationRequest("transactionId", OperationType.DIVISION, new double[]{ 2.0, 2.0 });
		UserResponse userResponse = UserResponse.builder().id(1L).status(UserStatus.ACTIVE).build();
		Operation operation = new Operation();
		operation.setCost(1.0);
		ResultOperationResponse expectedResponse = ResultOperationResponse.builder().result("1.0").build();

		when(userClient.findByUsername(username)).thenReturn(userResponse);
		when(recordService.findLastRecordByUserId(anyLong())).thenReturn(RecordResponse.builder().userBalance(50.0).build());
		when(operationRepository.findByOperationType(any(OperationType.class))).thenReturn(Optional.of(operation));
		when(recordService.create(any(CreateRecordRequest.class))).thenReturn(null);

		ResultOperationResponse result = operationService.execute(username, request);

		assertNotNull(result);
		assertEquals(expectedResponse.result(), result.result());
		verify(recordService).create(any(CreateRecordRequest.class));
	}

	@Test
	void testExecute_SquareRootSuccessful() {
		String username = "testuser";
		OperationRequest request = new OperationRequest("transactionId", OperationType.SQUARE_ROOT, new double[]{ 25.0 });
		UserResponse userResponse = UserResponse.builder().id(1L).status(UserStatus.ACTIVE).build();
		Operation operation = new Operation();
		operation.setCost(1.0);
		ResultOperationResponse expectedResponse = ResultOperationResponse.builder().result("5.0").build();

		when(userClient.findByUsername(username)).thenReturn(userResponse);
		when(recordService.findLastRecordByUserId(anyLong())).thenReturn(RecordResponse.builder().userBalance(50.0).build());
		when(operationRepository.findByOperationType(any(OperationType.class))).thenReturn(Optional.of(operation));
		when(recordService.create(any(CreateRecordRequest.class))).thenReturn(null);

		ResultOperationResponse result = operationService.execute(username, request);

		assertNotNull(result);
		assertEquals(expectedResponse.result(), result.result());
		verify(recordService).create(any(CreateRecordRequest.class));
	}
}

