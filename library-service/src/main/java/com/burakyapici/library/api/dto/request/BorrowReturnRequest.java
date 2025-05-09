package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.ReturnType;

import java.util.UUID;

public record BorrowReturnRequest(
    UUID patronId,
    ReturnType returnType
) {}
