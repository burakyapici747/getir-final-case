package com.burakyapici.library.domain.dto;

import com.burakyapici.library.domain.enums.PatronStatus;

public record UserDetailDto(
    String id,
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    String address,
    PatronStatus patronStatus
) {}
