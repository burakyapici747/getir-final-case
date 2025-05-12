package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.ApiResponse;
import com.burakyapici.library.api.dto.request.BookCopyCreateRequest;
import com.burakyapici.library.api.dto.request.BookCopySearchCriteria;
import com.burakyapici.library.api.dto.request.BookCopyUpdateRequest;
import com.burakyapici.library.api.dto.response.BookCopyResponse;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.common.mapper.BookCopyMapper;
import com.burakyapici.library.service.BookCopyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/book-copies")
public class BookCopyController {
    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageableResponse<BookCopyResponse>>> getAllBookCopies(
        @RequestParam(name = "page", defaultValue = "0", required = false) int currentPage,
        @RequestParam(name = "size", defaultValue = "10", required = false) int pageSize
    ) {
        PageableResponse<BookCopyResponse> bookCopies = BookCopyMapper.INSTANCE.bookCopyPageableDtoListToPageableResponse(
            bookCopyService.getAllBookCopies(currentPage, pageSize)
        );
        return ApiResponse.okResponse(bookCopies, "Book copies retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookCopyResponse>> getBookCopyById(@PathVariable("id") UUID id) {
        BookCopyResponse bookCopy = BookCopyMapper.INSTANCE.bookCopyDtoToBookCopyResponse(
            bookCopyService.getBookCopyById(id)
        );
        return ApiResponse.okResponse(bookCopy, "Book copy retrieved successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageableResponse<BookCopyResponse>>> searchBookCopies(
        @ModelAttribute BookCopySearchCriteria bookCopySearchCriteria,
        @RequestParam(name = "page", defaultValue = "0", required = false) int currentPage,
        @RequestParam(name = "size", defaultValue = "10", required = false) int pageSize
    ) {
        PageableResponse<BookCopyResponse> bookCopies = BookCopyMapper.INSTANCE.bookCopyPageableDtoListToPageableResponse(
            bookCopyService.searchBookCopies(bookCopySearchCriteria, currentPage, pageSize)
        );
        return ApiResponse.okResponse(bookCopies, "Book copies retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookCopyResponse>> createBookCopy(@RequestBody BookCopyCreateRequest bookCopyCreateRequest) {
        BookCopyResponse createdBookCopy = BookCopyMapper.INSTANCE.bookCopyDtoToBookCopyResponse(
            bookCopyService.createBookCopy(bookCopyCreateRequest)
        );
        return ApiResponse.createdResponse(createdBookCopy, "Book copy created successfully", createdBookCopy.id());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookCopyResponse>> updateBookCopy(
        @PathVariable("id") UUID id,
        @RequestBody BookCopyUpdateRequest bookCopyUpdateRequest
    ) {
        BookCopyResponse updatedBookCopy = BookCopyMapper.INSTANCE.bookCopyDtoToBookCopyResponse(
            bookCopyService.updateBookCopyById(id, bookCopyUpdateRequest)
        );
        return ApiResponse.okResponse(updatedBookCopy, "Book copy updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBookCopyById(@PathVariable("id") UUID id) {
        bookCopyService.deleteBookCopyById(id);
        return ApiResponse.noContentResponse("Book copy deleted successfully");
    }
}