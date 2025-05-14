package com.burakyapici.library.api.validation.waitlist.hold;

import com.burakyapici.library.domain.enums.BookStatus;
import com.burakyapici.library.exception.BookStatusValidationException;
import com.burakyapici.library.api.validation.waitlist.AbstractWaitListValidationHandler;
import com.burakyapici.library.api.validation.waitlist.PlaceHoldHandlerRequest;
import org.springframework.stereotype.Component;

@Component
public class BookStatusValidationHandler extends AbstractWaitListValidationHandler {
    @Override
    protected void performValidation(PlaceHoldHandlerRequest request) {
        if (!BookStatus.ACTIVE.equals(request.book().getBookStatus())) {
            throw new BookStatusValidationException("Book is not active and cannot be placed on hold");
        }
    }
} 