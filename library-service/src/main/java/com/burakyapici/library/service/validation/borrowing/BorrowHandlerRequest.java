package com.burakyapici.library.service.validation.borrowing;

import com.burakyapici.library.domain.model.*;

public record BorrowHandlerRequest (
    BookCopy bookCopy,
    Book book,
    User patron,
    WaitList waitList
){}
