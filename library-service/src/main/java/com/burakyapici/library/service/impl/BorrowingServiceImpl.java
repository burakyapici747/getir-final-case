package com.burakyapici.library.service.impl;

import com.burakyapici.library.api.dto.request.BookAvailabilityUpdateEvent;
import com.burakyapici.library.api.dto.request.BorrowBookCopyRequest;
import com.burakyapici.library.api.dto.request.BorrowReturnRequest;
import com.burakyapici.library.common.mapper.BorrowMapper;
import com.burakyapici.library.domain.dto.BorrowDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.enums.BorrowStatus;
import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.model.*;
import com.burakyapici.library.domain.repository.BorrowingRepository;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.BookCopyService;
import com.burakyapici.library.service.BorrowingService;
import com.burakyapici.library.service.UserService;
import com.burakyapici.library.service.WaitListService;
import com.burakyapici.library.service.validation.borrowing.BorrowHandlerRequest;
import com.burakyapici.library.service.validation.borrowing.BorrowValidationHandler;
import com.burakyapici.library.service.validation.returning.ReturnHandlerRequest;
import com.burakyapici.library.service.validation.returning.ReturnValidationHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;

@Service
public class BorrowingServiceImpl implements BorrowingService {
    private final UserService userService;
    private final BookCopyService bookCopyService;
    private final WaitListService waitListService;
    private final BorrowingRepository borrowingRepository;
    private final BorrowValidationHandler borrowValidationHandler;
    private final ReturnValidationHandler returnValidationHandler;
    private final Sinks.Many<BookAvailabilityUpdateEvent> bookAvailabilitySink;

    public BorrowingServiceImpl(
        BorrowingRepository borrowingRepository,
        BookCopyService bookCopyService,
        WaitListService waitListService,
        UserService userService,
        @Qualifier("borrowValidationChain")
        BorrowValidationHandler borrowValidationHandler,
        @Qualifier("returnValidationChain")
        ReturnValidationHandler returnValidationHandler,
        Sinks.Many<BookAvailabilityUpdateEvent> bookAvailabilitySink
    ) {
        this.borrowingRepository = borrowingRepository;
        this.bookCopyService = bookCopyService;
        this.waitListService = waitListService;
        this.userService = userService;
        this.borrowValidationHandler = borrowValidationHandler;
        this.returnValidationHandler = returnValidationHandler;
        this.bookAvailabilitySink = bookAvailabilitySink;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowDto borrowBookCopyByBarcode(
        String barcode,
        BorrowBookCopyRequest borrowBookCopyRequest,
        UserDetailsImpl userDetails
    ) {
        User patron = userService.getUserByIdOrElseThrow(borrowBookCopyRequest.patronId());
        User librarian = userService.getUserByIdOrElseThrow(userDetails.getId());

        BookCopy bookCopy = bookCopyService.getBookCopyByBarcodeOrElseThrow(barcode);

        Book book = bookCopy.getBook();

        WaitList waitList = waitListService.getByUserIdAndBookIdAndStatus(
            patron.getId(),
            book.getId(),
            WaitListStatus.READY_FOR_PICKUP
        );

        BorrowHandlerRequest borrowHandlerRequest = new BorrowHandlerRequest(
            bookCopy,
            book,
            patron,
            waitList
        );

        borrowValidationHandler.handle(borrowHandlerRequest);

        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowDate.plusDays(15);

        Borrowing borrowing = Borrowing.builder()
            .bookCopy(bookCopy)
            .user(patron)
            .borrowDate(borrowDate)
            .dueDate(dueDate)
            .processedByStaff(librarian)
            .status(BorrowStatus.BORROWED)
        .build();

        bookCopy.setStatus(BookCopyStatus.CHECKED_OUT);
        waitList.setStatus(WaitListStatus.COMPLETED);
        waitList.setEndDate(LocalDateTime.now());

        waitListService.saveWaitList(waitList);
        bookCopyService.saveBookCopy(bookCopy);

        Borrowing savedBorrowing = borrowingRepository.save(borrowing);

        publishBookAvailabilityUpdateEvent(bookCopy);

        return BorrowMapper.INSTANCE.borrowToBorrowDto(savedBorrowing);
    }

    @Override
    public BorrowDto returnBookCopyByBarcode(
        String barcode,
        BorrowReturnRequest borrowReturnRequest,
        UserDetailsImpl userDetails
    ) {
        User patron = userService.getUserByIdOrElseThrow(borrowReturnRequest.patronId());
        User librarian = userService.getUserByIdOrElseThrow(userDetails.getId());

        BookCopy bookCopy = bookCopyService.getBookCopyByBarcodeOrElseThrow(barcode);
        Book book = bookCopy.getBook();

        Borrowing borrowing = borrowingRepository.findByStatusAndBookCopy_BarcodeAndUser_Id(
            BorrowStatus.BORROWED,
            barcode,
            patron.getId()
        );

        ReturnHandlerRequest returnHandlerRequest = new ReturnHandlerRequest(
            bookCopy,
            book,
            patron,
            borrowing
        );

        returnValidationHandler.handle(returnHandlerRequest);

        bookCopy.setStatus(borrowReturnRequest.returnType().getBookCopyStatus());

        LocalDateTime returnDateTime = LocalDateTime.now();

        Borrowing returnBorrowing = Borrowing.builder()
            .bookCopy(bookCopy)
            .returnDate(returnDateTime)
            .processedByStaff(librarian)
            .user(patron)
            .status(borrowReturnRequest.returnType().getBorrowStatus())
            .build();

        Borrowing savedReturnBorrowing = borrowingRepository.save(returnBorrowing);

        publishBookAvailabilityUpdateEvent(bookCopy);

        return BorrowMapper.INSTANCE.borrowToBorrowDto(savedReturnBorrowing);
    }

    private void publishBookAvailabilityUpdateEvent(BookCopy bookCopy) {
        int newAvailableCount = bookCopyService.countByIdAndStatus(bookCopy.getBook().getId(), BookCopyStatus.AVAILABLE);

        BookAvailabilityUpdateEvent event =
                new BookAvailabilityUpdateEvent(bookCopy.getBook().getId(), newAvailableCount);

        bookAvailabilitySink.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
