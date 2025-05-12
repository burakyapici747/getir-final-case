package com.burakyapici.library.service.validation.waitlist;

import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record PlaceHoldHandlerRequest (
    User patron,
    Book book,
    UUID bookId,
    Optional<Integer> waitListCount,
    List<BookCopy> availableCopies
){} 