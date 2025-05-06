package com.burakyapici.library.service.impl;

import com.burakyapici.library.domain.model.WaitList;
import com.burakyapici.library.domain.repository.WaitListRepository;
import com.burakyapici.library.exception.WaitListNotFoundException;
import com.burakyapici.library.service.WaitListService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WaitListServiceImpl implements WaitListService {
    private final WaitListRepository waitListRepository;

    public WaitListServiceImpl(WaitListRepository waitListRepository) {
        this.waitListRepository = waitListRepository;
    }

    @Override
    public Set<WaitList> getWaitListsByIdsOrElseThrow(Set<UUID> waitListIds) {
        List<WaitList> waitLists = waitListRepository.findAllById(waitListIds);

        if (waitLists.size() != waitListIds.size()) {
            Set<UUID> foundWaitListIds = waitLists.stream()
                    .map(WaitList::getId)
                    .collect(Collectors.toSet());

            Set<UUID> missingWaitListIds = waitListIds.stream()
                    .filter(id -> !foundWaitListIds.contains(id))
                    .collect(Collectors.toSet());

            throw new WaitListNotFoundException(
                "The following wait list IDs could not be found: " +
                    missingWaitListIds.stream()
                        .map(UUID::toString)
                        .collect(Collectors.joining(", "))
            );
        }

        return new HashSet<>(waitLists);
    }

    @Override
    public boolean existsByBookId(UUID bookCopyId) {
        return waitListRepository.existsByBookId(bookCopyId);
    }
}
