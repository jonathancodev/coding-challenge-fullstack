package com.test.operationservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.service.annotation.GetExchange;

public interface RandomClient {
    Logger log = LoggerFactory.getLogger(RandomClient.class);

    @GetExchange("?num=1&len=32&digits=on&upperalpha=on&loweralpha=on&unique=on&format=plain")
    @CircuitBreaker(name = "generate-random-string", fallbackMethod = "fallbackMethod")
    @Retry(name = "generate-random-string")
    String generateRandomString();

    default boolean fallbackMethod(String username, Throwable throwable) {
        log.info("Cannot get user for username {}, failure reason: {}", username, throwable.getMessage());
        return false;
    }
}
