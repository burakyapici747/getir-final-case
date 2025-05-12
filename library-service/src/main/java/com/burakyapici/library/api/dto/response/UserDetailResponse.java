package com.burakyapici.library.api.dto.response;

import com.burakyapici.library.domain.dto.BorrowingDto;
import com.burakyapici.library.domain.dto.WaitListDto;
import com.burakyapici.library.domain.enums.PatronStatus;

import java.util.List;

public record UserDetailResponse(
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
