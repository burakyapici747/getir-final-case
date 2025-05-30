package com.burakyapici.library.api.validation.borrowing;

import com.burakyapici.library.domain.model.*;

import java.util.Optional;

public record BorrowHandlerRequest (
    User patron,
    Book book,
    BookCopy bookCopy,
    Optional<WaitList> waitList
){}
