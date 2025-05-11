package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<WaitListResponse>>> getMyHolds(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<WaitListResponse> waitLists = WaitListMapper.INSTANCE.waitListDtoToWaitListResponse(
            waitListService.getWaitListsByPatronId(userDetails.getId())
        );

        return ApiResponse.okResponse(waitLists, "Your holds retrieved successfully");
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<PageableResponse<WaitListResponse>>> getWaitListForBook(
        @PathVariable UUID bookId,
        @RequestParam(name = "page", defaultValue = "0", required = false) int currentPage,
        @RequestParam(name = "size", defaultValue = "10", required = false) int pageSize
    ) {
        PageableResponse<WaitListResponse> waitLists = WaitListMapper.INSTANCE.pageableDtoToPageableResponse(
            waitListService.getWaitListsByBookId(bookId, currentPage, pageSize)
        );

        return ApiResponse.okResponse(waitLists, "Wait list for book retrieved successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<PageableResponse<WaitListResponse>>> getAllWaitLists(
        @RequestParam(name = "page", defaultValue = "0", required = false) int currentPage,
        @RequestParam(name = "size", defaultValue = "10", required = false) int pageSize
    ) {
        PageableResponse<WaitListResponse> waitLists = WaitListMapper.INSTANCE.pageableDtoToPageableResponse(
            waitListService.getAllWaitLists(currentPage, pageSize)
        );

        return ApiResponse.okResponse(waitLists, "All wait lists retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WaitListResponse>> placeHold(
        @RequestBody PlaceHoldRequest placeHoldRequest,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        WaitListResponse waitList = WaitListMapper.INSTANCE.waitListDtoToWaitListResponse(
            waitListService.placeHold(placeHoldRequest, userDetails.getId())
        );

        return ApiResponse.createdResponse(waitList, "Hold placed successfully", waitList.id());
    }

    @DeleteMapping("/{waitListId}")
    public ResponseEntity<ApiResponse<Void>> cancelHold(
        @PathVariable UUID waitListId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        waitListService.cancelHold(waitListId, userDetails.getId());

        return ApiResponse.noContentResponse("Hold cancelled successfully");
    }
}