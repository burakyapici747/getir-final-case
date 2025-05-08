package com.burakyapici.library.service.validation;

import org.springframework.stereotype.Component;

@Component
public class WaitListStatusValidationHandler extends AbstractBorrowValidationHandler {
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        request.waitListOptional().orElseThrow(
            () -> new IllegalArgumentException("WaitList status is required for this operation.")
        );
    }
}
