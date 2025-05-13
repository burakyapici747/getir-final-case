package com.burakyapici.library.service.validation.waitlist.hold;

import com.burakyapici.library.api.advice.UnprocessableEntityException;
import com.burakyapici.library.config.LibraryRulesConfig;
import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.domain.model.WaitList;
import com.burakyapici.library.domain.repository.WaitListRepository;
import com.burakyapici.library.service.validation.waitlist.AbstractWaitListValidationHandler;
import com.burakyapici.library.service.validation.waitlist.PlaceHoldHandlerRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WaitListLimitValidationHandler extends AbstractWaitListValidationHandler {
    private final WaitListRepository waitListRepository;
    private final LibraryRulesConfig libraryRulesConfig;
    
    public WaitListLimitValidationHandler(WaitListRepository waitListRepository, LibraryRulesConfig libraryRulesConfig) {
        this.waitListRepository = waitListRepository;
        this.libraryRulesConfig = libraryRulesConfig;
    }
    
    @Override
    protected void performValidation(PlaceHoldHandlerRequest request) {
        List<WaitList> waitLists = waitListRepository.findByUser_IdAndStatusIn(
            request.patron().getId(),
            List.of(WaitListStatus.WAITING, WaitListStatus.READY_FOR_PICKUP)
        );

        if (waitLists.size() >= libraryRulesConfig.getMaxReservationsPerPatron()) {
            throw new UnprocessableEntityException(
                "Patron has reached the maximum number of allowed holds (" +
                libraryRulesConfig.getMaxReservationsPerPatron() + ")"
            );
        }
    }
} 