package com.burakyapici.library.api.validation.returning;

import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.api.advice.UnprocessableEntityException;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.Borrowing;
import com.burakyapici.library.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ReturnBorrowMatchValidationHandler extends AbstractReturnValidationHandler {
    @Override
    protected void performValidation(ReturnHandlerRequest request) {
        if(Objects.isNull(request.borrowing())){
            throw new EntityNotFoundException("Active borrowing not found.");
        }

        BookCopy bookCopy = request.bookCopy();
        Borrowing borrowing = request.borrowing();
        User patron = request.patron();

        if(!bookCopy.getId().equals(borrowing.getBookCopy().getId())){
            throw new UnprocessableEntityException("Book copy does not match with borrowing.");
        }

        if(!borrowing.getUser().getId().equals(patron.getId())){
            throw new UnprocessableEntityException("Patron does not match with borrowing.");
        }
    }
} 