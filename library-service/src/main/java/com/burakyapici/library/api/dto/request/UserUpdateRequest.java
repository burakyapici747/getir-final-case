package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.PatronStatus;

public record UserUpdateRequest(
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    String address,
    PatronStatus patronStatus
) {}
