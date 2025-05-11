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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

        Optional<WaitList> optionalWaitList = findReadyForPickupWaitListEntry(patron, book);

        validateBorrowRequest(bookCopy, book, patron, optionalWaitList);

        Borrowing borrowing = createBorrowingRecord(patron, librarian, bookCopy);

        updateRelatedEntities(bookCopy, optionalWaitList);

        Borrowing savedBorrowing = borrowingRepository.save(borrowing);

        publishBookAvailabilityUpdateEvent(bookCopy);

        return BorrowMapper.INSTANCE.borrowToBorrowDto(savedBorrowing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowDto returnBookCopyByBarcode(
        String barcode,
        BorrowReturnRequest borrowReturnRequest,
        UserDetailsImpl userDetails
    ) {
        User patron = userService.getUserByIdOrElseThrow(borrowReturnRequest.patronId());
        User librarian = userService.getUserByIdOrElseThrow(userDetails.getId());

        BookCopy bookCopy = bookCopyService.getBookCopyByBarcodeOrElseThrow(barcode);
        Book book = bookCopy.getBook();

        Borrowing borrowing = borrowingRepository.findByStatusAndBookCopyBarcodeAndUserId(
            BorrowStatus.BORROWED.name(),
            barcode,
            patron.getId()
        );

        validateReturnRequest(bookCopy, book, patron, borrowing);

        bookCopy.setStatus(borrowReturnRequest.returnType().getBookCopyStatus());
        bookCopyService.saveBookCopy(bookCopy);

        LocalDateTime returnDateTime = LocalDateTime.now();

        borrowing.setReturnDate(returnDateTime);
        borrowing.setStatus(borrowReturnRequest.returnType().getBorrowStatus());
        borrowing.setReturnedByStaff(librarian);

        Borrowing savedBorrowing = borrowingRepository.save(borrowing);

        publishBookAvailabilityUpdateEvent(bookCopy);

        return BorrowMapper.INSTANCE.borrowToBorrowDto(savedBorrowing);
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public CompletableFuture<Void> deleteAllByBookId(UUID bookId) {
        return CompletableFuture.runAsync(() -> {
            borrowingRepository.deleteAllByBookCopyBookId(bookId);
        });
    }

    private Optional<WaitList> findReadyForPickupWaitListEntry(User patron, Book book) {
        return waitListService.getByUserIdAndBookIdAndStatus(
            patron.getId(),
            book.getId(),
            WaitListStatus.READY_FOR_PICKUP
        );
    }

    private void validateBorrowRequest(
        BookCopy bookCopy,
        Book book,
        User patron,
        Optional<WaitList> optionalWaitList
    ) {
        BorrowHandlerRequest borrowHandlerRequest = new BorrowHandlerRequest(
            patron,
            book,
            bookCopy,
            optionalWaitList
        );

        borrowValidationHandler.handle(borrowHandlerRequest);
    }

    private void validateReturnRequest(BookCopy bookCopy, Book book, User patron, Borrowing borrowing) {
        ReturnHandlerRequest returnHandlerRequest = new ReturnHandlerRequest(
            bookCopy,
            book,
            patron,
            borrowing
        );

        returnValidationHandler.handle(returnHandlerRequest);
    }

    private Borrowing createBorrowingRecord(User patron, User librarian, BookCopy bookCopy) {
        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime dueDate = borrowDate.plusDays(15);

        return Borrowing.builder()
            .user(patron)
            .bookCopy(bookCopy)
            .borrowDate(borrowDate)
            .dueDate(dueDate)
            .borrowedByStaff(librarian)
            .status(BorrowStatus.BORROWED)
            .build();
    }

    private void updateRelatedEntities(BookCopy bookCopy, Optional<WaitList> optionalWaitList) {
        bookCopy.setStatus(BookCopyStatus.CHECKED_OUT);
        bookCopyService.saveBookCopy(bookCopy);

        optionalWaitList.ifPresent(waitList -> {
            waitList.setStatus(WaitListStatus.COMPLETED);
            waitList.setEndDate(LocalDateTime.now());
            waitListService.saveWaitList(waitList);
        });
    }

    private void publishBookAvailabilityUpdateEvent(BookCopy bookCopy) {
        int newAvailableCount = bookCopyService.countByIdAndStatus(bookCopy.getBook().getId(), BookCopyStatus.AVAILABLE);

        BookAvailabilityUpdateEvent event =
            new BookAvailabilityUpdateEvent(bookCopy.getBook().getId(), newAvailableCount);

        bookAvailabilitySink.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
