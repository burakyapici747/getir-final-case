package com.burakyapici.library.service.validation.borrowing;

import com.burakyapici.library.domain.enums.BookStatus;
import com.burakyapici.library.exception.BookStatusValidationException;
import org.springframework.stereotype.Component;

@Component
public class BorrowBookStatusValidationHandler extends AbstractBorrowValidationHandler {
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        if(!BookStatus.ACTIVE.equals(request.book().getBookStatus())){
            throw new BookStatusValidationException("Book is not available for borrowing/returning");
        }
    }
}
