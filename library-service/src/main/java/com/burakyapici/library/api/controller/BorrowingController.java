package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.ApiResponse;
import com.burakyapici.library.api.dto.request.BorrowBookCopyRequest;
import com.burakyapici.library.api.dto.request.BorrowReturnRequest;
import com.burakyapici.library.domain.dto.BorrowDto;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.BorrowingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/borrowing")
public class BorrowingController {
    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PostMapping("/{barcode}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<BorrowDto>> borrowBookCopyByBarcode(
        @PathVariable String barcode,
        @RequestBody BorrowBookCopyRequest borrowBookCopyRequest,
        @AuthenticationPrincipal UserDetailsImpl librarian
    ) {
        BorrowDto borrowDto = borrowingService.borrowBookCopyByBarcode(barcode, borrowBookCopyRequest, librarian);
        return ApiResponse.createdResponse(borrowDto, "Book successfully borrowed", borrowDto.id());
    }

    @PatchMapping("/{barcode}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<BorrowDto>> returnBookCopyByBarcode(
        @PathVariable String barcode,
        @RequestBody BorrowReturnRequest request,
        @AuthenticationPrincipal UserDetailsImpl librarian
    ) {
        BorrowDto borrowDto = borrowingService.returnBookCopyByBarcode(barcode, request, librarian);
        return ApiResponse.okResponse(borrowDto, "Book successfully returned");
    }
}