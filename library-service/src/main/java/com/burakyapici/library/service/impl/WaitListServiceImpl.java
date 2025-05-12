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
import com.burakyapici.library.exception.WaitListNotFoundException;
import com.burakyapici.library.service.BookCopyService;
import com.burakyapici.library.service.BookService;
import com.burakyapici.library.service.UserService;
import com.burakyapici.library.service.WaitListService;
import com.burakyapici.library.service.validation.waitlist.CancelHoldHandlerRequest;
import com.burakyapici.library.service.validation.waitlist.CancelHoldValidationHandler;
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
    private static final int TOTAL_ELEMENTS_PER_PAGE = 10;
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

        PlaceHoldHandlerRequest validationRequest = new PlaceHoldHandlerRequest(
            patron,
            book,
            placeHoldRequest.bookId(),
            Optional.empty(),
            availableCopies
        );
        
        waitListValidationHandler.handle(validationRequest);

        WaitList waitList = new WaitList();
        waitList.setUser(patron);
        waitList.setStartDate(LocalDateTime.now());
        waitList.setStatus(WaitListStatus.WAITING);

        waitListRepository.save(waitList);

        return WaitListMapper.INSTANCE.waitListToWaitListDto(waitList);
    }

    @Override
    public void cancelHold(UUID waitListId, UUID patronId) {
        WaitList waitList = findWaitListByIdOrElseThrow(waitListId);
        User patron = userService.getUserByIdOrElseThrow(patronId);

        CancelHoldHandlerRequest validationRequest = new CancelHoldHandlerRequest(
            patron,
            waitList,
            waitListId
        );
        
        cancelHoldValidationHandler.handle(validationRequest);

        if (WaitListStatus.READY_FOR_PICKUP.equals(waitList.getStatus())) {
            BookCopy reservedBookCopy = waitList.getReservedBookCopy();
            if (reservedBookCopy != null) {
                reservedBookCopy.setStatus(BookCopyStatus.AVAILABLE);
                bookCopyService.saveBookCopy(reservedBookCopy);
            }
        }

        waitList.setStatus(WaitListStatus.CANCELLED);
        waitList.setEndDate(LocalDateTime.now());

        waitListRepository.save(waitList);
    }

    @Override
    public Set<WaitList> getWaitListsByIdsOrElseThrow(Set<UUID> waitListIds) {
        List<WaitList> waitLists = waitListRepository.findAllById(waitListIds);

        if (waitLists.size() != waitListIds.size()) {
            Set<UUID> foundWaitListIds = new HashSet<>();
            for (WaitList waitList : waitLists) {
                foundWaitListIds.add(waitList.getId());
            }

            Set<UUID> missingWaitListIds = waitListIds.stream()
                .filter(id -> !foundWaitListIds.contains(id))
                .collect(Collectors.toSet());

            throw new WaitListNotFoundException(
                "The following wait list IDs could not be found: " +
                    missingWaitListIds.stream()
                        .map(UUID::toString)
                        .collect(Collectors.joining(", "))
            );
        }

        return new HashSet<>(waitLists);
    }

    @Override
    public List<WaitListDto> getWaitListsByPatronId(UUID patronId) {
        User patron = userService.getUserByIdOrElseThrow(patronId);

        if(patron.getPatronStatus() == null || !PatronStatus.ACTIVE.equals(patron.getPatronStatus())) {
            throw new PatronStatusValidationException("Patron status is not active");
        }

        List<WaitList> waitLists = waitListRepository.findByUser_IdAndStatusIn(
            patronId,
            List.of(WaitListStatus.WAITING, WaitListStatus.READY_FOR_PICKUP)
        );

        return WaitListMapper.INSTANCE.waitListToWaitListDto(waitLists);
    }

    @Override
    public PageableDto<WaitListDto> getWaitListsByBookId(UUID bookId, int currentPage, int pageSize) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<WaitList> allWaitListsPage = waitListRepository.findByBookId(bookId, pageable);
        List<WaitListDto> waitListDto = WaitListMapper.INSTANCE.waitListToWaitListDto(allWaitListsPage.getContent());

        return new PageableDto<>(
            waitListDto,
            allWaitListsPage.getTotalPages(),
            WaitListServiceImpl.TOTAL_ELEMENTS_PER_PAGE,
            currentPage,
            allWaitListsPage.hasNext(),
            allWaitListsPage.hasPrevious()
        );
    }

    @Override
    public PageableDto<WaitListDto> getAllWaitLists(int currentPage, int pageSize) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<WaitList> allWaitListsPage = waitListRepository.findAll(pageable);
        List<WaitListDto> waitListDto = WaitListMapper.INSTANCE.waitListToWaitListDto(allWaitListsPage.getContent());

        return new PageableDto<>(
            waitListDto,
            allWaitListsPage.getTotalPages(),
            WaitListServiceImpl.TOTAL_ELEMENTS_PER_PAGE,
            currentPage,
            allWaitListsPage.hasNext(),
            allWaitListsPage.hasPrevious()
        );
    }

    @Override
    public boolean existsWaitListForBookId(UUID bookCopyId) {
        return waitListRepository.existsByBook_Id(bookCopyId);
    }

    @Override
    public List<WaitListDto> getByBookIdAndStatus(UUID bookId, WaitListStatus waitListStatus) {
        List<WaitList> waitLists = waitListRepository.findByBookIdAndStatus(bookId, waitListStatus);
        return WaitListMapper.INSTANCE.waitListToWaitListDto(waitLists);
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

    private WaitList findWaitListByIdOrElseThrow(UUID waitListId) {
        return waitListRepository.findById(waitListId)
            .orElseThrow(() -> new EntityNotFoundException("Wait list not found with ID: " + waitListId));
    }
}
