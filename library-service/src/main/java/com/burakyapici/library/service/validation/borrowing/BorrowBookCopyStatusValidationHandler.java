package com.burakyapici.library.service.validation.borrowing;

import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.model.WaitList;
import com.burakyapici.library.exception.BookCopyNotAvailableException;
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
                throw new IllegalStateException("Book copy is reserved for another user.");
            }

            return;
        }else{
            throw new BookCopyNotAvailableException(bookCopy.getStatus().getDescription());
        }
    }
}
