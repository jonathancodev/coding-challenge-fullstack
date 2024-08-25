package com.test.operationservice;

import com.test.operationservice.client.UserClient;
import com.test.operationservice.dto.CreateRecordRequest;
import com.test.operationservice.dto.PaginationRecordRequest;
import com.test.operationservice.dto.RecordResponse;
import com.test.operationservice.dto.UserResponse;
import com.test.operationservice.enums.RecordStatus;
import com.test.operationservice.mapper.RecordMapper;
import com.test.operationservice.model.Record;
import com.test.operationservice.repository.RecordRepository;
import com.test.operationservice.service.impl.RecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

	@InjectMocks
	private RecordService recordService;

	@Mock
	private RecordRepository recordRepository;

	@Mock
	private RecordMapper recordMapper;

	@Mock
	private UserClient userClient;

	@Mock
	private MessageSource messageSource;

	private static final String DEFAULT_USERNAME = "testuser";
	private static final Long DEFAULT_USER_ID = 1L;
	private static final Long DEFAULT_RECORD_ID = 1L;

	@BeforeEach
	void setUp() {
		recordService = new RecordService(recordRepository, recordMapper, userClient, messageSource);
	}

	@Test
	void testCreate_Success() {
		CreateRecordRequest createRecordRequest = CreateRecordRequest.builder().build();
		Record record = Record.builder().build();
		RecordResponse recordResponse = RecordResponse.builder().build();
		when(recordMapper.toEntity(createRecordRequest)).thenReturn(record);
		when(recordRepository.save(record)).thenReturn(record);
		when(recordMapper.toDTO(record)).thenReturn(recordResponse);

		RecordResponse result = recordService.create(createRecordRequest);

		assertEquals(recordResponse, result);
		verify(recordMapper).toEntity(createRecordRequest);
		verify(recordRepository).save(record);
		verify(recordMapper).toDTO(record);
	}

	@Test
	void testLogicalDelete_Success() {
		Record record = Record.builder().id(DEFAULT_RECORD_ID).userId(DEFAULT_USER_ID).status(RecordStatus.ACTIVE).build();
		UserResponse userResponse = UserResponse.builder().id(DEFAULT_USER_ID).username(DEFAULT_USERNAME).build();
		when(recordRepository.findById(DEFAULT_RECORD_ID)).thenReturn(Optional.of(record));
		when(userClient.findByUsername(DEFAULT_USERNAME)).thenReturn(userResponse);
		when(recordRepository.save(record)).thenReturn(record);

		recordService.logicalDelete(DEFAULT_USERNAME, DEFAULT_RECORD_ID);

		assertEquals(RecordStatus.INACTIVE, record.getStatus());
		verify(recordRepository).findById(DEFAULT_RECORD_ID);
		verify(userClient).findByUsername(DEFAULT_USERNAME);
		verify(recordRepository).save(record);
	}

	@Test
	void testLogicalDelete_RecordNotFound() {
		when(recordRepository.findById(DEFAULT_RECORD_ID)).thenReturn(Optional.empty());

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
				recordService.logicalDelete(DEFAULT_USERNAME, DEFAULT_RECORD_ID));

		assertEquals(messageSource.getMessage("record.not.found", null, Locale.ENGLISH), exception.getMessage());
		verify(recordRepository).findById(DEFAULT_RECORD_ID);
	}

	@Test
	void testLogicalDelete_UserNotFound() {
		Record record = Record.builder().id(DEFAULT_RECORD_ID).userId(DEFAULT_USER_ID).status(RecordStatus.ACTIVE).build();
		when(recordRepository.findById(DEFAULT_RECORD_ID)).thenReturn(Optional.of(record));
		when(userClient.findByUsername(DEFAULT_USERNAME)).thenReturn(null);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
				recordService.logicalDelete(DEFAULT_USERNAME, DEFAULT_RECORD_ID));

		assertEquals(messageSource.getMessage("user.not.found", null, Locale.ENGLISH), exception.getMessage());
		verify(recordRepository).findById(DEFAULT_RECORD_ID);
		verify(userClient).findByUsername(DEFAULT_USERNAME);
	}

	@Test
	void testLogicalDelete_WrongUser() {
		Record record = Record.builder().id(DEFAULT_RECORD_ID).userId(DEFAULT_USER_ID).status(RecordStatus.ACTIVE).build();
		UserResponse userResponse = UserResponse.builder().id(DEFAULT_USER_ID).username(DEFAULT_USERNAME).build();
		when(recordRepository.findById(DEFAULT_RECORD_ID)).thenReturn(Optional.of(record));
		when(userClient.findByUsername(DEFAULT_USERNAME)).thenReturn(userResponse);

		record.setUserId(999L); // different user ID

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
				recordService.logicalDelete(DEFAULT_USERNAME, DEFAULT_RECORD_ID));

		assertEquals(messageSource.getMessage("record.wrong.user", null, Locale.ENGLISH), exception.getMessage());
		verify(recordRepository).findById(DEFAULT_RECORD_ID);
		verify(userClient).findByUsername(DEFAULT_USERNAME);
	}

	@Test
	void testFindLastRecordByUserId_Success() {
		Record record = Record.builder().build();
		RecordResponse recordResponse = RecordResponse.builder().build();
		when(recordRepository.findFirstByUserIdOrderByDateDesc(1L)).thenReturn(Optional.of(record));
		when(recordMapper.toDTO(record)).thenReturn(recordResponse);

		RecordResponse result = recordService.findLastRecordByUserId(1L);

		assertEquals(recordResponse, result);
		verify(recordRepository).findFirstByUserIdOrderByDateDesc(1L);
		verify(recordMapper).toDTO(record);
	}

	@Test
	void testFindLastRecordByUserId_NoRecordFound() {
		when(recordRepository.findFirstByUserIdOrderByDateDesc(1L)).thenReturn(Optional.empty());

		RecordResponse result = recordService.findLastRecordByUserId(1L);

		assertNull(result);
		verify(recordRepository).findFirstByUserIdOrderByDateDesc(1L);
	}

	@Test
	void testSearch_Success() {
		Record record = Record.builder().id(DEFAULT_RECORD_ID).userId(DEFAULT_USER_ID).status(RecordStatus.ACTIVE).build();
		RecordResponse recordResponse = RecordResponse.builder().build();
		PaginationRecordRequest paginationRecordRequest = PaginationRecordRequest.builder()
				.page(0).size(10).sortBy("date").sortDirection("desc").build();
		Pageable pageable = PageRequest.of(paginationRecordRequest.page(), paginationRecordRequest.size(), Sort.Direction.fromString(paginationRecordRequest.sortDirection()), paginationRecordRequest.sortBy());
		Page<Record> records = new PageImpl<>(List.of(record));

		when(recordRepository.search(paginationRecordRequest.term(), pageable)).thenReturn(records);
		when(recordMapper.toDTO(record)).thenReturn(recordResponse);

		Page<RecordResponse> result = recordService.search(paginationRecordRequest);

		assertEquals(1, result.getTotalElements());
		assertEquals(recordResponse, result.getContent().get(0));
		verify(recordRepository).search(paginationRecordRequest.term(), pageable);
		verify(recordMapper).toDTO(record);
	}
}

