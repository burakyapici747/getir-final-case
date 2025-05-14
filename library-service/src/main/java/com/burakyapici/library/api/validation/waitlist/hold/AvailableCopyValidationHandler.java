package com.burakyapici.library.api.validation.waitlist.hold;

import com.burakyapici.library.api.advice.UnprocessableEntityException;
import com.burakyapici.library.api.validation.waitlist.AbstractWaitListValidationHandler;
import com.burakyapici.library.api.validation.waitlist.PlaceHoldHandlerRequest;
import org.springframework.stereotype.Component;

@Component
public class AvailableCopyValidationHandler extends AbstractWaitListValidationHandler {
    @Override
    protected void performValidation(PlaceHoldHandlerRequest request) {
        if (!request.availableCopies().isEmpty()) {
            throw new UnprocessableEntityException("There are available copies of this book. No need to place a hold.");
        }
    }
} 