package com.burakyapici.library.service.validation;

import com.burakyapici.library.domain.enums.PatronStatus;
import com.burakyapici.library.exception.PatronStatusValidationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PatronStatusValidationHandler extends AbstractBorrowValidationHandler {
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        if(Objects.isNull(request.patron())){
            throw new IllegalStateException("BorrowHandlerRequest must contain a Patron user.");
        }

        if(!PatronStatus.ACTIVE.equals(request.patron().getPatronStatus())){
            throw new PatronStatusValidationException(request.patron().getPatronStatus().getDescription());
        }
    }
}
