package com.burakyapici.library.service.impl;

import com.burakyapici.library.api.advice.DataConflictException;
import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.api.dto.request.BookAvailabilityUpdateEvent;
import com.burakyapici.library.api.dto.request.BookCopyCreateRequest;
import com.burakyapici.library.api.dto.request.BookCopySearchCriteria;
import com.burakyapici.library.api.dto.request.BookCopyUpdateRequest;
import com.burakyapici.library.common.mapper.BookCopyMapper;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.repository.BookCopyRepository;
import com.burakyapici.library.domain.specification.BookCopySpecifications;
import com.burakyapici.library.service.BookCopyService;
import com.burakyapici.library.service.BookService;
import com.burakyapici.library.service.BorrowingService;
import com.burakyapici.library.service.WaitListService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.UUID;

@Service
public class BookCopyServiceImpl implements BookCopyService {
    private static final int TOTAL_ELEMENTS_PER_PAGE = 10;
    private final BookCopyRepository bookCopyRepository;
    private final BookService bookService;
    private final Sinks.Many<BookAvailabilityUpdateEvent> bookAvailabilitySink;
    private final BorrowingService borrowingService;
    private final WaitListService waitListService;

    public BookCopyServiceImpl(
        BookCopyRepository bookCopyRepository,
        @Lazy
        BookService bookService,
        Sinks.Many<BookAvailabilityUpdateEvent> bookAvailabilitySink,
        @Lazy
        BorrowingService borrowingService, WaitListService waitListService
    ) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookService = bookService;
        this.bookAvailabilitySink = bookAvailabilitySink;
        this.borrowingService = borrowingService;
        this.waitListService = waitListService;
    }

    @Override
    public BookCopyDto getBookCopyById(UUID id) {
        return BookCopyMapper.INSTANCE.bookCopyToBookCopyDto(findByIdOrElseThrow(id));
    }

    @Override
    @Transactional
    public void deleteBookCopyById(UUID id) {
        BookCopy bookCopy = findByIdOrElseThrow(id);
        waitListService.deleteByBookCopyId(id);
        borrowingService.deleteAllByBookCopyId(id);
        bookCopyRepository.delete(bookCopy);
    }

    @Override
    public int countByIdAndStatus(UUID id, BookCopyStatus status) {
        return bookCopyRepository.countByBook_IdAndStatus(id, status);
    }

    @Override
    public BookCopy saveBookCopy(BookCopy bookCopy) {
        return bookCopyRepository.save(bookCopy);
    }

    @Override
    public PageableDto<BookCopyDto> searchBookCopies(
        BookCopySearchCriteria bookCopySearchCriteria,
        int currentPage,
        int pageSize
    ) {
        Specification<BookCopy> spec = BookCopySpecifications.findByCriteria(bookCopySearchCriteria);

        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<BookCopy> allBookCopiesPage = bookCopyRepository.findAll(spec, pageable);

        List<BookCopyDto> bookCopyDtoList =
                BookCopyMapper.INSTANCE.bookCopyListToBookCopyDtoList(allBookCopiesPage.getContent());

        return new PageableDto<>(
            bookCopyDtoList,
            allBookCopiesPage.getTotalPages(),
            allBookCopiesPage.getSize(),
            allBookCopiesPage.getNumber(),
            allBookCopiesPage.hasNext(),
            allBookCopiesPage.hasPrevious()
        );
    }

    @Override
    public void deleteAllByBookId(UUID bookId) {
        bookCopyRepository.deleteByBookId(bookId);
    }

    @Override
    public List<BookCopy> findByBookIdAndStatus(UUID bookId, BookCopyStatus status) {
        return bookCopyRepository.findByBookIdAndStatus(bookId, status);
    }

    @Override
    public BookCopy getBookCopyByIdOrElseThrow(UUID id) {
        return findByIdOrElseThrow(id);
    }

    @Override
    public BookCopy getBookCopyByBarcodeOrElseThrow(String barcode) {
        return findByBarcodeOrElseThrow(barcode);
    }

    @Override
    public PageableDto<BookCopyDto> getAllBookCopies(int currentPage, int pageSize) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<BookCopy> bookCopies = bookCopyRepository.findAll(pageable);
        List<BookCopyDto> bookCopyDtoList = BookCopyMapper.INSTANCE.bookCopyListToBookCopyDtoList(bookCopies.getContent());

        return new PageableDto<>(
            bookCopyDtoList,
            bookCopies.getTotalPages(),
            TOTAL_ELEMENTS_PER_PAGE,
            currentPage,
            bookCopies.hasNext(),
            bookCopies.hasPrevious()
        );
    }

    @Override
    public PageableDto<BookCopyDto> getAllBookCopiesByBookId(UUID bookId, int currentPage, int pageSize) {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookCopy> bookCopies = bookCopyRepository.findAllByBookId(bookId, pageable);
        List<BookCopyDto> bookCopyDtoList = BookCopyMapper.INSTANCE.bookCopyListToBookCopyDtoList(bookCopies.getContent());

        return new PageableDto<>(
            bookCopyDtoList,
            bookCopies.getTotalPages(),
            TOTAL_ELEMENTS_PER_PAGE,
            0,
            bookCopies.hasNext(),
            bookCopies.hasPrevious()
        );
    }

    @Override
    public BookCopyDto createBookCopy(BookCopyCreateRequest bookCopyCreateRequest) {
        validateBookCopyIsNotExistsByBarcode(bookCopyCreateRequest.barcode());
        Book book = bookService.getBookByIdOrElseThrow(bookCopyCreateRequest.bookId());
        BookCopy bookCopy = BookCopy.builder()
            .book(book)
            .barcode(bookCopyCreateRequest.barcode())
            .status(bookCopyCreateRequest.bookCopyStatus())
            .build();

        int newAvailableCount = countByIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE) + 1;

        BookAvailabilityUpdateEvent event = new BookAvailabilityUpdateEvent(book.getId(), newAvailableCount);

        bookAvailabilitySink.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);

        return BookCopyMapper.INSTANCE.bookCopyToBookCopyDto(bookCopyRepository.save(bookCopy));
    }

    @Override
    public BookCopyDto updateBookCopyById(UUID id, BookCopyUpdateRequest bookCopyUpdateRequest) {
        BookCopy bookCopy = findByIdOrElseThrow(id);
        BookCopyMapper.INSTANCE.updateBookCopyFromBookCopyUpdateRequest(bookCopyUpdateRequest, bookCopy);

        return BookCopyMapper.INSTANCE.bookCopyToBookCopyDto(bookCopyRepository.save(bookCopy));
    }

    private BookCopy findByIdOrElseThrow(UUID id) {
        return bookCopyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Book copy not found with id: " + id));
    }

    private BookCopy findByBarcodeOrElseThrow(String barcode) {
        return bookCopyRepository.findByBarcode(barcode)
            .orElseThrow(() -> new EntityNotFoundException("Book copy not found with barcode: " + barcode));
    }

    private void validateBookCopyIsNotExistsByBarcode(String barcode){
        if(bookCopyRepository.existsByBarcode(barcode)){
            throw new DataConflictException("Book copy already exists with barcode: " + barcode);
        }
    }
}
