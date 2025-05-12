package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.BookCopyStatus;
import jakarta.validation.constraints.NotNull;

public record BookCopyUpdateRequest(
    @NotNull(message = "Book copy status cannot be null")
    BookCopyStatus status
) {}