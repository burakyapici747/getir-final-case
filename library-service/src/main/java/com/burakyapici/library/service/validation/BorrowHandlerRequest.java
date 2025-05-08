package com.burakyapici.library.service.validation;

import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.model.WaitList;

import java.util.Optional;

public record BorrowHandlerRequest (
    BookCopy bookCopy,
    Book book,
    User patron,
    Optional<WaitList> waitListOptional
){}
