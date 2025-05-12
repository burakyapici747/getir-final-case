package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.ApiResponse;
import com.burakyapici.library.api.dto.request.BookCopyCreateRequest;
import com.burakyapici.library.api.dto.request.BookCopySearchCriteria;
import com.burakyapici.library.api.dto.request.BookCopyUpdateRequest;
import com.burakyapici.library.api.dto.request.PageableParams;
import com.burakyapici.library.api.dto.response.BookCopyResponse;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.common.mapper.BookCopyMapper;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.service.BookCopyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1/book-copies")
public class BookCopyController {
    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageableResponse<BookCopyResponse>>> getAllBookCopies(@Valid PageableParams params) {
        PageableDto<BookCopyDto> pageResult = bookCopyService.getAllBookCopies(params.page(), params.size());
        PageableResponse<BookCopyResponse> response = BookCopyMapper.INSTANCE.toPageableResponse(pageResult);

        return ApiResponse.okResponse(response, "Book copies retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookCopyResponse>> getBookCopyById(@PathVariable("id") UUID id) {
        BookCopyDto dto = bookCopyService.getBookCopyById(id);
        BookCopyResponse response = BookCopyMapper.INSTANCE.toBookCopyResponse(dto);

        return ApiResponse.okResponse(response, "Book copy retrieved successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageableResponse<BookCopyResponse>>> searchBookCopies(
        @Valid @ModelAttribute BookCopySearchCriteria bookCopySearchCriteria,
        @Valid PageableParams params
    ) {
        PageableDto<BookCopyDto> pageResult =
                bookCopyService.searchBookCopies(bookCopySearchCriteria, params.page(), params.size());
        PageableResponse<BookCopyResponse> response = BookCopyMapper.INSTANCE.toPageableResponse(pageResult);

        return ApiResponse.okResponse(response, "Book copies retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookCopyResponse>> createBookCopy(
        @Valid @RequestBody BookCopyCreateRequest bookCopyCreateRequest
    ) {
        BookCopyDto dto = bookCopyService.createBookCopy(bookCopyCreateRequest);
        BookCopyResponse response = BookCopyMapper.INSTANCE.toBookCopyResponse(dto);

        return ApiResponse.createdResponse(response, "Book copy created successfully", response.id());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookCopyResponse>> updateBookCopy(
        @PathVariable("id") @NotNull UUID id,
        @Valid @RequestBody BookCopyUpdateRequest bookCopyUpdateRequest
    ) {
        BookCopyDto dto = bookCopyService.updateBookCopyById(id, bookCopyUpdateRequest);
        BookCopyResponse response = BookCopyMapper.INSTANCE.toBookCopyResponse(dto);

        return ApiResponse.okResponse(response, "Book copy updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBookCopyById(@PathVariable("id") UUID id) {
        bookCopyService.deleteBookCopyById(id);
        return ApiResponse.noContentResponse("Book copy deleted successfully");
    }
}