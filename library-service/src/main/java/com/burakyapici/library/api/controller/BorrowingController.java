package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.ApiResponse;
import com.burakyapici.library.api.dto.request.BorrowBookCopyRequest;
import com.burakyapici.library.api.dto.request.BorrowReturnRequest;
import com.burakyapici.library.domain.dto.BorrowDto;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.BorrowingService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<BorrowDto>> borrowBookCopyByBarcode(
        @PathVariable(value = "barcode") String barcode,
        @RequestBody BorrowBookCopyRequest borrowBookCopyRequest,
        @AuthenticationPrincipal UserDetailsImpl librarian
    ) {
        return ApiResponse.okResponse(
            borrowingService.borrowBookCopyByBarcode(barcode, borrowBookCopyRequest, librarian),
            "Book successfully borrowed"
        );
    }

    @PatchMapping("/{barcode}")
    public ResponseEntity<ApiResponse<BorrowDto>> returnBookCopyByBarcode(
        @PathVariable("barcode") String barcode,
        @RequestBody BorrowReturnRequest request,
        @AuthenticationPrincipal UserDetailsImpl librarian
    ) {
        return ApiResponse.okResponse(
            borrowingService.returnBookCopyByBarcode(barcode, request, librarian),
            "Book successfully returned"
        );
    }
}