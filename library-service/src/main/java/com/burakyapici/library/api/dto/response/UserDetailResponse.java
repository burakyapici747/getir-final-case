package com.burakyapici.library.api.dto.response;

import com.burakyapici.library.domain.enums.PatronStatus;

public record UserDetailResponse(
    String id,
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    String address,
    PatronStatus patronStatus
) {}
