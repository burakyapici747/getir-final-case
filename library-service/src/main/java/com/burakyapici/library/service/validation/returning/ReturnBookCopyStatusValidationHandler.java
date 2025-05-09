package com.burakyapici.library.service.validation.returning;

import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.model.BookCopy;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ReturnBookCopyStatusValidationHandler extends AbstractReturnValidationHandler {
    @Override
    protected void performValidation(ReturnHandlerRequest request) {
        if(Objects.isNull(request.borrowing())){
            throw new IllegalStateException("ActiveBorrowingNotFoundException");
        }

        BookCopy bookCopy = request.bookCopy();

        if(!BookCopyStatus.CHECKED_OUT.equals(bookCopy.getStatus())){
            throw new IllegalStateException("InvalidBookCopyStatusForReturnException");
        }
    }
}
