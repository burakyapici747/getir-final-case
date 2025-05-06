package com.burakyapici.library.service.validation;

import com.burakyapici.library.domain.enums.PatronStatus;
import com.burakyapici.library.exception.PatronStatusValidationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PatronStatusValidationHandler extends AbstractBorrowValidationHandler {
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        if(Objects.nonNull(request.user()) && !PatronStatus.ACTIVE.equals(request.user().getPatronStatus())){
            throw new PatronStatusValidationException(request.user().getPatronStatus().getDescription());
        }
    }
}
