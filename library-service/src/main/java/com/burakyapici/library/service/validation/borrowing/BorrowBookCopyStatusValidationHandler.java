package com.burakyapici.library.service.validation.borrowing;

import com.burakyapici.library.api.advice.DataConflictException;
import com.burakyapici.library.api.advice.UnprocessableEntityException;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.model.WaitList;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BorrowBookCopyStatusValidationHandler extends AbstractBorrowValidationHandler {
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        BookCopy bookCopy = request.bookCopy();
        User patron = request.patron();

        if(BookCopyStatus.AVAILABLE.equals(bookCopy.getStatus())){
            return;
        }else if(BookCopyStatus.ON_HOLD.equals(bookCopy.getStatus())){
            WaitList reservedForWaitList = request.waitList();

            if(Objects.isNull(reservedForWaitList)){
                throw new DataConflictException("Book copy is reserved for another user.");
            }

            return;
        }else{
            throw new UnprocessableEntityException(bookCopy.getStatus().getDescription());
        }
    }
}
