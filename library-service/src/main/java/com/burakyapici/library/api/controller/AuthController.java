package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.request.LoginRequest;
import com.burakyapici.library.api.dto.request.RegisterRequest;
import com.burakyapici.library.api.dto.response.LoginResponse;
import com.burakyapici.library.api.dto.response.RegisterResponse;
import com.burakyapici.library.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @Valid
        @RequestBody
        LoginRequest loginRequest
    ){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
        @Valid
        @RequestBody
        RegisterRequest registerRequest
    ){
        return ResponseEntity.ok(authService.register(registerRequest));
    }
}
