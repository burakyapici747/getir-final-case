package com.burakyapici.library.service.validation.borrowing;

import com.burakyapici.library.domain.enums.PatronStatus;
import com.burakyapici.library.exception.PatronStatusValidationException;
import org.springframework.stereotype.Component;

@Component
public class BorrowPatronStatusValidationHandler extends AbstractBorrowValidationHandler {
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        if(!PatronStatus.ACTIVE.equals(request.patron().getPatronStatus())){
            throw new PatronStatusValidationException(request.patron().getPatronStatus().getDescription());
        }
    }
}
