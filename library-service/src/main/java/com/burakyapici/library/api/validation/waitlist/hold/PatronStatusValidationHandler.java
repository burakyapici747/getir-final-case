package com.burakyapici.library.api.validation.waitlist.hold;

import com.burakyapici.library.domain.enums.PatronStatus;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.exception.PatronStatusValidationException;
import com.burakyapici.library.api.validation.waitlist.AbstractWaitListValidationHandler;
import com.burakyapici.library.api.validation.waitlist.PlaceHoldHandlerRequest;
import org.springframework.stereotype.Component;

@Component
public class PatronStatusValidationHandler extends AbstractWaitListValidationHandler {
    @Override
    protected void performValidation(PlaceHoldHandlerRequest request) {
        User patron = request.patron();

        if (patron.getPatronStatus() == null || !PatronStatus.ACTIVE.equals(patron.getPatronStatus())) {
            throw new PatronStatusValidationException("Patron status is not active");
        }
    }
} 