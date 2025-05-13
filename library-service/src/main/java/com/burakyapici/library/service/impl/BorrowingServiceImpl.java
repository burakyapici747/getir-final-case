package com.burakyapici.library.service.impl;

import com.burakyapici.library.api.dto.request.BookAvailabilityUpdateEvent;
import com.burakyapici.library.api.dto.request.BorrowBookCopyRequest;
import com.burakyapici.library.api.dto.request.BorrowReturnRequest;
import com.burakyapici.library.common.mapper.BorrowMapper;
import com.burakyapici.library.domain.dto.BorrowingDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.enums.BorrowStatus;
import com.burakyapici.library.domain.enums.ReturnType;
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
import java.util.Optional;
import java.util.UUID;

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
    public BorrowingDto borrowBookCopyByBarcode(
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

        return BorrowMapper.INSTANCE.toBorrowingDto(savedBorrowing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowingDto returnBookCopyByBarcode(
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

        BookCopyStatus bookCopyStatus = processWaitListAndReturnBookCopyStatus(book.getId(), bookCopy, borrowReturnRequest.returnType());

        bookCopy.setStatus(bookCopyStatus);
        bookCopyService.saveBookCopy(bookCopy);

        LocalDateTime returnDateTime = LocalDateTime.now();

        borrowing.setReturnDate(returnDateTime);
        borrowing.setStatus(borrowReturnRequest.returnType().getBorrowStatus());
        borrowing.setReturnedByStaff(librarian);

        Borrowing savedBorrowing = borrowingRepository.save(borrowing);

        publishBookAvailabilityUpdateEvent(bookCopy);

        return BorrowMapper.INSTANCE.toBorrowingDto(savedBorrowing);
    }

    private BookCopyStatus processWaitListAndReturnBookCopyStatus(UUID bookId, BookCopy bookCopy, ReturnType returnType) {
        if(ReturnType.NORMAL.equals(returnType)) {
            Optional<WaitList> waitListSuitableToProcessOptional =
                    waitListService.getTopByBookIdAndStatusOrderByStartDateAsc(bookId, WaitListStatus.WAITING);
            if(waitListSuitableToProcessOptional.isPresent()) {
                LocalDateTime endLocalDateTime = LocalDateTime.now().plusDays(3L);
                WaitList waitList = waitListSuitableToProcessOptional.get();
                waitList.setStatus(WaitListStatus.READY_FOR_PICKUP);
                waitList.setReservedBookCopy(bookCopy);
                waitList.setEndDate(endLocalDateTime);
                waitListService.saveWaitList(waitList);
                return BookCopyStatus.ON_HOLD;
            }
        }
        return returnType.getBookCopyStatus();
    }

    @Override
    public void deleteAllByBookCopyId(UUID bookCopyId) {
        borrowingRepository.deleteAllByBookCopyId(bookCopyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllByBookId(UUID bookId) {
        borrowingRepository.deleteAllByBookCopyBookId(bookId);
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
