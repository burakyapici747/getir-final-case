package com.burakyapici.library.common.util;

import com.burakyapici.library.api.dto.request.PlaceHoldRequest;
import com.burakyapici.library.domain.dto.WaitListDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.enums.BookStatus;
import com.burakyapici.library.domain.enums.PatronStatus;
import com.burakyapici.library.domain.enums.Role;
import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.model.WaitList;
import com.burakyapici.library.service.validation.waitlist.PlaceHoldHandlerRequest;
import com.burakyapici.library.service.validation.waitlist.cancel.CancelHoldHandlerRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WaitListServiceTestUtil {

    public static User createSampleUser(UUID userId, PatronStatus status) {
        User user = new User();
        user.setId(userId);
        user.setFirstName("Test User");
        user.setLastName("Last Name");
        user.setEmail("testuser@example.com");
        user.setPasswordHash("hashedpassword");
        user.setPhoneNumber("1234567890");
        user.setAddress("Test Address");
        user.setRole(Role.PATRON);
        user.setPatronStatus(status);
        return user;
    }

    public static User createSampleUser() {
        return createSampleUser(UUID.randomUUID(), PatronStatus.ACTIVE);
    }

    public static Book createSampleBook(UUID bookId, BookStatus status) {
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Sample Book for Waitlist");
        book.setIsbn("978-3-16-148410-0");
        book.setBookStatus(status);
        book.setPage(200);
        book.setPublicationDate(LocalDate.now());
        book.setAuthors(Collections.emptySet());
        book.setGenres(Collections.emptySet());
        return book;
    }
    
    public static Book createSampleBook(UUID bookId) {
        return createSampleBook(bookId, BookStatus.ACTIVE);
    }

    public static Book createSampleBook() {
        return createSampleBook(UUID.randomUUID(), BookStatus.ACTIVE);
    }

    public static BookCopy createSampleBookCopy(Book book, BookCopyStatus status) {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(UUID.randomUUID());
        bookCopy.setBook(book);
        bookCopy.setStatus(status);
        bookCopy.setBarcode(UUID.randomUUID().toString().substring(0,12));
        return bookCopy;
    }
    
    public static List<BookCopy> createSampleBookCopies(Book book, BookCopyStatus status, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createSampleBookCopy(book, status))
                .collect(Collectors.toList());
    }

    public static WaitList createSampleWaitList(User user, Book book, WaitListStatus status) {
        WaitList waitList = new WaitList();
        waitList.setId(UUID.randomUUID());
        waitList.setUser(user);
        waitList.setBook(book);
        waitList.setStartDate(LocalDateTime.now().minusDays(1));
        waitList.setStatus(status);
        
        if (status == WaitListStatus.READY_FOR_PICKUP) {
            BookCopy reservedCopy = createSampleBookCopy(book, BookCopyStatus.ON_HOLD);
            waitList.setReservedBookCopy(reservedCopy);
        } else if (status == WaitListStatus.CANCELLED || status == WaitListStatus.EXPIRED || status == WaitListStatus.COMPLETED) {
            waitList.setEndDate(LocalDateTime.now());
        }
        return waitList;
    }
    
    public static WaitList createSampleWaitList(User user, Book book) {
        return createSampleWaitList(user, book, WaitListStatus.WAITING);
    }
    
    public static List<WaitList> createSampleWaitLists(User user, Book book, WaitListStatus status, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createSampleWaitList(user, book, status))
                .collect(Collectors.toList());
    }

    public static PlaceHoldRequest createSamplePlaceHoldRequest(UUID bookId) {
        return new PlaceHoldRequest(bookId);
    }
    
    public static WaitListDto createSampleWaitListDto(WaitList waitList) {
        // Assuming WaitListDto has fields: id, userId, bookId, status, startDate, endDate
        // Based on your previous attachment, WaitListDto might only have: id, startDate, endDate, status
        // Adjusting to the simpler DTO structure shown in your diff for WaitListServiceTestUtil.java
        return new WaitListDto(
            waitList.getId(),
            // waitList.getUser() != null ? waitList.getUser().getId() : null, // Not in the simplified DTO
            // waitList.getBook() != null ? waitList.getBook().getId() : null, // Not in the simplified DTO
            waitList.getStartDate() != null ? waitList.getStartDate().toString() : null,
            waitList.getEndDate() != null ? waitList.getEndDate().toString() : null,
            waitList.getStatus()
        );
    }
    
    public static List<WaitListDto> createSampleWaitListDtos(List<WaitList> waitLists) {
        return waitLists.stream()
                .map(WaitListServiceTestUtil::createSampleWaitListDto)
                .collect(Collectors.toList());
    }
    
    public static PlaceHoldHandlerRequest createSamplePlaceHoldHandlerRequest(
            User user, 
            Book book, 
            UUID bookId, 
            Optional<BookCopy> bookCopy, 
            List<BookCopy> availableCopies) {
        // The PlaceHoldHandlerRequest record in your attachments shows Optional<Integer> waitListCount, not Optional<BookCopy>
        // Reverting to Optional<Integer> as per the record definition you provided earlier.
        // If PlaceHoldHandlerRequest has indeed changed to Optional<BookCopy>, this needs to be updated there first.
        return new PlaceHoldHandlerRequest(user, book, bookId, Optional.empty(), availableCopies); // Assuming waitListCount is Optional.empty() for simplicity
    }
    
    public static CancelHoldHandlerRequest createSampleCancelHoldHandlerRequest(
            User user,
            WaitList waitList, 
            UUID waitListId) {
        return new CancelHoldHandlerRequest(user, waitList, waitListId);
    }
}
