package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.ApiResponse;
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

@RestController
@RequestMapping(path = "/api/v1/waitlist", produces = MediaType.APPLICATION_JSON_VALUE)
public class WaitListController {
    private final WaitListService waitListService;

    public WaitListController(WaitListService waitListService) {
        this.waitListService = waitListService;
    }

    @GetMapping(path = "/my-holds")
    @PreAuthorize("hasRole('ROLE_PATRON')")
    public ResponseEntity<ApiResponse<List<WaitListResponse>>> getMyHolds(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<WaitListResponse> waitLists = WaitListMapper.INSTANCE.toWaitListResponse(
            waitListService.getWaitListsByPatronId(userDetails.getId())
        );

        return ApiResponse.okResponse(waitLists, "Your holds retrieved successfully");
    }

    @GetMapping(path = "/book/{bookId}")
    public ResponseEntity<ApiResponse<PageableResponse<WaitListResponse>>> getWaitListForBook(
        @PathVariable("bookId") UUID bookId,
        @Valid PageableParams params
    ) {
        PageableDto<WaitListDto> pageResult = waitListService.getWaitListsByBookId(bookId, params.page(), params.size());

        PageableResponse<WaitListResponse> response = WaitListMapper.INSTANCE.toPageableResponse(pageResult);

        return ApiResponse.okResponse(response, "Wait list for book retrieved successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<PageableResponse<WaitListResponse>>> getAllWaitLists(
        @Valid PageableParams params
    ) {
        PageableDto<WaitListDto> pageResult = waitListService.getAllWaitLists(params.page(), params.size());

        PageableResponse<WaitListResponse> response = WaitListMapper.INSTANCE.toPageableResponse(pageResult);

        return ApiResponse.okResponse(response, "All wait lists retrieved successfully");
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<WaitListResponse>> placeHold(
        @Valid @RequestBody PlaceHoldRequest placeHoldRequest,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        WaitListResponse waitList = WaitListMapper.INSTANCE.toWaitListResponse(
            waitListService.placeHold(placeHoldRequest, userDetails.getId())
        );

        return ApiResponse.createdResponse(waitList, "Hold placed successfully", waitList.id());
    }

    @DeleteMapping(path = "/{waitListId}")
    public ResponseEntity<ApiResponse<Void>> cancelHold(
        @PathVariable("waitListId") UUID waitListId,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        waitListService.cancelHold(waitListId, userDetails.getId());

        return ApiResponse.noContentResponse("Hold cancelled successfully");
    }
}