package com.burakyapici.library.domain.dto;

import com.burakyapici.library.domain.enums.WaitListStatus;

public record WaitListDto(
    UserDto user,
    String startDate,
    String endDate,
    WaitListStatus status
) {}
