package com.test.operationservice.exception;

import com.test.operationservice.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder error = new StringBuilder();
        FieldError fieldError = e.getBindingResult().getFieldErrors().getFirst();
        error.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage());

        return ResponseEntity.badRequest().body(ErrorResponse.builder().message(error.toString()).build());
    }
}
