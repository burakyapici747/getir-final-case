package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.BookCopyStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BookCopySearchCriteria(
    @Size(max = 50, message = "Barcode cannot exceed 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9-]*$", message = "Barcode can only contain letters, numbers and hyphens")
    String barcode,

    BookCopyStatus status
) {}