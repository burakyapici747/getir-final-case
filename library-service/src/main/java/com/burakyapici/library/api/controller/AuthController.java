package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.response.ApiResponse;
import com.burakyapici.library.api.dto.request.LoginRequest;
import com.burakyapici.library.api.dto.request.RegisterRequest;
import com.burakyapici.library.api.dto.response.LoginResponse;
import com.burakyapici.library.api.dto.response.RegisterResponse;
import com.burakyapici.library.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Handles user login and registration")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "User login", description = "Authenticates user and returns JWT token.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful",
                content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<LoginResponse>> login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "User login credentials",
            content = @Content(schema = @Schema(implementation = LoginRequest.class))
        )
        @Valid @RequestBody LoginRequest loginRequest
    ) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ApiResponse.okResponse(loginResponse, "Login successful");
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "User registration", description = "Registers a new user and returns user ID.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Registration successful",
                content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid registration request")
    })
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "User registration details",
            content = @Content(schema = @Schema(implementation = RegisterRequest.class))
        )
        @Valid @RequestBody RegisterRequest registerRequest
    ) {
        RegisterResponse registerResponse = authService.register(registerRequest);
        return ApiResponse.createdResponse(registerResponse, "Registration successful", registerResponse.id());
    }
}
