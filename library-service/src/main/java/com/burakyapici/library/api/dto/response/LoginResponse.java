package com.burakyapici.library.api.dto.response;

public record LoginResponse (
    String accessToken,
    String tokenType,
    long expiresIn,
    String email
){}
