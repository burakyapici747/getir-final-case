package com.burakyapici.library.service;

import com.burakyapici.library.api.dto.request.PlaceHoldRequest;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.dto.WaitListDto;
import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.model.WaitList;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface WaitListService {
    WaitListDto placeHold(PlaceHoldRequest placeHoldRequest, UUID patronId);
    void cancelHold(UUID waitListId, UUID patronId);
    Set<WaitList> getWaitListsByIdsOrElseThrow(Set<UUID> waitListIds);
    List<WaitListDto> getWaitListsByPatronId(UUID patronId);
    PageableDto<WaitListDto> getWaitListsByBookId(UUID bookId, int currentPage, int pageSize);
    PageableDto<WaitListDto> getAllWaitLists(int currentPage, int pageSize);
    boolean existsWaitListForBookId(UUID bookCopyId);
    List<WaitListDto> getByBookIdAndStatus(UUID bookId, WaitListStatus waitListStatus);
    Optional<WaitList> getByUserIdAndBookIdAndStatus(UUID userId, UUID bookId, WaitListStatus status);
    WaitList saveWaitList(WaitList waitList);
    void deleteByBookId(UUID bookId);
    void deleteByBookCopyId(UUID bookCopyId);
    Optional<WaitList> getTopByBookIdAndStatusOrderByStartDateAsc(UUID bookId, WaitListStatus status);
}
