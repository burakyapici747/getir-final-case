package com.burakyapici.library.api.dto.response;

import com.burakyapici.library.domain.enums.BorrowStatus;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.User;

import java.time.LocalDateTime;

public record BorrowingResponse(
    User user,
    BookCopy bookCopy,
    User borrowedByStaff,
    User returnedByStaff,
    LocalDateTime borrowDate,
    LocalDateTime dueDate,
    LocalDateTime returnDate,
    BorrowStatus status
) {}
