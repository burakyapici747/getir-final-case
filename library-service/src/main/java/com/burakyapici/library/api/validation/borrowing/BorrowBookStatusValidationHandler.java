package com.burakyapici.library.api.validation.borrowing;

import com.burakyapici.library.api.advice.UnprocessableEntityException;
import com.burakyapici.library.domain.enums.BookStatus;
import org.springframework.stereotype.Component;

@Component
public class BorrowBookStatusValidationHandler extends AbstractBorrowValidationHandler {
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        if(!BookStatus.ACTIVE.equals(request.book().getBookStatus())){
            throw new UnprocessableEntityException("Book is not available for borrowing/returning");
        }
    }
}
