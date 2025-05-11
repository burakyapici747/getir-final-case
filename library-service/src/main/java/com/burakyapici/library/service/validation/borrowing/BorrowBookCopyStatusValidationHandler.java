package com.burakyapici.library.service.validation.borrowing;

import com.burakyapici.library.api.advice.DataConflictException;
import com.burakyapici.library.api.advice.UnprocessableEntityException;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.model.BookCopy;
import org.springframework.stereotype.Component;

@Component
public class BorrowBookCopyStatusValidationHandler extends AbstractBorrowValidationHandler {
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        BookCopy bookCopy = request.bookCopy();

        if(BookCopyStatus.AVAILABLE.equals(bookCopy.getStatus())){
            return;
        }else if(BookCopyStatus.ON_HOLD.equals(bookCopy.getStatus())){
            request.waitList().orElseThrow(
                () -> new DataConflictException("Book copy is reserved for another patron.")
            );

            return;
        }else{
            throw new UnprocessableEntityException(bookCopy.getStatus().getDescription());
        }
    }
}
