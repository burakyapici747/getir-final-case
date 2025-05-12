package com.burakyapici.library.service.validation.waitlist;

import com.burakyapici.library.api.advice.DataConflictException;
import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.repository.WaitListRepository;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ExistingWaitListValidationHandler extends AbstractWaitListValidationHandler {
    private final WaitListRepository waitListRepository;
    
    public ExistingWaitListValidationHandler(WaitListRepository waitListRepository) {
        this.waitListRepository = waitListRepository;
    }
    
    @Override
    protected void performValidation(PlaceHoldHandlerRequest request) {
        waitListRepository.findWaitListByBookIdAndUserIdAndWaitListStatusIn(
            request.bookId(),
            request.patron().getId(),
            Set.of(WaitListStatus.WAITING, WaitListStatus.READY_FOR_PICKUP)
        ).ifPresent(waitList -> {
            throw new DataConflictException("There is already a wait list for this book.");
        });
    }
} 