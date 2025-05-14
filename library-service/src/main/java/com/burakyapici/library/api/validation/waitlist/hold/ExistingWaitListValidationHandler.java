package com.burakyapici.library.api.validation.waitlist.hold;

import com.burakyapici.library.api.advice.DataConflictException;
import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.repository.WaitListRepository;
import com.burakyapici.library.api.validation.waitlist.AbstractWaitListValidationHandler;
import com.burakyapici.library.api.validation.waitlist.PlaceHoldHandlerRequest;
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
            Set.of(WaitListStatus.WAITING.name(), WaitListStatus.READY_FOR_PICKUP.name())
        ).ifPresent(waitList -> {
            throw new DataConflictException("There is already a wait list for this book.");
        });
    }
} 