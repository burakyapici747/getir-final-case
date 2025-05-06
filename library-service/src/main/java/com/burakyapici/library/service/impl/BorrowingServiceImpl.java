package com.burakyapici.library.service.impl;

import com.burakyapici.library.common.mapper.BorrowMapper;
import com.burakyapici.library.domain.dto.BorrowDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.enums.BorrowStatus;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.Borrowing;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.repository.BorrowingRepository;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.BookCopyService;
import com.burakyapici.library.service.BorrowingService;
import com.burakyapici.library.service.UserService;
import com.burakyapici.library.service.WaitListService;
import com.burakyapici.library.service.validation.BorrowHandlerRequest;
import com.burakyapici.library.service.validation.BorrowValidationHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BorrowingServiceImpl implements BorrowingService {
    private final BorrowingRepository borrowingRepository;
    private final BookCopyService bookCopyService;
    private final WaitListService waitListService;
    private final UserService userService;
    private final BorrowValidationHandler borrowValidationHandler;

    public BorrowingServiceImpl(
        BorrowingRepository borrowingRepository,
        BookCopyService bookCopyService,
        WaitListService waitListService, UserService userService,
        @Qualifier("borrowValidationChain")
        BorrowValidationHandler borrowValidationHandler
    ) {
        this.borrowingRepository = borrowingRepository;
        this.bookCopyService = bookCopyService;
        this.waitListService = waitListService;
        this.userService = userService;
        this.borrowValidationHandler = borrowValidationHandler;
    }

    @Override
    public BorrowDto borrowBook(UUID bookCopyBarcode, UserDetailsImpl userDetails) {
        User patron = userService.getUserByEmailOrElseThrow(userDetails.getUsername());

        BookCopy bookCopy = bookCopyService.getBookCopyByBarcodeOrElseThrow(bookCopyBarcode);

        Book book = bookCopy.getBook();

        BorrowHandlerRequest borrowHandlerRequest = new BorrowHandlerRequest(
            bookCopy,
            book,
            patron
        );

        borrowValidationHandler.handle(borrowHandlerRequest);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime after15Days = now.plusDays(15);

        Borrowing borrowing = Borrowing.builder()
            .bookCopy(bookCopy)
            .user(patron)
            .borrowDate(now)
            .dueDate(after15Days)
            .status(BorrowStatus.BORROWED)
            .build();

        bookCopy.setStatus(BookCopyStatus.CHECKED_OUT);

        return BorrowMapper.INSTANCE.toDto(borrowing);
    }
}
