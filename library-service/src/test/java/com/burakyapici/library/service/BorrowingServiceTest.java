package com.burakyapici.library.service;

import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.api.dto.request.BookAvailabilityUpdateEvent;
import com.burakyapici.library.api.dto.request.BorrowBookCopyRequest;
import com.burakyapici.library.api.dto.request.BorrowReturnRequest;
import com.burakyapici.library.common.util.BookCopyServiceTestUtil;
import com.burakyapici.library.common.util.BookServiceTestUtil;
import com.burakyapici.library.common.util.BorrowingServiceTestUtil;
import com.burakyapici.library.common.util.UserServiceTestUtil;
import com.burakyapici.library.domain.dto.BorrowingDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.enums.BorrowingStatus;
import com.burakyapici.library.domain.enums.ReturnType;
import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.Borrowing;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.model.WaitList;
import com.burakyapici.library.domain.repository.BorrowingRepository;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.impl.BorrowingServiceImpl;
import com.burakyapici.library.api.validation.borrowing.BorrowHandlerRequest;
import com.burakyapici.library.api.validation.borrowing.BorrowValidationHandler;
import com.burakyapici.library.api.validation.returning.ReturnHandlerRequest;
import com.burakyapici.library.api.validation.returning.ReturnValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BorrowingServiceTest {
    
    @Mock
    private BorrowingRepository borrowingRepository;
    
    @Mock
    private BookCopyService bookCopyService;
    
    @Mock
    private WaitListService waitListService;
    
    @Mock
    private UserService userService;
    
    @Mock
    private BorrowValidationHandler borrowValidationHandler;
    
    @Mock
    private ReturnValidationHandler returnValidationHandler;
    
    @Mock
    private Sinks.Many<BookAvailabilityUpdateEvent> bookAvailabilitySink;
    
    @InjectMocks
    private BorrowingServiceImpl borrowingService;
    
    @Test
    @DisplayName("Given valid borrow request, when borrowBookCopyByBarcode, then return borrowing dto")
    public void givenValidBorrowRequest_whenBorrowBookCopyByBarcode_thenReturnBorrowingDto() {
        UUID patronId = UUID.randomUUID();
        UUID librarianId = UUID.randomUUID();
        String barcode = "BC12345";
        
        User patron = UserServiceTestUtil.createSampleUserWithId(patronId);
        UserDetailsImpl librarian = UserDetailsImpl.builder()
                .id(librarianId)
                .email("librarian@example.com")
                .build();
        User librarianUser = UserServiceTestUtil.createSampleUserWithId(librarianId);
        
        Book book = BookServiceTestUtil.createSampleBookWithId(UUID.randomUUID());
        BookCopy bookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithBookAndStatus(book, BookCopyStatus.AVAILABLE);
        bookCopy.setBarcode(barcode);
        
        BorrowBookCopyRequest borrowRequest = BorrowingServiceTestUtil.createSampleBorrowBookCopyRequest(patronId);
        Borrowing savedBorrowing = BorrowingServiceTestUtil.createSampleBorrowingWithId(UUID.randomUUID(), patron, librarianUser, bookCopy);
        
        when(userService.getUserByIdOrElseThrow(patronId)).thenReturn(patron);
        when(userService.getUserByIdOrElseThrow(librarianId)).thenReturn(librarianUser);
        when(bookCopyService.getBookCopyByBarcodeOrElseThrow(barcode)).thenReturn(bookCopy);
        when(waitListService.getByUserIdAndBookIdAndStatus(patronId, book.getId(), WaitListStatus.READY_FOR_PICKUP))
                .thenReturn(Optional.empty());
        doNothing().when(borrowValidationHandler).handle(any(BorrowHandlerRequest.class));
        when(borrowingRepository.save(any(Borrowing.class))).thenReturn(savedBorrowing);
        when(bookCopyService.saveBookCopy(any(BookCopy.class))).thenReturn(bookCopy);
        when(bookCopyService.countByIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE)).thenReturn(5);
        
        BorrowingDto result = borrowingService.borrowBookCopyByBarcode(barcode, borrowRequest, librarian);
        
        assertNotNull(result);
        assertEquals(patronId, result.userId());
        assertEquals(patron.getEmail(), result.userEmail());
        assertEquals(patron.getFirstName(), result.userFirstName());
        assertEquals(patron.getLastName(), result.userLastName());
        assertEquals(bookCopy.getId(), result.bookCopyId());
        assertEquals(barcode, result.bookCopyBarcode());
        assertEquals(librarianUser.getId(), result.borrowedByStaffId());
        assertEquals(BorrowingStatus.BORROWED, result.status());
        
        verify(userService).getUserByIdOrElseThrow(patronId);
        verify(userService).getUserByIdOrElseThrow(librarianId);
        verify(bookCopyService).getBookCopyByBarcodeOrElseThrow(barcode);
        verify(waitListService).getByUserIdAndBookIdAndStatus(patronId, book.getId(), WaitListStatus.READY_FOR_PICKUP);
        verify(borrowValidationHandler).handle(any(BorrowHandlerRequest.class));
        verify(borrowingRepository).save(any(Borrowing.class));
        verify(bookCopyService).saveBookCopy(any(BookCopy.class));
        verify(bookAvailabilitySink).emitNext(any(BookAvailabilityUpdateEvent.class), any());
    }
    
    @Test
    @DisplayName("Given borrow request with waitlist, when borrowBookCopyByBarcode, then update waitlist and return borrowing dto")
    public void givenBorrowRequestWithWaitlist_whenBorrowBookCopyByBarcode_thenUpdateWaitlistAndReturnBorrowingDto() {
        UUID patronId = UUID.randomUUID();
        UUID librarianId = UUID.randomUUID();
        String barcode = "BC12345";
        
        User patron = UserServiceTestUtil.createSampleUserWithId(patronId);
        UserDetailsImpl librarian = UserDetailsImpl.builder()
                .id(librarianId)
                .email("librarian@example.com")
                .build();
        User librarianUser = UserServiceTestUtil.createSampleUserWithId(librarianId);
        
        Book book = BookServiceTestUtil.createSampleBookWithId(UUID.randomUUID());
        BookCopy bookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithBookAndStatus(book, BookCopyStatus.ON_HOLD);
        bookCopy.setBarcode(barcode);
        
        WaitList waitList = BorrowingServiceTestUtil.createSampleWaitList(patron, book, bookCopy, WaitListStatus.READY_FOR_PICKUP);
        
        BorrowBookCopyRequest borrowRequest = BorrowingServiceTestUtil.createSampleBorrowBookCopyRequest(patronId);
        Borrowing savedBorrowing = BorrowingServiceTestUtil.createSampleBorrowingWithId(UUID.randomUUID(), patron, librarianUser, bookCopy);
        
        when(userService.getUserByIdOrElseThrow(patronId)).thenReturn(patron);
        when(userService.getUserByIdOrElseThrow(librarianId)).thenReturn(librarianUser);
        when(bookCopyService.getBookCopyByBarcodeOrElseThrow(barcode)).thenReturn(bookCopy);
        when(waitListService.getByUserIdAndBookIdAndStatus(patronId, book.getId(), WaitListStatus.READY_FOR_PICKUP))
                .thenReturn(Optional.of(waitList));
        doNothing().when(borrowValidationHandler).handle(any(BorrowHandlerRequest.class));
        when(borrowingRepository.save(any(Borrowing.class))).thenReturn(savedBorrowing);
        when(bookCopyService.saveBookCopy(any(BookCopy.class))).thenReturn(bookCopy);
        when(waitListService.saveWaitList(any(WaitList.class))).thenReturn(waitList);
        when(bookCopyService.countByIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE)).thenReturn(5);
        
        BorrowingDto result = borrowingService.borrowBookCopyByBarcode(barcode, borrowRequest, librarian);
        
        assertNotNull(result);
        assertEquals(patronId, result.userId());
        assertEquals(patron.getEmail(), result.userEmail());
        assertEquals(patron.getFirstName(), result.userFirstName());
        assertEquals(patron.getLastName(), result.userLastName());
        assertEquals(bookCopy.getId(), result.bookCopyId());
        assertEquals(barcode, result.bookCopyBarcode());
        assertEquals(librarianUser.getId(), result.borrowedByStaffId());
        assertEquals(BorrowingStatus.BORROWED, result.status());
        
        verify(userService).getUserByIdOrElseThrow(patronId);
        verify(userService).getUserByIdOrElseThrow(librarianId);
        verify(bookCopyService).getBookCopyByBarcodeOrElseThrow(barcode);
        verify(waitListService).getByUserIdAndBookIdAndStatus(patronId, book.getId(), WaitListStatus.READY_FOR_PICKUP);
        verify(borrowValidationHandler).handle(any(BorrowHandlerRequest.class));
        verify(borrowingRepository).save(any(Borrowing.class));
        verify(bookCopyService).saveBookCopy(any(BookCopy.class));
        verify(waitListService).saveWaitList(any(WaitList.class));
        verify(bookAvailabilitySink).emitNext(any(BookAvailabilityUpdateEvent.class), any());
    }
    
    @Test
    @DisplayName("Given valid return request, when returnBookCopyByBarcode, then return borrowing dto")
    public void givenValidReturnRequest_whenReturnBookCopyByBarcode_thenReturnBorrowingDto() {
        UUID patronId = UUID.randomUUID();
        UUID librarianId = UUID.randomUUID();
        String barcode = "BC12345";
        
        User patron = UserServiceTestUtil.createSampleUserWithId(patronId);
        UserDetailsImpl librarian = UserDetailsImpl.builder()
                .id(librarianId)
                .email("librarian@example.com")
                .build();
        User librarianUser = UserServiceTestUtil.createSampleUserWithId(librarianId);
        
        Book book = BookServiceTestUtil.createSampleBookWithId(UUID.randomUUID());
        BookCopy bookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithBookAndStatus(book, BookCopyStatus.CHECKED_OUT);
        bookCopy.setBarcode(barcode);
        
        Borrowing borrowing = BorrowingServiceTestUtil.createSampleBorrowing(patron, librarianUser, bookCopy);
        borrowing.setId(UUID.randomUUID());
        
        BorrowReturnRequest returnRequest = BorrowingServiceTestUtil.createSampleBorrowReturnRequest(patronId, ReturnType.NORMAL);
        
        when(userService.getUserByIdOrElseThrow(patronId)).thenReturn(patron);
        when(userService.getUserByIdOrElseThrow(librarianId)).thenReturn(librarianUser);
        when(bookCopyService.getBookCopyByBarcodeOrElseThrow(barcode)).thenReturn(bookCopy);
        when(borrowingRepository.findByStatusAndBookCopyBarcodeAndUserId(
                BorrowingStatus.BORROWED.name(), barcode, patronId)).thenReturn(borrowing);
        doNothing().when(returnValidationHandler).handle(any(ReturnHandlerRequest.class));
        when(waitListService.getTopByBookIdAndStatusOrderByStartDateAsc(book.getId(), WaitListStatus.WAITING))
                .thenReturn(Optional.empty());
        when(bookCopyService.saveBookCopy(any(BookCopy.class))).thenReturn(bookCopy);
        when(borrowingRepository.save(any(Borrowing.class))).thenReturn(borrowing);
        when(bookCopyService.countByIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE)).thenReturn(5);
        
        BorrowingDto result = borrowingService.returnBookCopyByBarcode(barcode, returnRequest, librarian);
        
        assertNotNull(result);
        assertEquals(patronId, result.userId());
        assertEquals(patron.getEmail(), result.userEmail());
        assertEquals(patron.getFirstName(), result.userFirstName());
        assertEquals(patron.getLastName(), result.userLastName());
        assertEquals(bookCopy.getId(), result.bookCopyId());
        assertEquals(barcode, result.bookCopyBarcode());
        assertEquals(librarianUser.getId(), result.borrowedByStaffId());
        assertEquals(librarianUser.getId(), result.returnedByStaffId());
        assertEquals(ReturnType.NORMAL.getBorrowingStatus(), result.status());
        
        verify(userService).getUserByIdOrElseThrow(patronId);
        verify(userService).getUserByIdOrElseThrow(librarianId);
        verify(bookCopyService).getBookCopyByBarcodeOrElseThrow(barcode);
        verify(borrowingRepository).findByStatusAndBookCopyBarcodeAndUserId(
                BorrowingStatus.BORROWED.name(), barcode, patronId);
        verify(returnValidationHandler).handle(any(ReturnHandlerRequest.class));
        verify(waitListService).getTopByBookIdAndStatusOrderByStartDateAsc(book.getId(), WaitListStatus.WAITING);
        verify(bookCopyService).saveBookCopy(any(BookCopy.class));
        verify(borrowingRepository).save(any(Borrowing.class));
        verify(bookAvailabilitySink).emitNext(any(BookAvailabilityUpdateEvent.class), any());
    }
    
    @Test
    @DisplayName("Given return request with waiting list entry, when returnBookCopyByBarcode, then update wait list and return borrowing dto")
    public void givenReturnRequestWithWaitingListEntry_whenReturnBookCopyByBarcode_thenUpdateWaitListAndReturnBorrowingDto() {
        UUID patronId = UUID.randomUUID();
        UUID librarianId = UUID.randomUUID();
        String barcode = "BC12345";
        
        User patron = UserServiceTestUtil.createSampleUserWithId(patronId);
        User waitingPatron = UserServiceTestUtil.createSampleUserWithId(UUID.randomUUID());
        UserDetailsImpl librarian = UserDetailsImpl.builder()
                .id(librarianId)
                .email("librarian@example.com")
                .build();
        User librarianUser = UserServiceTestUtil.createSampleUserWithId(librarianId);
        
        Book book = BookServiceTestUtil.createSampleBookWithId(UUID.randomUUID());
        BookCopy bookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithBookAndStatus(book, BookCopyStatus.CHECKED_OUT);
        bookCopy.setBarcode(barcode);
        
        Borrowing borrowing = BorrowingServiceTestUtil.createSampleBorrowing(patron, librarianUser, bookCopy);
        borrowing.setId(UUID.randomUUID());
        
        WaitList waitList = BorrowingServiceTestUtil.createSampleWaitList(waitingPatron, book, null, WaitListStatus.WAITING);
        
        BorrowReturnRequest returnRequest = BorrowingServiceTestUtil.createSampleBorrowReturnRequest(patronId, ReturnType.NORMAL);
        
        when(userService.getUserByIdOrElseThrow(patronId)).thenReturn(patron);
        when(userService.getUserByIdOrElseThrow(librarianId)).thenReturn(librarianUser);
        when(bookCopyService.getBookCopyByBarcodeOrElseThrow(barcode)).thenReturn(bookCopy);
        when(borrowingRepository.findByStatusAndBookCopyBarcodeAndUserId(
                BorrowingStatus.BORROWED.name(), barcode, patronId)).thenReturn(borrowing);
        doNothing().when(returnValidationHandler).handle(any(ReturnHandlerRequest.class));
        when(waitListService.getTopByBookIdAndStatusOrderByStartDateAsc(book.getId(), WaitListStatus.WAITING))
                .thenReturn(Optional.of(waitList));
        when(bookCopyService.saveBookCopy(any(BookCopy.class))).thenReturn(bookCopy);
        when(waitListService.saveWaitList(any(WaitList.class))).thenReturn(waitList);
        when(borrowingRepository.save(any(Borrowing.class))).thenReturn(borrowing);
        when(bookCopyService.countByIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE)).thenReturn(4);
        
        BorrowingDto result = borrowingService.returnBookCopyByBarcode(barcode, returnRequest, librarian);
        
        assertNotNull(result);
        assertEquals(patronId, result.userId());
        assertEquals(patron.getEmail(), result.userEmail());
        assertEquals(patron.getFirstName(), result.userFirstName());
        assertEquals(patron.getLastName(), result.userLastName());
        assertEquals(bookCopy.getId(), result.bookCopyId());
        assertEquals(barcode, result.bookCopyBarcode());
        assertEquals(librarianUser.getId(), result.borrowedByStaffId());
        assertEquals(librarianUser.getId(), result.returnedByStaffId());
        assertEquals(ReturnType.NORMAL.getBorrowingStatus(), result.status());
        
        verify(userService).getUserByIdOrElseThrow(patronId);
        verify(userService).getUserByIdOrElseThrow(librarianId);
        verify(bookCopyService).getBookCopyByBarcodeOrElseThrow(barcode);
        verify(borrowingRepository).findByStatusAndBookCopyBarcodeAndUserId(
                BorrowingStatus.BORROWED.name(), barcode, patronId);
        verify(returnValidationHandler).handle(any(ReturnHandlerRequest.class));
        verify(waitListService).getTopByBookIdAndStatusOrderByStartDateAsc(book.getId(), WaitListStatus.WAITING);
        verify(bookCopyService).saveBookCopy(any(BookCopy.class));
        verify(waitListService).saveWaitList(any(WaitList.class));
        verify(borrowingRepository).save(any(Borrowing.class));
        verify(bookAvailabilitySink).emitNext(any(BookAvailabilityUpdateEvent.class), any());
    }
    
    @Test
    @DisplayName("Given damaged return request, when returnBookCopyByBarcode, then set book copy status to in repair")
    public void givenDamagedReturnRequest_whenReturnBookCopyByBarcode_thenSetBookCopyStatusToInRepair() {
        UUID patronId = UUID.randomUUID();
        UUID librarianId = UUID.randomUUID();
        String barcode = "BC12345";
        
        User patron = UserServiceTestUtil.createSampleUserWithId(patronId);
        UserDetailsImpl librarian = UserDetailsImpl.builder()
                .id(librarianId)
                .email("librarian@example.com")
                .build();
        User librarianUser = UserServiceTestUtil.createSampleUserWithId(librarianId);
        
        Book book = BookServiceTestUtil.createSampleBookWithId(UUID.randomUUID());
        BookCopy bookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithBookAndStatus(book, BookCopyStatus.CHECKED_OUT);
        bookCopy.setBarcode(barcode);
        
        Borrowing borrowing = BorrowingServiceTestUtil.createSampleBorrowing(patron, librarianUser, bookCopy);
        borrowing.setId(UUID.randomUUID());
        
        BorrowReturnRequest returnRequest = BorrowingServiceTestUtil.createSampleBorrowReturnRequest(patronId, ReturnType.DAMAGED);
        
        when(userService.getUserByIdOrElseThrow(patronId)).thenReturn(patron);
        when(userService.getUserByIdOrElseThrow(librarianId)).thenReturn(librarianUser);
        when(bookCopyService.getBookCopyByBarcodeOrElseThrow(barcode)).thenReturn(bookCopy);
        when(borrowingRepository.findByStatusAndBookCopyBarcodeAndUserId(
                BorrowingStatus.BORROWED.name(), barcode, patronId)).thenReturn(borrowing);
        doNothing().when(returnValidationHandler).handle(any(ReturnHandlerRequest.class));
        when(bookCopyService.saveBookCopy(any(BookCopy.class))).thenReturn(bookCopy);
        when(borrowingRepository.save(any(Borrowing.class))).thenReturn(borrowing);
        when(bookCopyService.countByIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE)).thenReturn(5);
        
        BorrowingDto result = borrowingService.returnBookCopyByBarcode(barcode, returnRequest, librarian);
        
        assertNotNull(result);
        assertEquals(patronId, result.userId());
        assertEquals(patron.getEmail(), result.userEmail());
        assertEquals(patron.getFirstName(), result.userFirstName());
        assertEquals(patron.getLastName(), result.userLastName());
        assertEquals(bookCopy.getId(), result.bookCopyId());
        assertEquals(barcode, result.bookCopyBarcode());
        assertEquals(librarianUser.getId(), result.borrowedByStaffId());
        assertEquals(librarianUser.getId(), result.returnedByStaffId());
        assertEquals(ReturnType.DAMAGED.getBorrowingStatus(), result.status());
        
        verify(userService).getUserByIdOrElseThrow(patronId);
        verify(userService).getUserByIdOrElseThrow(librarianId);
        verify(bookCopyService).getBookCopyByBarcodeOrElseThrow(barcode);
        verify(borrowingRepository).findByStatusAndBookCopyBarcodeAndUserId(
                BorrowingStatus.BORROWED.name(), barcode, patronId);
        verify(returnValidationHandler).handle(any(ReturnHandlerRequest.class));
        verify(bookCopyService).saveBookCopy(any(BookCopy.class));
        verify(borrowingRepository).save(any(Borrowing.class));
        verify(bookAvailabilitySink).emitNext(any(BookAvailabilityUpdateEvent.class), any());
        verify(waitListService, never()).getTopByBookIdAndStatusOrderByStartDateAsc(any(), any());
    }
    
    @Test
    @DisplayName("Given user id with borrowings, when getCurrentUserBorrowings, then return list of borrowing dtos")
    public void givenUserIdWithBorrowings_whenGetCurrentUserBorrowings_thenReturnListOfBorrowingDtos() {
        UUID userId = UUID.randomUUID();
        User user = UserServiceTestUtil.createSampleUserWithId(userId);
        Book book = BookServiceTestUtil.createSampleBookWithId(UUID.randomUUID());
        BookCopy bookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithBookAndStatus(book, BookCopyStatus.CHECKED_OUT);
        
        List<Borrowing> borrowings = new ArrayList<>();
        borrowings.add(BorrowingServiceTestUtil.createSampleBorrowingWithId(UUID.randomUUID(), user, user, bookCopy));
        borrowings.add(BorrowingServiceTestUtil.createSampleBorrowingWithId(UUID.randomUUID(), user, user, bookCopy));
        
        when(borrowingRepository.findAllByUserId(userId)).thenReturn(borrowings);
        
        List<BorrowingDto> result = borrowingService.getCurrentUserBorrowings(userId);
        
        assertNotNull(result);
        assertEquals(borrowings.size(), result.size());
        assertEquals(userId, result.getFirst().userId());
        assertEquals(bookCopy.getId(), result.getFirst().bookCopyId());
        
        verify(borrowingRepository).findAllByUserId(userId);
    }
    
    @Test
    @DisplayName("Given user id without borrowings, when getCurrentUserBorrowings, then return empty list")
    public void givenUserIdWithoutBorrowings_whenGetCurrentUserBorrowings_thenReturnEmptyList() {
        UUID userId = UUID.randomUUID();
        
        when(borrowingRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());
        
        List<BorrowingDto> result = borrowingService.getCurrentUserBorrowings(userId);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(borrowingRepository).findAllByUserId(userId);
    }
    
    @Test
    @DisplayName("Given existing user id with borrowings, when getUserBorrowingsById, then return list of borrowing dtos")
    public void givenExistingUserIdWithBorrowings_whenGetUserBorrowingsById_thenReturnListOfBorrowingDtos() {
        UUID userId = UUID.randomUUID();
        User user = UserServiceTestUtil.createSampleUserWithId(userId);
        Book book = BookServiceTestUtil.createSampleBookWithId(UUID.randomUUID());
        BookCopy bookCopy = BookCopyServiceTestUtil.createSampleBookCopyWithBookAndStatus(book, BookCopyStatus.CHECKED_OUT);
        
        List<Borrowing> borrowings = new ArrayList<>();
        borrowings.add(BorrowingServiceTestUtil.createSampleBorrowingWithId(UUID.randomUUID(), user, user, bookCopy));
        borrowings.add(BorrowingServiceTestUtil.createSampleBorrowingWithId(UUID.randomUUID(), user, user, bookCopy));
        
        when(userService.getUserByIdOrElseThrow(userId)).thenReturn(user);
        when(borrowingRepository.findAllByUserId(userId)).thenReturn(borrowings);
        
        List<BorrowingDto> result = borrowingService.getUserBorrowingsById(userId);
        
        assertNotNull(result);
        assertEquals(borrowings.size(), result.size());
        assertEquals(userId, result.getFirst().userId());
        assertEquals(bookCopy.getId(), result.getFirst().bookCopyId());
        
        verify(userService).getUserByIdOrElseThrow(userId);
        verify(borrowingRepository).findAllByUserId(userId);
    }
    
    @Test
    @DisplayName("Given non-existing user id, when getUserBorrowingsById, then throw EntityNotFoundException")
    public void givenNonExistingUserId_whenGetUserBorrowingsById_thenThrowEntityNotFoundException() {
        UUID userId = UUID.randomUUID();
        
        when(userService.getUserByIdOrElseThrow(userId)).thenThrow(new EntityNotFoundException("User not found"));
        
        assertThrows(EntityNotFoundException.class, () -> borrowingService.getUserBorrowingsById(userId));
        
        verify(userService).getUserByIdOrElseThrow(userId);
        verify(borrowingRepository, never()).findAllByUserId(any());
    }
    
    @Test
    @DisplayName("Given book copy id, when deleteAllByBookCopyId, then delete all borrowings")
    public void givenBookCopyId_whenDeleteAllByBookCopyId_thenDeleteAllBorrowings() {
        UUID bookCopyId = UUID.randomUUID();
        
        borrowingService.deleteAllByBookCopyId(bookCopyId);
        
        verify(borrowingRepository).deleteAllByBookCopyId(bookCopyId);
    }
    
    @Test
    @DisplayName("Given book id, when deleteAllByBookId, then delete all borrowings")
    public void givenBookId_whenDeleteAllByBookId_thenDeleteAllBorrowings() {
        UUID bookId = UUID.randomUUID();
        
        borrowingService.deleteAllByBookId(bookId);
        
        verify(borrowingRepository).deleteAllByBookCopyBookId(bookId);
    }
}
