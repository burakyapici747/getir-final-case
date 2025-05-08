package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.request.PlaceHoldRequest;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.api.dto.response.WaitListResponse;
import com.burakyapici.library.common.mapper.WaitListMapper;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.WaitListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/waitlist")
public class WaitListController {
    private final WaitListService waitListService;

    public WaitListController(WaitListService waitListService) {
        this.waitListService = waitListService;
    }

    @GetMapping("/my-holds")
    public ResponseEntity<List<WaitListResponse>> getMyHolds(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(
            WaitListMapper.INSTANCE.waitListDtoToWaitListResponse(
                waitListService.getWaitListsByPatronId(userDetails.getId())
            )
        );
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<PageableResponse<WaitListResponse>> getWaitListForBook(
        @PathVariable UUID bookId,
        @RequestParam(name = "page", required = false) int currentPage,
        @RequestParam(name = "size", required = false) int pageSize
    ) {
        return ResponseEntity.ok(
            WaitListMapper.INSTANCE.pageableDtoToPageableResponse(
                waitListService.getWaitListsByBookId(bookId, currentPage, pageSize)
            )
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<PageableResponse<WaitListResponse>> getAllWaitLists(
        @RequestParam(name = "page", required = false) int currentPage,
        @RequestParam(name = "size", required = false) int pageSize
    ) {
        return ResponseEntity.ok(
            WaitListMapper.INSTANCE.pageableDtoToPageableResponse(
                waitListService.getAllWaitLists(currentPage, pageSize)
            )
        );
    }

    @PostMapping
    public ResponseEntity<WaitListResponse> placeHold(
        @RequestBody PlaceHoldRequest placeHoldRequest,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(
            WaitListMapper.INSTANCE.waitListDtoToWaitListResponse(
                waitListService.placeHold(placeHoldRequest, userDetails.getId())
            )
        );
    }

    @DeleteMapping("/{waitListId}")
    public ResponseEntity<Void> cancelHold(
        @PathVariable UUID waitListId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        waitListService.cancelHold(waitListId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
