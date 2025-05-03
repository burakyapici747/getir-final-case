package com.burakyapici.library.api.dto.response;

import lombok.Builder;

public record LoginResponse (
    String accessToken,
    String tokenType,
    long expiresIn,
    String email
){}
