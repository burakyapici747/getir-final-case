package com.burakyapici.library.api.dto.response;

import java.util.UUID;

public record RegisterResponse(
    UUID id,
    String email,
    String firstName,
    String lastName,
    String role,
    String token
) {}