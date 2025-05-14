package com.burakyapici.library.domain.dto;

import com.burakyapici.library.domain.enums.BorrowingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record BorrowingDto(
    UUID id,
    UUID userId,
    String userEmail,
    String userFirstName,
    String userLastName,
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
    BorrowingStatus status
) {}
