package com.burakyapici.library.api.dto.response;

import com.burakyapici.library.domain.enums.BookCopyStatus;

import java.util.UUID;

public record BookCopyResponse(
    UUID id,
    UUID barcode,
    BookCopyStatus status
) {}
