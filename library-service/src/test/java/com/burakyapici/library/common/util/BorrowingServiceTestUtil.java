package com.burakyapici.library.common.util;

import com.burakyapici.library.api.dto.request.BookAvailabilityUpdateEvent;
import com.burakyapici.library.api.dto.request.BorrowBookCopyRequest;
import com.burakyapici.library.api.dto.request.BorrowReturnRequest;
import com.burakyapici.library.domain.dto.BorrowingDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.enums.BorrowStatus;
import com.burakyapici.library.domain.enums.ReturnType;
import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.Borrowing;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.model.WaitList;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.validation.borrowing.BorrowHandlerRequest;
import com.burakyapici.library.service.validation.returning.ReturnHandlerRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class BorrowingServiceTestUtil {
    
    public static BorrowBookCopyRequest createSampleBorrowBookCopyRequest() {
        return new BorrowBookCopyRequest(UUID.randomUUID());
    }
    
    public static BorrowBookCopyRequest createSampleBorrowBookCopyRequest(UUID patronId) {
        return new BorrowBookCopyRequest(patronId);
    }
    
    public static BorrowReturnRequest createSampleBorrowReturnRequest() {
        return new BorrowReturnRequest(UUID.randomUUID(), ReturnType.NORMAL);
    }
    
    public static BorrowReturnRequest createSampleBorrowReturnRequest(UUID patronId, ReturnType returnType) {
        return new BorrowReturnRequest(patronId, returnType);
    }
    
    public static Borrowing createSampleBorrowing(User patron, User librarian, BookCopy bookCopy) {
        LocalDateTime borrowDate = LocalDateTime.now().minusDays(5);
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
    
    public static Borrowing createSampleBorrowingWithId(UUID borrowingId, User patron, User librarian, BookCopy bookCopy) {
        Borrowing borrowing = createSampleBorrowing(patron, librarian, bookCopy);
        borrowing.setId(borrowingId);
        return borrowing;
    }
    
    public static Borrowing createSampleReturnedBorrowing(User patron, User librarian, BookCopy bookCopy) {
        LocalDateTime borrowDate = LocalDateTime.now().minusDays(10);
        LocalDateTime dueDate = borrowDate.plusDays(15);
        LocalDateTime returnDate = LocalDateTime.now();
        
        Borrowing borrowing = Borrowing.builder()
                .user(patron)
                .bookCopy(bookCopy)
                .borrowDate(borrowDate)
                .dueDate(dueDate)
                .returnDate(returnDate)
                .borrowedByStaff(librarian)
                .returnedByStaff(librarian)
                .status(BorrowStatus.RETURNED)
                .build();
        
        borrowing.setId(UUID.randomUUID());
        return borrowing;
    }
    
    public static BorrowingDto createSampleBorrowingDto(Borrowing borrowing) {
        Book book = borrowing.getBookCopy().getBook();
        String borrowedByStaffName = borrowing.getBorrowedByStaff().getFirstName() + " " + borrowing.getBorrowedByStaff().getLastName();
        String returnedByStaffName = null;
        if (borrowing.getReturnedByStaff() != null) {
            returnedByStaffName = borrowing.getReturnedByStaff().getFirstName() + " " + borrowing.getReturnedByStaff().getLastName();
        }
        
        return new BorrowingDto(
                borrowing.getId(),
                borrowing.getUser().getId(),
                borrowing.getUser().getEmail(),
                borrowing.getUser().getFirstName(),
                borrowing.getUser().getLastName(),
                borrowing.getBookCopy().getId(),
                borrowing.getBookCopy().getBarcode(),
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                borrowing.getBorrowedByStaff().getId(),
                borrowedByStaffName,
                borrowing.getReturnedByStaff() != null ? borrowing.getReturnedByStaff().getId() : null,
                returnedByStaffName,
                borrowing.getBorrowDate(),
                borrowing.getDueDate(),
                borrowing.getReturnDate(),
                borrowing.getStatus()
        );
    }
    
    public static UserDetailsImpl createSampleLibrarianUserDetails() {
        return UserDetailsImpl.builder()
                .id(UUID.randomUUID())
                .email("librarian@example.com")
                .password("password")
                .build();
    }
    
    public static BorrowHandlerRequest createSampleBorrowHandlerRequest(User patron, Book book, BookCopy bookCopy, Optional<WaitList> waitList) {
        return new BorrowHandlerRequest(patron, book, bookCopy, waitList);
    }
    
    public static ReturnHandlerRequest createSampleReturnHandlerRequest(BookCopy bookCopy, Book book, User patron, Borrowing borrowing) {
        return new ReturnHandlerRequest(bookCopy, book, patron, borrowing);
    }
    
    public static BookAvailabilityUpdateEvent createSampleBookAvailabilityUpdateEvent(UUID bookId, int newAvailableCount) {
        return new BookAvailabilityUpdateEvent(bookId, newAvailableCount);
    }
    
    public static WaitList createSampleWaitList(User user, Book book, BookCopy reservedBookCopy, WaitListStatus status) {
        WaitList waitList = new WaitList();
        waitList.setId(UUID.randomUUID());
        waitList.setUser(user);
        waitList.setBook(book);
        waitList.setReservedBookCopy(reservedBookCopy);
        waitList.setStartDate(LocalDateTime.now().minusDays(5));
        waitList.setStatus(status);
        
        if (status == WaitListStatus.READY_FOR_PICKUP) {
            waitList.setEndDate(LocalDateTime.now().plusDays(3));
        } else if (status == WaitListStatus.COMPLETED || status == WaitListStatus.CANCELLED || status == WaitListStatus.EXPIRED) {
            waitList.setEndDate(LocalDateTime.now());
        }
        
        return waitList;
    }
}
