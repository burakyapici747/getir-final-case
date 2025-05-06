package com.burakyapici.library.domain.dto;

import com.burakyapici.library.domain.enums.PatronStatus;

import java.util.List;

public record UserDetailDto(
    String id,
    String email,
    String firstName,
    String lastName,
    String phoneNumber,
    String address,
    PatronStatus patronStatus,
    List<BorrowingDto> borrowingDtoList,
    List<WaitListDto> waitListDtoList
) {}
