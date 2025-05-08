package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.BookCopyStatus;

public record BookCopySearchCriteria(
    String barcode,
    BookCopyStatus status
) {}
