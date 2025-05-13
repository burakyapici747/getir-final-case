package com.burakyapici.library.common.util;

import com.burakyapici.library.api.dto.request.BookAvailabilityUpdateEvent;
import com.burakyapici.library.api.dto.request.BookCopyCreateRequest;
import com.burakyapici.library.api.dto.request.BookCopySearchCriteria;
import com.burakyapici.library.api.dto.request.BookCopyUpdateRequest;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.WaitList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BookCopyServiceTestUtil {

    public static BookCopy createSampleBookCopy() {
        Book book = BookServiceTestUtil.createSampleBookWithId(UUID.randomUUID());
        
        BookCopy bookCopy = new BookCopy();
        bookCopy.setBarcode("BC-12345");
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopy.setBook(book);
        bookCopy.setBorrowings(new HashSet<>());
        bookCopy.setWaitLists(new HashSet<>());
        
        return bookCopy;
    }

    public static BookCopy createSampleBookCopyWithId(UUID bookCopyId) {
        BookCopy bookCopy = createSampleBookCopy();
        bookCopy.setId(bookCopyId);
        return bookCopy;
    }

    public static BookCopy createSampleBookCopyWithBookAndStatus(Book book, BookCopyStatus status) {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(UUID.randomUUID());
        bookCopy.setBarcode("BC-" + UUID.randomUUID().toString().substring(0, 8));
        bookCopy.setStatus(status);
        bookCopy.setBook(book);
        bookCopy.setBorrowings(new HashSet<>());
        bookCopy.setWaitLists(new HashSet<>());
        
        return bookCopy;
    }

    public static List<BookCopy> createSampleBookCopies(int count) {
        Book book = BookServiceTestUtil.createSampleBookWithId(UUID.randomUUID());
        
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    BookCopy bookCopy = new BookCopy();
                    bookCopy.setId(UUID.randomUUID());
                    bookCopy.setBarcode("BC-" + (1000 + i));
                    bookCopy.setStatus(i % 2 == 0 ? BookCopyStatus.AVAILABLE : BookCopyStatus.CHECKED_OUT);
                    bookCopy.setBook(book);
                    bookCopy.setBorrowings(new HashSet<>());
                    bookCopy.setWaitLists(new HashSet<>());
                    return bookCopy;
                })
                .collect(Collectors.toList());
    }

    public static List<BookCopy> createSampleBookCopiesForBook(Book book, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    BookCopy bookCopy = new BookCopy();
                    bookCopy.setId(UUID.randomUUID());
                    bookCopy.setBarcode("BC-" + book.getId().toString().substring(0, 4) + "-" + i);
                    bookCopy.setStatus(i % 2 == 0 ? BookCopyStatus.AVAILABLE : BookCopyStatus.CHECKED_OUT);
                    bookCopy.setBook(book);
                    bookCopy.setBorrowings(new HashSet<>());
                    bookCopy.setWaitLists(new HashSet<>());
                    return bookCopy;
                })
                .collect(Collectors.toList());
    }

    public static BookCopyDto createSampleBookCopyDto(BookCopy bookCopy) {
        return new BookCopyDto(
                bookCopy.getId(),
                bookCopy.getBarcode(),
                bookCopy.getStatus()
        );
    }

    public static List<BookCopyDto> createSampleBookCopyDtos(List<BookCopy> bookCopies) {
        return bookCopies.stream()
                .map(BookCopyServiceTestUtil::createSampleBookCopyDto)
                .collect(Collectors.toList());
    }

    public static BookCopyCreateRequest createSampleBookCopyCreateRequest(UUID bookId) {
        return new BookCopyCreateRequest(
                "BC-" + UUID.randomUUID().toString().substring(0, 8),
                bookId,
                BookCopyStatus.AVAILABLE
        );
    }

    public static BookCopyUpdateRequest createSampleBookCopyUpdateRequest(BookCopyStatus status) {
        return new BookCopyUpdateRequest(status);
    }

    public static BookCopySearchCriteria createSampleBookCopySearchCriteria(String barcode, BookCopyStatus status) {
        return new BookCopySearchCriteria(barcode, status);
    }

    public static BookCopySearchCriteria createEmptyBookCopySearchCriteria() {
        return new BookCopySearchCriteria(null, null);
    }

    public static BookAvailabilityUpdateEvent createSampleBookAvailabilityUpdateEvent(UUID bookId, int newAvailableCount) {
        return new BookAvailabilityUpdateEvent(bookId, newAvailableCount);
    }

    public static Page<BookCopy> createBookCopyPage(List<BookCopy> bookCopies, int currentPage, int pageSize, long totalElements) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        return new PageImpl<>(bookCopies, pageable, totalElements);
    }

    public static PageableDto<BookCopyDto> createBookCopyPageableDto(List<BookCopyDto> bookCopyDtos, int totalPages, int elementsPerPage, int currentPage) {
        return new PageableDto<>(
                bookCopyDtos,
                totalPages,
                elementsPerPage,
                currentPage,
                currentPage < totalPages - 1,
                currentPage > 0
        );
    }

    public static WaitList createSampleWaitList(Book book, BookCopy bookCopy, WaitListStatus status) {
        WaitList waitList = new WaitList();
        waitList.setId(UUID.randomUUID());
        waitList.setBook(book);
        waitList.setStartDate(LocalDateTime.now().minusDays(2));
        waitList.setStatus(status);
        
        if (status == WaitListStatus.READY_FOR_PICKUP) {
            waitList.setReservedBookCopy(bookCopy);
            waitList.setEndDate(LocalDateTime.now().plusDays(3));
        } else if (status == WaitListStatus.CANCELLED || status == WaitListStatus.EXPIRED || status == WaitListStatus.COMPLETED) {
            waitList.setEndDate(LocalDateTime.now());
        }
        
        return waitList;
    }
}
