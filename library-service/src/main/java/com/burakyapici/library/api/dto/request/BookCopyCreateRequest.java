package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.BookCopyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record BookCopyCreateRequest(
    @NotBlank(message = "Barcode cannot be empty")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Barcode can only contain letters, numbers and hyphens")
    String barcode,

    @NotNull(message = "Book ID cannot be null")
    UUID bookId,

    BookCopyStatus bookCopyStatus
) {
    public BookCopyCreateRequest(String barcode, UUID bookId, BookCopyStatus bookCopyStatus) {
        this.barcode = barcode;
        this.bookId = bookId;
        this.bookCopyStatus = bookCopyStatus != null ? bookCopyStatus : BookCopyStatus.AVAILABLE;
    }
}