package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.response.ApiResponse;
import com.burakyapici.library.api.dto.request.PageableParams;
import com.burakyapici.library.api.dto.request.PlaceHoldRequest;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.api.dto.response.WaitListResponse;
import com.burakyapici.library.common.mapper.WaitListMapper;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.dto.WaitListDto;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.WaitListService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(path = "/api/v1/waitlist", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Waitlist", description = "Operations for managing book hold requests")
public class WaitListController {
    private final WaitListService waitListService;

    public WaitListController(WaitListService waitListService) {
        this.waitListService = waitListService;
    }

    @GetMapping(path = "/my-holds")
    @PreAuthorize("hasRole('ROLE_PATRON')")
    @Operation(summary = "Get current user's holds", description = "Retrieves the waitlist entries (holds) placed by the authenticated patron.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Holds retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<WaitListResponse>>> getMyHolds(
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<WaitListResponse> waitLists = WaitListMapper.INSTANCE.toWaitListResponse(
            waitListService.getWaitListsByPatronId(userDetails.getId())
        );
        return ApiResponse.okResponse(waitLists, "Your holds retrieved successfully");
    }

    @GetMapping(path = "/book/{bookId}")
    @Operation(summary = "Get waitlist for a book", description = "Retrieves the waitlist entries for a specific book.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Waitlist for book retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<ApiResponse<PageableResponse<WaitListResponse>>> getWaitListForBook(
        @Parameter(description = "UUID of the book") @PathVariable("bookId") UUID bookId,
        @Valid PageableParams params
    ) {
        PageableDto<WaitListDto> pageResult = waitListService.getWaitListsByBookId(bookId, params.page(), params.size());
        PageableResponse<WaitListResponse> response = WaitListMapper.INSTANCE.toPageableResponse(pageResult);
        return ApiResponse.okResponse(response, "Wait list for book retrieved successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Get all waitlist entries", description = "Retrieves all waitlist entries. Only librarians can access this endpoint.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All waitlists retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<PageableResponse<WaitListResponse>>> getAllWaitLists(
        @Valid PageableParams params
    ) {
        PageableDto<WaitListDto> pageResult = waitListService.getAllWaitLists(params.page(), params.size());
        PageableResponse<WaitListResponse> response = WaitListMapper.INSTANCE.toPageableResponse(pageResult);
        return ApiResponse.okResponse(response, "All wait lists retrieved successfully");
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Place a hold request", description = "Adds the authenticated user to the waitlist for a book.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Hold placed successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    public ResponseEntity<ApiResponse<WaitListResponse>> placeHold(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Hold placement request body",
            content = @Content(schema = @Schema(implementation = PlaceHoldRequest.class))
        )
        @Valid @RequestBody PlaceHoldRequest placeHoldRequest,
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        WaitListResponse waitList = WaitListMapper.INSTANCE.toWaitListResponse(
            waitListService.placeHold(placeHoldRequest, userDetails.getId())
        );
        return ApiResponse.createdResponse(waitList, "Hold placed successfully", waitList.id());
    }

    @DeleteMapping(path = "/{waitListId}")
    @Operation(summary = "Cancel hold request", description = "Cancels a hold request of the authenticated user.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Hold cancelled successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Hold not found")
    })
    public ResponseEntity<ApiResponse<Void>> cancelHold(
        @Parameter(description = "UUID of the hold entry") @PathVariable("waitListId") UUID waitListId,
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        waitListService.cancelHold(waitListId, userDetails.getId());
        return ApiResponse.noContentResponse("Hold cancelled successfully");
    }
}
