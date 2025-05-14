package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.response.ApiResponse;
import com.burakyapici.library.api.dto.request.BorrowBookCopyRequest;
import com.burakyapici.library.api.dto.request.BorrowReturnRequest;
import com.burakyapici.library.domain.dto.BorrowingDto;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.BorrowingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/borrowing")
@Validated
@Tag(name = "Borrowing", description = "Endpoints for borrowing and returning book copies")
public class BorrowingController {
    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PostMapping("/{barcode}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Borrow a book copy", description = "Librarian borrows a book for a user using the book's barcode.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book successfully borrowed",
                content = @Content(schema = @Schema(implementation = BorrowingDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book or user not found")
    })
    public ResponseEntity<ApiResponse<BorrowingDto>> borrowBookCopyByBarcode(
        @Parameter(description = "Barcode of the book copy to borrow") @PathVariable(value = "barcode") String barcode,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Information about which user is borrowing",
            content = @Content(schema = @Schema(implementation = BorrowBookCopyRequest.class))
        )
        @RequestBody BorrowBookCopyRequest borrowBookCopyRequest,
        @Parameter(hidden = true) @Valid @NotNull @AuthenticationPrincipal UserDetailsImpl librarian
    ) {
        return ApiResponse.okResponse(
            borrowingService.borrowBookCopyByBarcode(barcode, borrowBookCopyRequest, librarian),
            "Book successfully borrowed"
        );
    }

    @PatchMapping("/{barcode}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Return a book copy", description = "Librarian returns a previously borrowed book using the book's barcode.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book successfully returned",
                content = @Content(schema = @Schema(implementation = BorrowingDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Borrow record not found")
    })
    public ResponseEntity<ApiResponse<BorrowingDto>> returnBookCopyByBarcode(
        @Parameter(description = "Barcode of the book copy to return") @PathVariable("barcode") String barcode,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Information about the return, including return type",
            content = @Content(schema = @Schema(implementation = BorrowReturnRequest.class))
        )
        @RequestBody BorrowReturnRequest request,
        @Parameter(hidden = true) @Valid @NotNull @AuthenticationPrincipal UserDetailsImpl librarian
    ) {
        return ApiResponse.okResponse(
                borrowingService.returnBookCopyByBarcode(barcode, request, librarian),
                "Book successfully returned"
        );
    }
}
