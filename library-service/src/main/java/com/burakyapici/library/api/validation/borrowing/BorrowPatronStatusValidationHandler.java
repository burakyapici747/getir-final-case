package com.burakyapici.library.api.validation.borrowing;

import com.burakyapici.library.api.advice.ForbiddenAccessException;
import com.burakyapici.library.domain.enums.PatronStatus;
import org.springframework.stereotype.Component;

@Component
public class BorrowPatronStatusValidationHandler extends AbstractBorrowValidationHandler {
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        if(!PatronStatus.ACTIVE.equals(request.patron().getPatronStatus())){
            throw new ForbiddenAccessException(request.patron().getPatronStatus().getDescription());
        }
    }
}
