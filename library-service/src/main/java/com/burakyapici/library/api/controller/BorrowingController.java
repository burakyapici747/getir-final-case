package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.request.ReturnRequest;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.BorrowingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/borrowing")
public class BorrowingController {
    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    // TODO: Patron bilgisi authentication context icerisinden alinabilir
    @PostMapping("/{bookCopyBarcode}")
    public ResponseEntity<String> borrowBook(
        @PathVariable UUID bookCopyBarcode,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok("Book borrowed successfully");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> returnBook(@PathVariable UUID id, @RequestBody ReturnRequest request) {
        return null;
    }
}
