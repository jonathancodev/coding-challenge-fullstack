package com.test.codingchallengefullstack;

import org.springframework.boot.SpringApplication;

public class TestCodingChallengeFullstackApplication {

    public static void main(String[] args) {
        SpringApplication.from(CodingChallengeFullstackApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
