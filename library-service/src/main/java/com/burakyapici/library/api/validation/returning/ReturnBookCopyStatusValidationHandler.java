package com.burakyapici.library.api.validation.returning;

import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.api.advice.UnprocessableEntityException;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.model.BookCopy;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ReturnBookCopyStatusValidationHandler extends AbstractReturnValidationHandler {
    @Override
    protected void performValidation(ReturnHandlerRequest request) {
        if(Objects.isNull(request.borrowing())){
            throw new EntityNotFoundException("Active borrowing not found.");
        }

        BookCopy bookCopy = request.bookCopy();

        if(!BookCopyStatus.CHECKED_OUT.equals(bookCopy.getStatus())){
            throw new UnprocessableEntityException("Book copy is not checked out.");
        }
    }
}
