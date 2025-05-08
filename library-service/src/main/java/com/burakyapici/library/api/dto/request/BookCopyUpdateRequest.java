package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.BookCopyStatus;

import java.util.UUID;

public record BookCopyUpdateRequest(
    UUID barcode,
    BookCopyStatus bookCopyStatus
) {}
