package com.burakyapici.library.service;

import com.burakyapici.library.domain.model.WaitList;

import java.util.Set;
import java.util.UUID;

public interface WaitListService {
    Set<WaitList> getWaitListsByIdsOrElseThrow(Set<UUID> waitListIds);
    boolean existsByBookId(UUID bookCopyId);
}
