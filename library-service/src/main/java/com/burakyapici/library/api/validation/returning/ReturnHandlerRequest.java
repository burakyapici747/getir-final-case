package com.burakyapici.library.api.validation.returning;

import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.Borrowing;
import com.burakyapici.library.domain.model.User;

public record ReturnHandlerRequest(
    BookCopy bookCopy,
    Book book,
    User patron,
    Borrowing borrowing
){}
