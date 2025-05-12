package com.burakyapici.library.service.validation.waitlist;

import com.burakyapici.library.api.advice.UnprocessableEntityException;
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