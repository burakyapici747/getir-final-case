package com.burakyapici.library.service.impl;

import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.api.dto.request.PlaceHoldRequest;
import com.burakyapici.library.common.mapper.WaitListMapper;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.dto.WaitListDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.enums.PatronStatus;
import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.model.WaitList;
import com.burakyapici.library.domain.repository.WaitListRepository;
import com.burakyapici.library.exception.PatronStatusValidationException;
import com.burakyapici.library.service.BookCopyService;
import com.burakyapici.library.service.BookService;
import com.burakyapici.library.service.UserService;
import com.burakyapici.library.service.WaitListService;
import com.burakyapici.library.service.validation.waitlist.cancel.CancelHoldHandlerRequest;
import com.burakyapici.library.service.validation.waitlist.cancel.CancelHoldValidationHandler;
import com.burakyapici.library.service.validation.waitlist.PlaceHoldHandlerRequest;
import com.burakyapici.library.service.validation.waitlist.WaitListValidationHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WaitListServiceImpl implements WaitListService {
    private final BookService bookService;
    private final UserService userService;
    private final BookCopyService bookCopyService;
    private final WaitListRepository waitListRepository;
    private final WaitListValidationHandler waitListValidationHandler;
    private final CancelHoldValidationHandler cancelHoldValidationHandler;

    public WaitListServiceImpl(
        WaitListRepository waitListRepository,
        @Lazy
        BookService bookService,
        UserService userService,
        @Lazy
        BookCopyService bookCopyService,
        @Qualifier("waitListValidationChain")
        WaitListValidationHandler waitListValidationHandler,
        @Qualifier("cancelHoldValidationChain")
        CancelHoldValidationHandler cancelHoldValidationHandler
    ) {
        this.waitListRepository = waitListRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.bookCopyService = bookCopyService;
        this.waitListValidationHandler = waitListValidationHandler;
        this.cancelHoldValidationHandler = cancelHoldValidationHandler;
    }

    @Override
    public WaitListDto placeHold(PlaceHoldRequest placeHoldRequest, UUID patronId) {
        User patron = userService.getUserByIdOrElseThrow(patronId);
        Book book = bookService.getBookByIdOrElseThrow(placeHoldRequest.bookId());

        List<BookCopy> availableCopies = bookCopyService.findByBookIdAndStatus(
            book.getId(),
            BookCopyStatus.AVAILABLE
        );

        PlaceHoldHandlerRequest validationRequest = createPlaceHoldValidationRequest(
            patron,
            book,
            placeHoldRequest.bookId(),
            availableCopies
        );

        waitListValidationHandler.handle(validationRequest);

        WaitList waitList = WaitList.builder()
            .user(patron)
            .book(book)
            .startDate(LocalDateTime.now())
            .status(WaitListStatus.WAITING)
            .build();


        return WaitListMapper.INSTANCE.toWaitListDtoList(waitListRepository.save(waitList));
    }

    @Override
    public void cancelHold(UUID waitListId, UUID patronId) {
        WaitList waitList = findWaitListByIdOrElseThrow(waitListId);
        User patron = userService.getUserByIdOrElseThrow(patronId);

        validateCancelHoldRequest(patron, waitList, waitListId);
        handleBookCopyReleaseIfReady(waitList);

        updateWaitListForCancellation(waitList);
    }

    @Override
    public Set<WaitList> getWaitListsByIdsOrElseThrow(Set<UUID> waitListIds) {
        List<WaitList> waitLists = waitListRepository.findAllById(waitListIds);

        if (waitLists.size() != waitListIds.size()) {
            Set<UUID> foundWaitListIds = extractFoundWaitListIds(waitLists);
            Set<UUID> missingWaitListIds = findMissingWaitListIds(waitListIds, foundWaitListIds);

            throw new EntityNotFoundException(
                "The following wait list IDs could not be found: " +
                formatMissingWaitListIds(missingWaitListIds)
            );
        }

        return new HashSet<>(waitLists);
    }

    @Override
    public List<WaitListDto> getWaitListsByPatronId(UUID patronId) {
        User patron = userService.getUserByIdOrElseThrow(patronId);
        validatePatronStatus(patron);

        List<WaitList> waitLists = waitListRepository.findByUser_IdAndStatusIn(
            patronId,
            List.of(WaitListStatus.WAITING, WaitListStatus.READY_FOR_PICKUP)
        );

        return WaitListMapper.INSTANCE.toWaitListDtoList(waitLists);
    }

    @Override
    public PageableDto<WaitListDto> getWaitListsByBookId(UUID bookId, int currentPage, int pageSize) {
        Pageable pageable = createPageRequest(currentPage, pageSize);
        Page<WaitList> waitListsPage = waitListRepository.findByBookId(bookId, pageable);

        return createPageableResponse(waitListsPage, pageSize);
    }

    @Override
    public PageableDto<WaitListDto> getAllWaitLists(int currentPage, int pageSize) {
        Pageable pageable = createPageRequest(currentPage, pageSize);
        Page<WaitList> waitListsPage = waitListRepository.findAll(pageable);

        return createPageableResponse(waitListsPage, pageSize);
    }

    @Override
    public boolean existsWaitListForBookId(UUID bookId) {
        return waitListRepository.existsByBook_Id(bookId);
    }

    @Override
    public List<WaitListDto> getByBookIdAndStatus(UUID bookId, WaitListStatus waitListStatus) {
        List<WaitList> waitLists = waitListRepository.findByBookIdAndStatus(bookId, waitListStatus);
        return WaitListMapper.INSTANCE.toWaitListDtoList(waitLists);
    }

    @Override
    public Optional<WaitList> getByUserIdAndBookIdAndStatus(UUID userId, UUID bookId, WaitListStatus waitListStatus) {
        return waitListRepository.findByUserIdAndBookIdAndStatus(userId, bookId, waitListStatus.name());
    }

    @Override
    public WaitList saveWaitList(WaitList waitList) {
        return waitListRepository.save(waitList);
    }

    @Override
    public void deleteByBookId(UUID bookId) {
        waitListRepository.deleteByBookId(bookId);
    }

    @Override
    public void deleteByBookCopyId(UUID bookCopyId) {
        waitListRepository.deleteByBookCopyId(bookCopyId);
    }

    @Override
    public Optional<WaitList> getTopByBookIdAndStatusOrderByStartDateAsc(UUID bookId, WaitListStatus status) {
        return waitListRepository.findTopByBookIdAndStatusOrderByStartDateAsc(bookId, status.name());
    }

    private WaitList findWaitListByIdOrElseThrow(UUID waitListId) {
        return waitListRepository.findById(waitListId)
            .orElseThrow(() -> new EntityNotFoundException("Wait list not found with ID: " + waitListId));
    }

    private PlaceHoldHandlerRequest createPlaceHoldValidationRequest(
        User patron,
        Book book,
        UUID bookId,
        List<BookCopy> availableCopies
    ) {
        return new PlaceHoldHandlerRequest(
            patron,
            book,
            bookId,
            Optional.empty(),
            availableCopies
        );
    }

    private void validateCancelHoldRequest(User patron, WaitList waitList, UUID waitListId) {
        CancelHoldHandlerRequest validationRequest = new CancelHoldHandlerRequest(
            patron,
            waitList,
            waitListId
        );

        cancelHoldValidationHandler.handle(validationRequest);
    }

    private void handleBookCopyReleaseIfReady(WaitList waitList) {
        if (WaitListStatus.READY_FOR_PICKUP.equals(waitList.getStatus())) {
            BookCopy reservedBookCopy = waitList.getReservedBookCopy();
            if (reservedBookCopy != null) {
                reservedBookCopy.setStatus(BookCopyStatus.AVAILABLE);
                bookCopyService.saveBookCopy(reservedBookCopy);
            }
        }
    }

    private void updateWaitListForCancellation(WaitList waitList) {
        waitList.setStatus(WaitListStatus.CANCELLED);
        waitList.setEndDate(LocalDateTime.now());
        waitListRepository.save(waitList);
    }

    private Set<UUID> extractFoundWaitListIds(List<WaitList> waitLists) {
        return waitLists.stream()
            .map(WaitList::getId)
            .collect(Collectors.toSet());
    }

    private Set<UUID> findMissingWaitListIds(Set<UUID> allIds, Set<UUID> foundIds) {
        return allIds.stream()
            .filter(id -> !foundIds.contains(id))
            .collect(Collectors.toSet());
    }

    private String formatMissingWaitListIds(Set<UUID> missingIds) {
        return missingIds.stream()
            .map(UUID::toString)
            .collect(Collectors.joining(", "));
    }

    private void validatePatronStatus(User patron) {
        if (patron.getPatronStatus() == null || !PatronStatus.ACTIVE.equals(patron.getPatronStatus())) {
            throw new PatronStatusValidationException("Patron status is not active");
        }
    }

    private Pageable createPageRequest(int currentPage, int pageSize) {
        return PageRequest.of(currentPage, pageSize);
    }

    private PageableDto<WaitListDto> createPageableResponse(Page<WaitList> waitListsPage, int pageSize) {
        List<WaitListDto> waitListDto = WaitListMapper.INSTANCE.toWaitListDtoList(waitListsPage.getContent());

        return new PageableDto<>(
            waitListDto,
            waitListsPage.getTotalPages(),
            pageSize,
            waitListsPage.getNumber(),
            waitListsPage.hasNext(),
            waitListsPage.hasPrevious()
        );
    }
}
