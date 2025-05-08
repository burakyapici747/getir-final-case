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
import com.burakyapici.library.service.validation.BorrowHandlerRequest;
import com.burakyapici.library.service.validation.BorrowValidationHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BorrowingServiceImpl implements BorrowingService {
    private final UserService userService;
    private final BookCopyService bookCopyService;
    private final WaitListService waitListService;
    private final BorrowingRepository borrowingRepository;
    private final BorrowValidationHandler borrowValidationHandler;
    private final Sinks.Many<BookAvailabilityUpdateEvent> bookAvailabilitySink;

    public BorrowingServiceImpl(
            BorrowingRepository borrowingRepository,
            BookCopyService bookCopyService,
            WaitListService waitListService, UserService userService,
            @Qualifier("borrowValidationChain")
        BorrowValidationHandler borrowValidationHandler, Sinks.Many<BookAvailabilityUpdateEvent> bookAvailabilitySink
    ) {
        this.borrowingRepository = borrowingRepository;
        this.bookCopyService = bookCopyService;
        this.waitListService = waitListService;
        this.userService = userService;
        this.borrowValidationHandler = borrowValidationHandler;
        this.bookAvailabilitySink = bookAvailabilitySink;
    }

    @Override
    public BorrowDto borrowBookCopyByBarcode(
        String barcode,
        BorrowBookCopyRequest borrowBookCopyRequest,
        UserDetailsImpl userDetails
    ) {
        User patron = userService.getUserByIdOrElseThrow(borrowBookCopyRequest.patronId());
        User librarian = userService.getUserByIdOrElseThrow(userDetails.getId());

        BookCopy bookCopy = bookCopyService.getBookCopyByBarcodeOrElseThrow(barcode);

        Book book = bookCopy.getBook();

        Optional<WaitList> waitListOptional = waitListService.getByUserIdAndBookIdAndStatus(
            patron.getId(),
            book.getId(),
            WaitListStatus.WAITING
        );

        BorrowHandlerRequest borrowHandlerRequest = new BorrowHandlerRequest(
            bookCopy,
            book,
            patron,
            waitListOptional
        );

        borrowValidationHandler.handle(borrowHandlerRequest);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(15);

        Borrowing borrowing = Borrowing.builder()
            .bookCopy(bookCopy)
            .user(patron)
            .borrowDate(now)
            .dueDate(dueDate)
            .processedByStaff(librarian)
            .status(BorrowStatus.BORROWED)
        .build();

        bookCopy.setStatus(BookCopyStatus.CHECKED_OUT);

        int newAvailableCount = bookCopyService.countByIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE);

        BookAvailabilityUpdateEvent event = new BookAvailabilityUpdateEvent(book.getId(), newAvailableCount);

        bookAvailabilitySink.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);

        return BorrowMapper.INSTANCE.toDto(borrowingRepository.save(borrowing));
    }

    @Override
    public BorrowDto returnBookCopyByBarcode(String barcode, BorrowReturnRequest borrowReturnRequest, UserDetailsImpl userDetails) {
        User patron = userService.getUserByIdOrElseThrow(borrowReturnRequest.patronId());
        User librarian = userService.getUserByIdOrElseThrow(userDetails.getId());

        BookCopy bookCopy = bookCopyService.getBookCopyByBarcodeOrElseThrow(barcode);

        Borrowing borrowing = borrowingRepository.findByStatusAndBookCopy_BarcodeAndUser_Id(
            BorrowStatus.BORROWED,
            barcode,
            patron.getId()
        ).orElseThrow(() -> new IllegalArgumentException("Borrowing not found"));

        if(borrowReturnRequest.isLost()){
            borrowing.setStatus(BorrowStatus.LOST);
            bookCopy.setStatus(BookCopyStatus.LOST);
        }else if(borrowReturnRequest.damageReportedDuringReturn()){
            borrowing.setDamageReportedDuringReturn(true);
            borrowing.setDamageNotesDuringReturn(borrowReturnRequest.damageNotesDuringReturn());
            bookCopy.setStatus(BookCopyStatus.IN_REPAIR);
        }else{
            bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        }

        LocalDateTime returnDate = LocalDateTime.now();

        borrowing = Borrowing.builder()
            .returnDate(returnDate)
            .processedByStaff(librarian)
            .bookCopy(bookCopy)
            .build();

        return BorrowMapper.INSTANCE.toDto(borrowingRepository.save(borrowing));
    }
}
