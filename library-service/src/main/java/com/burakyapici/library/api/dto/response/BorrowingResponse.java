package com.burakyapici.library.api.dto.response;

import com.burakyapici.library.domain.enums.BorrowStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record BorrowingResponse(
    UUID id,
    UUID userId,
    String userEmail,
    String userFullName,
    UUID bookCopyId,
    String bookCopyBarcode,
    UUID bookId,
    String bookTitle,
    String bookIsbn,
    UUID borrowedByStaffId,
    String borrowedByStaffName,
    UUID returnedByStaffId,
    String returnedByStaffName,
    LocalDateTime borrowDate,
    LocalDateTime dueDate,
    LocalDateTime returnDate,
    BorrowStatus status
) {}
