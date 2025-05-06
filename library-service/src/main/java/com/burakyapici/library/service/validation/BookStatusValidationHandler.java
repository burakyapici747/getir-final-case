package com.burakyapici.library.service.validation;

import com.burakyapici.library.domain.enums.BookStatus;
import com.burakyapici.library.exception.BookStatusValidationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BookStatusValidationHandler extends AbstractBorrowValidationHandler {
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        if(Objects.nonNull(request.book()) && !BookStatus.ACTIVE.equals(request.book().getBookStatus())){
            throw new BookStatusValidationException(request.book().getBookStatus().getDescription());
        }
    }
}
