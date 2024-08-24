package com.test.apigateway.client;

import com.test.apigateway.dto.UserResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface UserClient {
    Logger log = LoggerFactory.getLogger(UserClient.class);

    @GetExchange("/api/v1/users")
    @CircuitBreaker(name = "users-find-by-username", fallbackMethod = "fallbackMethod")
    @Retry(name = "users-find-by-username")
    UserResponse findByUsername(@RequestParam String username);

    default boolean fallbackMethod(String username, Throwable throwable) {
        log.info("Cannot get user for username {}, failure reason: {}", username, throwable.getMessage());
        return false;
    }
}
