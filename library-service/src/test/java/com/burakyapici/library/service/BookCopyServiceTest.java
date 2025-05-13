package com.burakyapici.library.service;

import com.burakyapici.library.api.advice.DataConflictException;
import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.api.dto.request.BookAvailabilityUpdateEvent;
import com.burakyapici.library.api.dto.request.BookCopyCreateRequest;
import com.burakyapici.library.api.dto.request.BookCopySearchCriteria;
import com.burakyapici.library.api.dto.request.BookCopyUpdateRequest;
import com.burakyapici.library.common.util.BookCopyServiceTestUtil;
import com.burakyapici.library.common.util.BookServiceTestUtil;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.WaitList;
import com.burakyapici.library.domain.repository.BookCopyRepository;
import com.burakyapici.library.domain.specification.BookCopySpecifications;
import com.burakyapici.library.service.impl.BookCopyServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookCopyServiceTest {

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private BookService bookService;

    @Mock
    private WaitListService waitListService;

    @Mock
    private BorrowingService borrowingService;

    @Mock
    private Sinks.Many<BookAvailabilityUpdateEvent> bookAvailabilitySink;

    @InjectMocks
    private BookCopyServiceImpl bookCopyService;

    @Test
    @DisplayName("Given book copy id, when getBookCopyById, then return book copy dto")
    public void givenBookCopyId_whenGetBookCopyById_thenReturnBookCopyDto() {
        UUID bookCopyId = UUID.randomUUID();
        BookCopy bookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithId(bookCopyId);

        when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));

        BookCopyDto result = bookCopyService.getBookCopyById(bookCopyId);

        assertNotNull(result);
        assertEquals(bookCopyId, result.id());
        assertEquals(bookCopy.getBarcode(), result.barcode());
        assertEquals(bookCopy.getStatus(), result.status());
        verify(bookCopyRepository).findById(bookCopyId);
    }

    @Test
    @DisplayName("Given non-existing book copy id, when getBookCopyById, then throw EntityNotFoundException")
    public void givenNonExistingBookCopyId_whenGetBookCopyById_thenThrowEntityNotFoundException() {
        UUID bookCopyId = UUID.randomUUID();

        when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookCopyService.getBookCopyById(bookCopyId));
        verify(bookCopyRepository).findById(bookCopyId);
    }

    @Test
    @DisplayName("Given book copy id, when deleteBookCopyById, then delete book copy and related entities")
    public void givenBookCopyId_whenDeleteBookCopyById_thenDeleteBookCopyAndRelatedEntities() {
        UUID bookCopyId = UUID.randomUUID();
        BookCopy bookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithId(bookCopyId);

        when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));

        bookCopyService.deleteBookCopyById(bookCopyId);

        verify(bookCopyRepository).findById(bookCopyId);
        verify(waitListService).deleteByBookCopyId(bookCopyId);
        verify(borrowingService).deleteAllByBookCopyId(bookCopyId);
        verify(bookCopyRepository).delete(bookCopy);
    }

    @Test
    @DisplayName("Given book id and status, when countByIdAndStatus, then return count")
    public void givenBookIdAndStatus_whenCountByIdAndStatus_thenReturnCount() {
        UUID bookId = UUID.randomUUID();
        BookCopyStatus status = BookCopyStatus.AVAILABLE;
        int expectedCount = 5;

        when(bookCopyRepository.countByBook_IdAndStatus(bookId, status)).thenReturn(expectedCount);

        int result = bookCopyService.countByIdAndStatus(bookId, status);

        assertEquals(expectedCount, result);
        verify(bookCopyRepository).countByBook_IdAndStatus(bookId, status);
    }

    @Test
    @DisplayName("Given book copy, when saveBookCopy, then return saved book copy")
    public void givenBookCopy_whenSaveBookCopy_thenReturnSavedBookCopy() {
        BookCopy bookCopy = BookCopyServiceTestUtil.createSampleBookCopy();
        BookCopy savedBookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithId(UUID.randomUUID());

        when(bookCopyRepository.save(bookCopy)).thenReturn(savedBookCopy);

        BookCopy result = bookCopyService.saveBookCopy(bookCopy);

        assertNotNull(result);
        assertEquals(savedBookCopy, result);
        verify(bookCopyRepository).save(bookCopy);
    }

    @Test
    @DisplayName("Given search criteria and page parameters, when searchBookCopies, then return pageable dto")
    public void givenSearchCriteriaAndPageParameters_whenSearchBookCopies_thenReturnPageableDto() {
        BookCopySearchCriteria criteria = BookCopyServiceTestUtil.createSampleBookCopySearchCriteria("BC", BookCopyStatus.AVAILABLE);
        int currentPage = 0;
        int pageSize = 10;

        List<BookCopy> bookCopies = BookCopyServiceTestUtil.createSampleBookCopies(3);
        Page<BookCopy> bookCopyPage = BookCopyServiceTestUtil.createBookCopyPage(bookCopies, currentPage, pageSize, bookCopies.size());

        try (MockedStatic<BookCopySpecifications> mockedBookCopySpecifications = Mockito.mockStatic(BookCopySpecifications.class)) {
            Specification<BookCopy> specification = mock(Specification.class);
            mockedBookCopySpecifications.when(() -> BookCopySpecifications.findByCriteria(criteria)).thenReturn(specification);

            when(bookCopyRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(bookCopyPage);

            PageableDto<BookCopyDto> result = bookCopyService.searchBookCopies(criteria, currentPage, pageSize);

            assertNotNull(result);
            assertEquals(bookCopies.size(), result.elements().size());
            assertEquals(currentPage, result.currentPage());
            verify(bookCopyRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    @DisplayName("Given book id, when deleteAllByBookId, then delete all book copies")
    public void givenBookId_whenDeleteAllByBookId_thenDeleteAllBookCopies() {
        UUID bookId = UUID.randomUUID();

        bookCopyService.deleteAllByBookId(bookId);

        verify(bookCopyRepository).deleteByBookId(bookId);
    }

    @Test
    @DisplayName("Given book id and status, when findByBookIdAndStatus, then return matching book copies")
    public void givenBookIdAndStatus_whenFindByBookIdAndStatus_thenReturnMatchingBookCopies() {
        UUID bookId = UUID.randomUUID();
        BookCopyStatus status = BookCopyStatus.AVAILABLE;
        List<BookCopy> expectedBookCopies = BookCopyServiceTestUtil.createSampleBookCopies(3);

        when(bookCopyRepository.findByBookIdAndStatus(bookId, status.name())).thenReturn(expectedBookCopies);

        List<BookCopy> result = bookCopyService.findByBookIdAndStatus(bookId, status);

        assertNotNull(result);
        assertEquals(expectedBookCopies.size(), result.size());
        assertEquals(expectedBookCopies, result);
        verify(bookCopyRepository).findByBookIdAndStatus(bookId, status.name());
    }

    @Test
    @DisplayName("Given book copy id, when getBookCopyByIdOrElseThrow, then return book copy")
    public void givenBookCopyId_whenGetBookCopyByIdOrElseThrow_thenReturnBookCopy() {
        UUID bookCopyId = UUID.randomUUID();
        BookCopy expectedBookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithId(bookCopyId);

        when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(expectedBookCopy));

        BookCopy result = bookCopyService.getBookCopyByIdOrElseThrow(bookCopyId);

        assertNotNull(result);
        assertEquals(expectedBookCopy, result);
        verify(bookCopyRepository).findById(bookCopyId);
    }

    @Test
    @DisplayName("Given barcode, when getBookCopyByBarcodeOrElseThrow, then return book copy")
    public void givenBarcode_whenGetBookCopyByBarcodeOrElseThrow_thenReturnBookCopy() {
        String barcode = "BC-12345";
        BookCopy expectedBookCopy = BookCopyServiceTestUtil.createSampleBookCopy();

        when(bookCopyRepository.findByBarcode(barcode)).thenReturn(Optional.of(expectedBookCopy));

        BookCopy result = bookCopyService.getBookCopyByBarcodeOrElseThrow(barcode);

        assertNotNull(result);
        assertEquals(expectedBookCopy, result);
        verify(bookCopyRepository).findByBarcode(barcode);
    }

    @Test
    @DisplayName("Given non-existing barcode, when getBookCopyByBarcodeOrElseThrow, then throw EntityNotFoundException")
    public void givenNonExistingBarcode_whenGetBookCopyByBarcodeOrElseThrow_thenThrowEntityNotFoundException() {
        String barcode = "NON-EXISTING";

        when(bookCopyRepository.findByBarcode(barcode)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookCopyService.getBookCopyByBarcodeOrElseThrow(barcode));
        verify(bookCopyRepository).findByBarcode(barcode);
    }

    @Test
    @DisplayName("Given page parameters, when getAllBookCopies, then return pageable dto")
    public void givenPageParameters_whenGetAllBookCopies_thenReturnPageableDto() {
        int currentPage = 0;
        int pageSize = 10;

        List<BookCopy> bookCopies = BookCopyServiceTestUtil.createSampleBookCopies(3);
        Page<BookCopy> bookCopyPage = BookCopyServiceTestUtil.createBookCopyPage(bookCopies, currentPage, pageSize, bookCopies.size());

        when(bookCopyRepository.findAll(any(Pageable.class))).thenReturn(bookCopyPage);

        PageableDto<BookCopyDto> result = bookCopyService.getAllBookCopies(currentPage, pageSize);

        assertNotNull(result);
        assertEquals(bookCopies.size(), result.elements().size());
        assertEquals(currentPage, result.currentPage());
        verify(bookCopyRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Given book id and page parameters, when getAllBookCopiesByBookId, then return pageable dto")
    public void givenBookIdAndPageParameters_whenGetAllBookCopiesByBookId_thenReturnPageableDto() {
        UUID bookId = UUID.randomUUID();
        int currentPage = 0;
        int pageSize = 10;

        List<BookCopy> bookCopies = BookCopyServiceTestUtil.createSampleBookCopies(3);
        Page<BookCopy> bookCopyPage = BookCopyServiceTestUtil.createBookCopyPage(bookCopies, currentPage, pageSize, bookCopies.size());

        when(bookCopyRepository.findAllByBookId(eq(bookId), any(Pageable.class))).thenReturn(bookCopyPage);

        PageableDto<BookCopyDto> result = bookCopyService.getAllBookCopiesByBookId(bookId, currentPage, pageSize);

        assertNotNull(result);
        assertEquals(bookCopies.size(), result.elements().size());
        assertEquals(0, result.currentPage());
        verify(bookCopyRepository).findAllByBookId(eq(bookId), any(Pageable.class));
    }

    @Test
    @DisplayName("Given create request, when createBookCopy, then return created book copy dto")
    public void givenCreateRequest_whenCreateBookCopy_thenReturnCreatedBookCopyDto() {
        UUID bookId = UUID.randomUUID();
        Book book = BookServiceTestUtil.createSampleBookWithId(bookId);
        BookCopyCreateRequest createRequest = BookCopyServiceTestUtil.createSampleBookCopyCreateRequest(bookId);
        BookCopy createdBookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithId(UUID.randomUUID());

        when(bookCopyRepository.existsByBarcode(createRequest.barcode())).thenReturn(false);
        when(bookService.getBookByIdOrElseThrow(bookId)).thenReturn(book);
        when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(createdBookCopy);
        when(waitListService.getTopByBookIdAndStatusOrderByStartDateAsc(eq(bookId), eq(WaitListStatus.WAITING)))
                .thenReturn(Optional.empty());
        when(bookCopyService.countByIdAndStatus(bookId, BookCopyStatus.AVAILABLE)).thenReturn(1);

        BookCopyDto result = bookCopyService.createBookCopy(createRequest);

        assertNotNull(result);
        assertEquals(createdBookCopy.getId(), result.id());
        assertEquals(createdBookCopy.getBarcode(), result.barcode());
        assertEquals(createdBookCopy.getStatus(), result.status());
        verify(bookCopyRepository).existsByBarcode(createRequest.barcode());
        verify(bookService).getBookByIdOrElseThrow(bookId);
        verify(bookCopyRepository).save(any(BookCopy.class));
        verify(bookAvailabilitySink).emitNext(any(BookAvailabilityUpdateEvent.class), any());
    }

    @Test
    @DisplayName("Given existing barcode, when createBookCopy, then throw DataConflictException")
    public void givenExistingBarcode_whenCreateBookCopy_thenThrowDataConflictException() {
        UUID bookId = UUID.randomUUID();
        BookCopyCreateRequest createRequest = BookCopyServiceTestUtil.createSampleBookCopyCreateRequest(bookId);

        when(bookCopyRepository.existsByBarcode(createRequest.barcode())).thenReturn(true);

        assertThrows(DataConflictException.class, () -> bookCopyService.createBookCopy(createRequest));
        verify(bookCopyRepository).existsByBarcode(createRequest.barcode());
        verify(bookService, never()).getBookByIdOrElseThrow(any());
    }

    @Test
    @DisplayName("Given create request with waiting list entry, when createBookCopy, then update wait list and book copy status")
    public void givenCreateRequestWithWaitingListEntry_whenCreateBookCopy_thenUpdateWaitListAndBookCopyStatus() {
        UUID bookId = UUID.randomUUID();
        Book book = BookServiceTestUtil.createSampleBookWithId(bookId);
        BookCopyCreateRequest createRequest = BookCopyServiceTestUtil.createSampleBookCopyCreateRequest(bookId);
        BookCopy createdBookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithId(UUID.randomUUID());
        WaitList waitList = BookCopyServiceTestUtil.createSampleWaitList(book, null, WaitListStatus.WAITING);

        when(bookCopyRepository.existsByBarcode(createRequest.barcode())).thenReturn(false);
        when(bookService.getBookByIdOrElseThrow(bookId)).thenReturn(book);
        when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(createdBookCopy);
        when(waitListService.getTopByBookIdAndStatusOrderByStartDateAsc(eq(bookId), eq(WaitListStatus.WAITING)))
                .thenReturn(Optional.of(waitList));
        when(bookCopyService.countByIdAndStatus(bookId, BookCopyStatus.AVAILABLE)).thenReturn(1);

        BookCopyDto result = bookCopyService.createBookCopy(createRequest);

        assertNotNull(result);
        assertEquals(createdBookCopy.getId(), result.id());
        assertEquals(createdBookCopy.getBarcode(), result.barcode());
        assertEquals(createdBookCopy.getStatus(), result.status());
        verify(bookCopyRepository).existsByBarcode(createRequest.barcode());
        verify(bookService).getBookByIdOrElseThrow(bookId);
        verify(bookCopyRepository).save(any(BookCopy.class));
        verify(bookAvailabilitySink).emitNext(any(BookAvailabilityUpdateEvent.class), any());
    }

    @Test
    @DisplayName("Given book copy id and update request, when updateBookCopyById, then return updated book copy dto")
    public void givenBookCopyIdAndUpdateRequest_whenUpdateBookCopyById_thenReturnUpdatedBookCopyDto() {
        UUID bookCopyId = UUID.randomUUID();
        BookCopy bookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithId(bookCopyId);
        BookCopyUpdateRequest updateRequest = BookCopyServiceTestUtil.createSampleBookCopyUpdateRequest(BookCopyStatus.CHECKED_OUT);
        BookCopy updatedBookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithId(bookCopyId);

        when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
        when(bookCopyRepository.save(bookCopy)).thenReturn(updatedBookCopy);

        BookCopyDto result = bookCopyService.updateBookCopyById(bookCopyId, updateRequest);

        assertNotNull(result);
        assertEquals(updatedBookCopy.getId(), result.id());
        assertEquals(updatedBookCopy.getBarcode(), result.barcode());
        assertEquals(updatedBookCopy.getStatus(), result.status());
        verify(bookCopyRepository).findById(bookCopyId);
        verify(bookCopyRepository).save(bookCopy);
    }
}
