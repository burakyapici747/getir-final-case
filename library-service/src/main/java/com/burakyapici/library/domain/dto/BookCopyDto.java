package com.burakyapici.library.domain.dto;

import com.burakyapici.library.domain.enums.BookCopyStatus;

import java.util.UUID;

public record BookCopyDto(
    UUID id,
    String barcode,
    BookCopyStatus status
) {}
