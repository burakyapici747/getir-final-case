package com.burakyapici.library.api.dto.response;

public record RegisterResponse(
    String token,
    String email,
    String firstName,
    String lastName,
    String role
) {}