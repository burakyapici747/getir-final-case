package com.burakyapici.library.service.validation;

import com.burakyapici.library.service.validation.borrowing.AbstractBorrowValidationHandler;
import com.burakyapici.library.service.validation.borrowing.BorrowHandlerRequest;
import org.springframework.stereotype.Component;

@Component
public class WaitListStatusValidationHandler extends AbstractBorrowValidationHandler {
    @Override
    protected void performValidation(BorrowHandlerRequest request) {

    }
//    @Override
//    protected void performValidation(BorrowHandlerRequest request) {
//        request.waitListOptional().orElseThrow(
//            () -> new IllegalArgumentException("WaitList status is required for this operation.")
//        );
//    }
}
