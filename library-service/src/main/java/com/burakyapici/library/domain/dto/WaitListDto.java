package com.burakyapici.library.domain.dto;

import com.burakyapici.library.domain.enums.WaitListStatus;

import java.util.UUID;

public record WaitListDto(
    UUID id,
    String startDate,
    String endDate,
    WaitListStatus status
) {}
