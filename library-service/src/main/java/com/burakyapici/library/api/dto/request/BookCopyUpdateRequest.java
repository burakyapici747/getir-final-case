package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.BookCopyStatus;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BookCopyUpdateRequest(
    @NotNull(message = "Barcode cannot be null")
    UUID barcode,

    @NotNull(message = "Book copy status cannot be null")
    BookCopyStatus bookCopyStatus
) {}