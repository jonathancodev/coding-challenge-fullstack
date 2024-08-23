package com.test.apigateway.controller;

import com.test.apigateway.dto.LoginRequest;
import com.test.apigateway.dto.LoginResponse;
import com.test.apigateway.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest.username(), loginRequest.password());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}

