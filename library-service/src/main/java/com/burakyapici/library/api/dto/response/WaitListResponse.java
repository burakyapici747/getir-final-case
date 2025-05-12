package com.burakyapici.library.api.dto.response;

import com.burakyapici.library.domain.enums.WaitListStatus;

import java.util.UUID;

public record WaitListResponse(
    UUID id,
    String startDate,
    String endDate,
    WaitListStatus status
) {}
