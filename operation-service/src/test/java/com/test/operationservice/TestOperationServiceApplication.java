package com.test.operationservice;

import org.springframework.boot.SpringApplication;

public class TestOperationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(OperationServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
