package com.burakyapici.library.service;

import com.burakyapici.library.api.advice.DataConflictException;
import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.api.advice.ForbiddenAccessException;
import com.burakyapici.library.api.advice.UnprocessableEntityException;
import com.burakyapici.library.api.dto.request.PlaceHoldRequest;
import com.burakyapici.library.common.mapper.WaitListMapper;
import com.burakyapici.library.common.util.WaitListServiceTestUtil;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.dto.WaitListDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.enums.BookStatus;
import com.burakyapici.library.domain.enums.PatronStatus;
import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.model.WaitList;
import com.burakyapici.library.domain.repository.WaitListRepository;
import com.burakyapici.library.exception.BookStatusValidationException;
import com.burakyapici.library.exception.PatronStatusValidationException;
import com.burakyapici.library.service.impl.WaitListServiceImpl;
import com.burakyapici.library.api.validation.waitlist.PlaceHoldHandlerRequest;
import com.burakyapici.library.api.validation.waitlist.WaitListValidationHandler;
import com.burakyapici.library.api.validation.waitlist.cancel.CancelHoldHandlerRequest;
import com.burakyapici.library.api.validation.waitlist.cancel.CancelHoldValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WaitListServiceTest {

    @Mock
    private WaitListRepository waitListRepository;
    @Mock
    private BookService bookService;
    @Mock
    private UserService userService;
    @Mock
    private BookCopyService bookCopyService;
    @Mock
    private WaitListValidationHandler waitListValidationHandler;
    @Mock
    private CancelHoldValidationHandler cancelHoldValidationHandler;

    @InjectMocks
    private WaitListServiceImpl waitListService;

    @Test
    @DisplayName("Should place hold successfully when all validations pass and no copies available")
    void givenValidRequestAndNoAvailableCopies_whenPlaceHold_thenWaitListCreatedAndDtoReturned() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        WaitList testWaitList = WaitListServiceTestUtil.createSampleWaitList(testUser, testBook, WaitListStatus.WAITING);
        PlaceHoldRequest placeHoldRequest = WaitListServiceTestUtil.createSamplePlaceHoldRequest(bookId);
        
        List<BookCopy> availableCopies = Collections.emptyList();
        WaitListDto expectedDto = WaitListServiceTestUtil.createSampleWaitListDto(testWaitList);

        when(userService.getUserByIdOrElseThrow(userId)).thenReturn(testUser);
        when(bookService.getBookByIdOrElseThrow(bookId)).thenReturn(testBook);
        when(bookCopyService.findByBookIdAndStatus(bookId, BookCopyStatus.AVAILABLE)).thenReturn(availableCopies);
        doNothing().when(waitListValidationHandler).handle(any(PlaceHoldHandlerRequest.class));
        when(waitListRepository.save(any(WaitList.class))).thenReturn(testWaitList);

        WaitListDto actualDto = waitListService.placeHold(placeHoldRequest, userId);

        assertNotNull(actualDto, "Dönen DTO null olmamalı");
        assertEquals(expectedDto.id(), actualDto.id(), "WaitList ID'leri eşleşmeli");
        assertEquals(expectedDto.status(), actualDto.status(), "WaitList durumları eşleşmeli");

        verify(waitListValidationHandler).handle(any(PlaceHoldHandlerRequest.class));
        verify(waitListRepository).save(any(WaitList.class));
    }

    @Test
    @DisplayName("Should throw PatronStatusValidationException when patron is not active during place hold")
    void givenInactivePatron_whenPlaceHold_thenThrowPatronStatusValidationException() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        User inactiveUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.INACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        PlaceHoldRequest placeHoldRequest = WaitListServiceTestUtil.createSamplePlaceHoldRequest(bookId);

        when(userService.getUserByIdOrElseThrow(userId)).thenReturn(inactiveUser); 
        when(bookService.getBookByIdOrElseThrow(bookId)).thenReturn(testBook);
        when(bookCopyService.findByBookIdAndStatus(bookId, BookCopyStatus.AVAILABLE)).thenReturn(Collections.emptyList());
        doThrow(new PatronStatusValidationException("Patron status is not active"))
            .when(waitListValidationHandler).handle(any(PlaceHoldHandlerRequest.class));

        assertThrows(PatronStatusValidationException.class, () -> waitListService.placeHold(placeHoldRequest, userId));

        verify(waitListRepository, never()).save(any(WaitList.class));
    }

    @Test
    @DisplayName("Should throw BookStatusValidationException when book is not active during place hold")
    void givenInactiveBook_whenPlaceHold_thenThrowBookStatusValidationException() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book inactiveBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ARCHIVED);
        PlaceHoldRequest placeHoldRequest = WaitListServiceTestUtil.createSamplePlaceHoldRequest(bookId);

        when(userService.getUserByIdOrElseThrow(userId)).thenReturn(testUser);
        when(bookService.getBookByIdOrElseThrow(bookId)).thenReturn(inactiveBook);
        when(bookCopyService.findByBookIdAndStatus(bookId, BookCopyStatus.AVAILABLE)).thenReturn(Collections.emptyList());
        doThrow(new BookStatusValidationException("Book is not active"))
            .when(waitListValidationHandler).handle(any(PlaceHoldHandlerRequest.class));

        assertThrows(BookStatusValidationException.class, () -> waitListService.placeHold(placeHoldRequest, userId));
        verify(waitListRepository, never()).save(any(WaitList.class));
    }
    
    @Test
    @DisplayName("Should throw DataConflictException if patron already has a waitlist for the book")
    void givenExistingWaitListForPatronAndBook_whenPlaceHold_thenThrowDataConflictException() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        PlaceHoldRequest placeHoldRequest = WaitListServiceTestUtil.createSamplePlaceHoldRequest(bookId);

        when(userService.getUserByIdOrElseThrow(userId)).thenReturn(testUser);
        when(bookService.getBookByIdOrElseThrow(bookId)).thenReturn(testBook);
        when(bookCopyService.findByBookIdAndStatus(bookId, BookCopyStatus.AVAILABLE)).thenReturn(Collections.emptyList());
        doThrow(new DataConflictException("Patron already on waitlist"))
            .when(waitListValidationHandler).handle(any(PlaceHoldHandlerRequest.class));

        assertThrows(DataConflictException.class, () -> waitListService.placeHold(placeHoldRequest, userId));
        verify(waitListRepository, never()).save(any(WaitList.class));
    }

    @Test
    @DisplayName("Should throw UnprocessableEntityException if book has available copies during place hold")
    void givenAvailableCopies_whenPlaceHold_thenThrowUnprocessableEntityException() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        PlaceHoldRequest placeHoldRequest = WaitListServiceTestUtil.createSamplePlaceHoldRequest(bookId);
        List<BookCopy> availableCopies = List.of(WaitListServiceTestUtil.createSampleBookCopy(testBook, BookCopyStatus.AVAILABLE));
        
        when(userService.getUserByIdOrElseThrow(userId)).thenReturn(testUser);
        when(bookService.getBookByIdOrElseThrow(bookId)).thenReturn(testBook);
        when(bookCopyService.findByBookIdAndStatus(bookId, BookCopyStatus.AVAILABLE)).thenReturn(availableCopies);
         doThrow(new UnprocessableEntityException("Book has available copies"))
            .when(waitListValidationHandler).handle(any(PlaceHoldHandlerRequest.class));

        assertThrows(UnprocessableEntityException.class, () -> waitListService.placeHold(placeHoldRequest, userId));
        verify(waitListRepository, never()).save(any(WaitList.class));
    }

    @Test
    @DisplayName("Should cancel hold successfully when all validations pass")
    void givenValidRequest_whenCancelHold_thenWaitListCancelled() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        UUID waitListId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        WaitList testWaitList = WaitListServiceTestUtil.createSampleWaitList(testUser, testBook, WaitListStatus.WAITING);
        testWaitList.setId(waitListId);

        when(waitListRepository.findById(waitListId)).thenReturn(Optional.of(testWaitList));
        when(userService.getUserByIdOrElseThrow(userId)).thenReturn(testUser);
        doNothing().when(cancelHoldValidationHandler).handle(any(CancelHoldHandlerRequest.class));
        when(waitListRepository.save(any(WaitList.class))).thenReturn(testWaitList); 

        waitListService.cancelHold(waitListId, userId);

        ArgumentCaptor<WaitList> captor = ArgumentCaptor.forClass(WaitList.class);
        verify(waitListRepository).save(captor.capture());
        assertEquals(WaitListStatus.CANCELLED, captor.getValue().getStatus());
        assertNotNull(captor.getValue().getEndDate());
    }
    
    @Test
    @DisplayName("Should throw ForbiddenAccessException when cancelling hold not owned by patron")
    void givenPatronCancellingAnotherUsersHold_whenCancelHold_thenThrowForbiddenAccessException() {
        UUID currentUserId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        UUID waitListId = UUID.randomUUID();

        User currentUser = WaitListServiceTestUtil.createSampleUser(currentUserId, PatronStatus.ACTIVE);
        User anotherUser = WaitListServiceTestUtil.createSampleUser(anotherUserId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        WaitList testWaitList = WaitListServiceTestUtil.createSampleWaitList(anotherUser, testBook, WaitListStatus.WAITING);
        testWaitList.setId(waitListId);

        when(waitListRepository.findById(waitListId)).thenReturn(Optional.of(testWaitList));
        when(userService.getUserByIdOrElseThrow(currentUserId)).thenReturn(currentUser); 
        doThrow(new ForbiddenAccessException("Cannot cancel another user's hold"))
            .when(cancelHoldValidationHandler).handle(any(CancelHoldHandlerRequest.class));

        assertThrows(ForbiddenAccessException.class, () -> waitListService.cancelHold(waitListId, currentUserId));
        verify(waitListRepository, never()).save(any(WaitList.class));
    }

    @Test
    @DisplayName("Should throw UnprocessableEntityException when cancelling hold with invalid status")
    void givenInvalidStatusForCancellation_whenCancelHold_thenThrowUnprocessableEntityException() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        UUID waitListId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        WaitList testWaitList = WaitListServiceTestUtil.createSampleWaitList(testUser, testBook, WaitListStatus.COMPLETED);
        testWaitList.setId(waitListId);

        when(waitListRepository.findById(waitListId)).thenReturn(Optional.of(testWaitList));
        when(userService.getUserByIdOrElseThrow(userId)).thenReturn(testUser);
         doThrow(new UnprocessableEntityException("Waitlist cannot be cancelled due to its status"))
            .when(cancelHoldValidationHandler).handle(any(CancelHoldHandlerRequest.class));

        assertThrows(UnprocessableEntityException.class, () -> waitListService.cancelHold(waitListId, userId));
        verify(waitListRepository, never()).save(any(WaitList.class));
    }

    @Test
    @DisplayName("Should release book copy and cancel hold when status is READY_FOR_PICKUP")
    void givenReadyForPickupHold_whenCancelHold_thenBookCopyReleasedAndWaitListCancelled() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        UUID waitListId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        BookCopy reservedCopy = WaitListServiceTestUtil.createSampleBookCopy(testBook, BookCopyStatus.ON_HOLD);
        WaitList testWaitList = WaitListServiceTestUtil.createSampleWaitList(testUser, testBook, WaitListStatus.READY_FOR_PICKUP);
        testWaitList.setId(waitListId);
        testWaitList.setReservedBookCopy(reservedCopy);

        when(waitListRepository.findById(waitListId)).thenReturn(Optional.of(testWaitList));
        when(userService.getUserByIdOrElseThrow(userId)).thenReturn(testUser);
        doNothing().when(cancelHoldValidationHandler).handle(any(CancelHoldHandlerRequest.class));
        when(bookCopyService.saveBookCopy(any(BookCopy.class))).thenReturn(reservedCopy); 
        when(waitListRepository.save(any(WaitList.class))).thenReturn(testWaitList); 

        waitListService.cancelHold(waitListId, userId);

        ArgumentCaptor<BookCopy> bookCopyCaptor = ArgumentCaptor.forClass(BookCopy.class);
        verify(bookCopyService).saveBookCopy(bookCopyCaptor.capture());
        assertEquals(BookCopyStatus.AVAILABLE, bookCopyCaptor.getValue().getStatus());

        ArgumentCaptor<WaitList> waitListCaptor = ArgumentCaptor.forClass(WaitList.class);
        verify(waitListRepository).save(waitListCaptor.capture());
        assertEquals(WaitListStatus.CANCELLED, waitListCaptor.getValue().getStatus());
        assertNotNull(waitListCaptor.getValue().getEndDate());
    }
    
    @Test
    @DisplayName("Should throw EntityNotFoundException when cancelling a non-existent waitlist")
    void givenNonExistentWaitListId_whenCancelHold_thenThrowEntityNotFoundException() {
        UUID userId = UUID.randomUUID();
        UUID nonExistentWaitListId = UUID.randomUUID();
        when(waitListRepository.findById(nonExistentWaitListId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> waitListService.cancelHold(nonExistentWaitListId, userId));

        verify(cancelHoldValidationHandler, never()).handle(any(CancelHoldHandlerRequest.class));
        verify(waitListRepository, never()).save(any(WaitList.class));
    }

    @Test
    @DisplayName("getTopByBookIdAndStatusOrderByStartDateAsc - Should return top waitlist when exists")
    void getTopByBookIdAndStatusOrderByStartDateAsc_whenWaitListExists_thenReturnWaitList() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        WaitListStatus status = WaitListStatus.WAITING;
        WaitList expectedWaitList = WaitListServiceTestUtil.createSampleWaitList(testUser, testBook, status);

        when(waitListRepository.findTopByBookIdAndStatusOrderByStartDateAsc(bookId, status.name()))
            .thenReturn(Optional.of(expectedWaitList));

        Optional<WaitList> actualWaitListOptional = waitListService.getTopByBookIdAndStatusOrderByStartDateAsc(bookId, status);

        assertTrue(actualWaitListOptional.isPresent());
        assertEquals(expectedWaitList, actualWaitListOptional.get());
        verify(waitListRepository).findTopByBookIdAndStatusOrderByStartDateAsc(bookId, status.name());
    }

    @Test
    @DisplayName("getTopByBookIdAndStatusOrderByStartDateAsc - Should return empty Optional when not exists")
    void getTopByBookIdAndStatusOrderByStartDateAsc_whenNoWaitListExists_thenReturnEmptyOptional() {
        UUID bookId = UUID.randomUUID();
        WaitListStatus status = WaitListStatus.WAITING;
        when(waitListRepository.findTopByBookIdAndStatusOrderByStartDateAsc(bookId, status.name()))
            .thenReturn(Optional.empty());

        Optional<WaitList> actualWaitListOptional = waitListService.getTopByBookIdAndStatusOrderByStartDateAsc(bookId, status);

        assertTrue(actualWaitListOptional.isEmpty());
        verify(waitListRepository).findTopByBookIdAndStatusOrderByStartDateAsc(bookId, status.name());
    }

    @Test
    @DisplayName("deleteByBookCopyId - Should call repository deleteByBookCopyId")
    void deleteByBookCopyId_shouldCallRepository() {
        UUID bookCopyId = UUID.randomUUID();
        doNothing().when(waitListRepository).deleteByBookCopyId(bookCopyId);

        waitListService.deleteByBookCopyId(bookCopyId);

        verify(waitListRepository).deleteByBookCopyId(bookCopyId);
    }

    @Test
    @DisplayName("deleteByBookId - Should call repository deleteByBookId")
    void deleteByBookId_shouldCallRepository() {
        UUID bookId = UUID.randomUUID();
        doNothing().when(waitListRepository).deleteByBookId(bookId);

        waitListService.deleteByBookId(bookId);

        verify(waitListRepository).deleteByBookId(bookId);
    }

    @Test
    @DisplayName("saveWaitList - Should save and return waitlist")
    void saveWaitList_shouldSaveAndReturnWaitList() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        WaitList testWaitList = WaitListServiceTestUtil.createSampleWaitList(testUser, testBook, WaitListStatus.WAITING);

        when(waitListRepository.save(any(WaitList.class))).thenReturn(testWaitList);

        WaitList savedWaitList = waitListService.saveWaitList(testWaitList);

        assertNotNull(savedWaitList);
        assertEquals(testWaitList.getId(), savedWaitList.getId());
        verify(waitListRepository).save(testWaitList);
    }

    @Test
    @DisplayName("getByUserIdAndBookIdAndStatus - Should return waitlist when exists")
    void getByUserIdAndBookIdAndStatus_whenExists_thenReturnWaitList() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        WaitList testWaitList = WaitListServiceTestUtil.createSampleWaitList(testUser, testBook, WaitListStatus.WAITING);
        WaitListStatus status = WaitListStatus.WAITING;

        when(waitListRepository.findByUserIdAndBookIdAndStatus(userId, bookId, status.name()))
            .thenReturn(Optional.of(testWaitList));

        Optional<WaitList> actualWaitListOptional = waitListService.getByUserIdAndBookIdAndStatus(userId, bookId, status);

        assertTrue(actualWaitListOptional.isPresent());
        assertEquals(testWaitList, actualWaitListOptional.get());
        verify(waitListRepository).findByUserIdAndBookIdAndStatus(userId, bookId, status.name());
    }

    @Test
    @DisplayName("getByUserIdAndBookIdAndStatus - Should return empty Optional when not exists")
    void getByUserIdAndBookIdAndStatus_whenNotExists_thenReturnEmptyOptional() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        WaitListStatus status = WaitListStatus.WAITING;
        when(waitListRepository.findByUserIdAndBookIdAndStatus(userId, bookId, status.name()))
            .thenReturn(Optional.empty());

        Optional<WaitList> actualWaitListOptional = waitListService.getByUserIdAndBookIdAndStatus(userId, bookId, status);

        assertTrue(actualWaitListOptional.isEmpty());
        verify(waitListRepository).findByUserIdAndBookIdAndStatus(userId, bookId, status.name());
    }

    @Test
    @DisplayName("getByBookIdAndStatus - Should return list of WaitListDto")
    void getByBookIdAndStatus_shouldReturnListOfDto() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        WaitList testWaitList = WaitListServiceTestUtil.createSampleWaitList(testUser, testBook, WaitListStatus.WAITING);
        WaitListStatus status = WaitListStatus.WAITING;
        List<WaitList> waitLists = List.of(testWaitList);
        List<WaitListDto> expectedDtos = WaitListMapper.INSTANCE.toWaitListDtoList(waitLists);

        when(waitListRepository.findByBookIdAndStatus(bookId, status)).thenReturn(waitLists);

        List<WaitListDto> actualDtos = waitListService.getByBookIdAndStatus(bookId, status);

        assertNotNull(actualDtos);
        assertEquals(expectedDtos.size(), actualDtos.size());
        if (!expectedDtos.isEmpty() && !actualDtos.isEmpty()) {
            assertEquals(expectedDtos.getFirst().id(), actualDtos.getFirst().id());
        }
        verify(waitListRepository).findByBookIdAndStatus(bookId, status);
    }
    
    @Test
    @DisplayName("getByBookIdAndStatus - Should return empty list when no waitlists exist")
    void getByBookIdAndStatus_whenNoWaitlists_shouldReturnEmptyList() {
        UUID bookId = UUID.randomUUID();
        WaitListStatus status = WaitListStatus.WAITING;
        when(waitListRepository.findByBookIdAndStatus(bookId, status)).thenReturn(Collections.emptyList());

        List<WaitListDto> actualDtos = waitListService.getByBookIdAndStatus(bookId, status);

        assertNotNull(actualDtos);
        assertTrue(actualDtos.isEmpty());
        verify(waitListRepository).findByBookIdAndStatus(bookId, status);
    }

    @Test
    @DisplayName("getAllWaitLists - Should return pageable DTO of waitlists")
    void getAllWaitLists_shouldReturnPageableDto() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        WaitList testWaitList = WaitListServiceTestUtil.createSampleWaitList(testUser, testBook, WaitListStatus.WAITING);
        
        int currentPage = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        List<WaitList> waitListsContent = List.of(testWaitList);
        Page<WaitList> page = new PageImpl<>(waitListsContent, pageable, waitListsContent.size());
        
        when(waitListRepository.findAll(pageable)).thenReturn(page);

        PageableDto<WaitListDto> result = waitListService.getAllWaitLists(currentPage, pageSize);

        assertNotNull(result);
        assertEquals(1, result.elements().size());
        assertEquals(testWaitList.getId(), ((List<WaitListDto>)result.elements()).getFirst().id());
        assertEquals(currentPage, result.currentPage());
        assertEquals(pageSize, result.totalElementsPerPage()); 
        assertEquals(1, result.totalPages());
        verify(waitListRepository).findAll(pageable);
    }

    @Test
    @DisplayName("getWaitListsByBookId - Should return pageable DTO for a book")
    void getWaitListsByBookId_shouldReturnPageableDtoForBook() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        WaitList testWaitList = WaitListServiceTestUtil.createSampleWaitList(testUser, testBook, WaitListStatus.WAITING);

        int currentPage = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        List<WaitList> waitListsContent = List.of(testWaitList);
        Page<WaitList> page = new PageImpl<>(waitListsContent, pageable, waitListsContent.size());

        when(waitListRepository.findByBookId(bookId, pageable)).thenReturn(page);

        PageableDto<WaitListDto> result = waitListService.getWaitListsByBookId(bookId, currentPage, pageSize);

        assertNotNull(result);
        assertEquals(1, result.elements().size());
        assertEquals(testWaitList.getId(), ((List<WaitListDto>)result.elements()).getFirst().id());

        assertEquals(currentPage, result.currentPage());
        assertEquals(pageSize, result.totalElementsPerPage()); 
        verify(waitListRepository).findByBookId(bookId, pageable);
    }

    @Test
    @DisplayName("getWaitListsByPatronId - Should return DTO list for active patron")
    void getWaitListsByPatronId_whenPatronActive_shouldReturnDtoList() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        WaitList testWaitList = WaitListServiceTestUtil.createSampleWaitList(testUser, testBook, WaitListStatus.WAITING);

        when(userService.getUserByIdOrElseThrow(userId)).thenReturn(testUser);
        List<WaitList> waitLists = List.of(testWaitList);
        when(waitListRepository.findByUser_IdAndStatusIn(userId, List.of(WaitListStatus.WAITING, WaitListStatus.READY_FOR_PICKUP)))
            .thenReturn(waitLists);

        List<WaitListDto> result = waitListService.getWaitListsByPatronId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testWaitList.getId(), result.getFirst().id());
        verify(userService).getUserByIdOrElseThrow(userId);
        verify(waitListRepository).findByUser_IdAndStatusIn(userId, List.of(WaitListStatus.WAITING, WaitListStatus.READY_FOR_PICKUP));
    }

    @Test
    @DisplayName("getWaitListsByPatronId - Should throw EntityNotFoundException if patron not found")
    void getWaitListsByPatronId_whenPatronNotFound_shouldThrowEntityNotFoundException() {
        UUID nonExistentUserId = UUID.randomUUID();
        when(userService.getUserByIdOrElseThrow(nonExistentUserId)).thenThrow(new EntityNotFoundException("Patron not found"));

        assertThrows(EntityNotFoundException.class, () -> waitListService.getWaitListsByPatronId(nonExistentUserId));

        verify(userService).getUserByIdOrElseThrow(nonExistentUserId);
        verify(waitListRepository, never()).findByUser_IdAndStatusIn(any(UUID.class), anyList());
    }
    
    @Test
    @DisplayName("getWaitListsByPatronId - Should throw PatronStatusValidationException if patron not active")
    void getWaitListsByPatronId_whenPatronNotActive_shouldThrowPatronStatusValidationException() {
        UUID userId = UUID.randomUUID();
        User inactiveUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.INACTIVE);
        when(userService.getUserByIdOrElseThrow(userId)).thenReturn(inactiveUser);

        assertThrows(PatronStatusValidationException.class, () -> waitListService.getWaitListsByPatronId(userId));

        verify(userService).getUserByIdOrElseThrow(userId);
        verify(waitListRepository, never()).findByUser_IdAndStatusIn(any(UUID.class), anyList());
    }

    @Test
    @DisplayName("getWaitListsByIdsOrElseThrow - Should return set of waitlists when all found")
    void getWaitListsByIdsOrElseThrow_whenAllFound_shouldReturnSet() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        UUID waitListId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        WaitList testWaitList = WaitListServiceTestUtil.createSampleWaitList(testUser, testBook, WaitListStatus.WAITING);
        testWaitList.setId(waitListId);

        Set<UUID> waitListIdsSet = Set.of(waitListId);
        List<WaitList> foundWaitLists = List.of(testWaitList);
        when(waitListRepository.findAllById(waitListIdsSet)).thenReturn(foundWaitLists);

        Set<WaitList> result = waitListService.getWaitListsByIdsOrElseThrow(waitListIdsSet);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(testWaitList));
        verify(waitListRepository).findAllById(waitListIdsSet);
    }

    @Test
    @DisplayName("getWaitListsByIdsOrElseThrow - Should throw EntityNotFoundException when some IDs not found")
    void getWaitListsByIdsOrElseThrow_whenSomeNotFound_shouldThrowEntityNotFoundException() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        UUID waitListId = UUID.randomUUID();
        User testUser = WaitListServiceTestUtil.createSampleUser(userId, PatronStatus.ACTIVE);
        Book testBook = WaitListServiceTestUtil.createSampleBook(bookId, BookStatus.ACTIVE);
        WaitList testWaitList = WaitListServiceTestUtil.createSampleWaitList(testUser, testBook, WaitListStatus.WAITING);
        testWaitList.setId(waitListId);

        UUID missingId = UUID.randomUUID();
        Set<UUID> waitListIdsSet = Set.of(waitListId, missingId);
        List<WaitList> foundWaitLists = List.of(testWaitList); 
        
        when(waitListRepository.findAllById(waitListIdsSet)).thenReturn(foundWaitLists);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> waitListService.getWaitListsByIdsOrElseThrow(waitListIdsSet));
        
        assertTrue(exception.getMessage().contains(missingId.toString()));
        verify(waitListRepository).findAllById(waitListIdsSet);
    }
}
