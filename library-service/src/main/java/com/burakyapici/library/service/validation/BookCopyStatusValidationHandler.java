package com.burakyapici.library.service.validation;

import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.model.WaitList;
import com.burakyapici.library.exception.BookCopyNotAvailableException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BookCopyStatusValidationHandler extends AbstractBorrowValidationHandler {
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        if(Objects.isNull(request.bookCopy())){
            throw new IllegalStateException("BorrowHandlerRequest must contain a BookCopy.");
        }

        if(Objects.isNull(request.patron())){
            throw new IllegalStateException("BorrowHandlerRequest must contain a Patron user.");
        }

        BookCopy bookCopy = request.bookCopy();
        User patron = request.patron();

        if(BookCopyStatus.AVAILABLE.equals(bookCopy.getStatus())){
            return;
        }else if(BookCopyStatus.ON_HOLD.equals(bookCopy.getStatus())){

            WaitList reservedForWaitList = bookCopy.getReservedForWaitList();

            if(Objects.isNull(reservedForWaitList)){
                throw new IllegalStateException("Book copy status is ON_HOLD but it is not linked to any wait list reservation.");
            }

            if(!reservedForWaitList.getUser().getId().equals(patron.getId())){
                throw new IllegalStateException("Book copy is reserved for another user.");
            }

            return;
        }else{
            throw new BookCopyNotAvailableException(bookCopy.getStatus().getDescription());
        }
    }
}
