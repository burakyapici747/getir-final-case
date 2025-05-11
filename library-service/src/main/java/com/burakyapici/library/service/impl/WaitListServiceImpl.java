package com.burakyapici.library.service.impl;

import com.burakyapici.library.api.advice.DataConflictException;
import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.api.advice.UnauthorizedResourceAccessException;
import com.burakyapici.library.api.advice.UnprocessableEntityException;
import com.burakyapici.library.api.dto.request.PlaceHoldRequest;
import com.burakyapici.library.common.mapper.WaitListMapper;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.dto.WaitListDto;
import com.burakyapici.library.domain.enums.BookStatus;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.enums.PatronStatus;
import com.burakyapici.library.domain.enums.Role;
import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.model.WaitList;
import com.burakyapici.library.domain.repository.BookCopyRepository;
import com.burakyapici.library.domain.repository.WaitListRepository;
import com.burakyapici.library.exception.BookStatusValidationException;
import com.burakyapici.library.exception.PatronStatusValidationException;
import com.burakyapici.library.exception.WaitListNotFoundException;
import com.burakyapici.library.service.BookService;
import com.burakyapici.library.service.UserService;
import com.burakyapici.library.service.WaitListService;
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
    private final WaitListRepository waitListRepository;
    private final BookCopyRepository bookCopyRepository;

    public WaitListServiceImpl(
        WaitListRepository waitListRepository,
        @Lazy
        BookService bookService,
        UserService userService,
        BookCopyRepository bookCopyRepository
    ) {
        this.waitListRepository = waitListRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.bookCopyRepository = bookCopyRepository;
    }

    @Override
    public WaitListDto placeHold(PlaceHoldRequest placeHoldRequest, UUID patronId) {
        User patron = userService.getUserByIdOrElseThrow(patronId);
        if(!PatronStatus.ACTIVE.equals(patron.getPatronStatus())) {
            throw new PatronStatusValidationException(patron.getPatronStatus().getDescription());
        }

        Book book = bookService.getBookByIdOrElseThrow(placeHoldRequest.bookId());
        if(!BookStatus.ACTIVE.equals(book.getBookStatus())){
            throw new BookStatusValidationException(book.getBookStatus().getDescription());
        }

        waitListRepository.findWaitListByBookIdAndWaitListStatusIn(
            placeHoldRequest.bookId(),
            Set.of(WaitListStatus.WAITING, WaitListStatus.READY_FOR_PICKUP)
        ).ifPresent(waitList -> {
            throw new DataConflictException("There is already a wait list for this book.");
        });

        //TODO: Wait List LIMIT kontrolu yapilacak.
        //TODO: Book availability (müsait kopya var mı) kontrolünü ekleyin (BookCopyService kullanarak) eger musait yoksa .
        //TODO: Patron'un herhangi bir cezasi var mi kontrolu

        WaitList waitList = WaitList.builder()
            .startDate(LocalDateTime.now())
            .status(WaitListStatus.WAITING)
            .build();

        waitListRepository.save(waitList);
        bookService.saveBook(book);

        return WaitListMapper.INSTANCE.waitListToWaitListDto(waitList);
    }

    @Override
    public void cancelHold(UUID waitListId, UUID patronId) {
        WaitList waitList = findWaitListByIdOrElseThrow(waitListId);
        User patron = userService.getUserByIdOrElseThrow(patronId);

        if( !patron.getId().equals(waitList.getUser().getId()) && Role.PATRON.equals(patron.getRole()) ){
            throw new UnauthorizedResourceAccessException("You cannot cancel a wait list that does not belong to you.");
        }

        if( !(WaitListStatus.WAITING.equals(waitList.getStatus()) || WaitListStatus.READY_FOR_PICKUP.equals(waitList.getStatus())) ){
            throw new UnprocessableEntityException("You can only cancel a wait list that is in WAITING or READY_FOR_PICKUP status.");
        }

        //TODO: Eğer iptal edilen bekleme kaydının önceki durumu READY_FOR_PICKUP ise, ilgili BookCopy'yi serbest
        // bırakma (ON_HOLD'dan çıkarma, status güncelleme, reservedForWaitList=null yapma) mantığını ekleyin
        // (BookCopyService kullanarak).

        waitList.setStatus(WaitListStatus.CANCELLED);

        waitListRepository.save(waitList);
    }

    @Override
    public Set<WaitList> getWaitListsByIdsOrElseThrow(Set<UUID> waitListIds) {
        List<WaitList> waitLists = waitListRepository.findAllById(waitListIds);

        if (waitLists.size() != waitListIds.size()) {
            Set<UUID> foundWaitListIds = waitLists.stream()
                .map(WaitList::getId)
                .collect(Collectors.toSet());

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

        if(!PatronStatus.ACTIVE.equals(patron.getPatronStatus())) {
            throw new PatronStatusValidationException(patron.getPatronStatus().getDescription());
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

    private void processWaitListQueueForBook(UUID bookId) {
        List<WaitList> waitingList = waitListRepository.findByBookIdAndStatusOrderByStartDateAsc(
            bookId, WaitListStatus.WAITING.name());
        
        if(waitingList.isEmpty()) {
            return;
        }
        
        List<BookCopy> availableCopies = bookCopyRepository.findByBookIdAndStatus(
            bookId, BookCopyStatus.AVAILABLE);
        
        if(availableCopies.isEmpty()) {
            return;
        }
        
        WaitList nextWaitList = waitingList.getFirst();
        BookCopy bookCopy = availableCopies.getFirst();
        
        nextWaitList.setReservedBookCopy(bookCopy);
        nextWaitList.setStatus(WaitListStatus.READY_FOR_PICKUP);
        waitListRepository.save(nextWaitList);
        
        bookCopy.setStatus(BookCopyStatus.ON_HOLD);
        bookCopyRepository.save(bookCopy);
    }

    private WaitList findWaitListByIdOrElseThrow(UUID waitListId) {
        return waitListRepository.findById(waitListId)
            .orElseThrow(() -> new EntityNotFoundException("Wait list not found with ID: " + waitListId));
    }
}
