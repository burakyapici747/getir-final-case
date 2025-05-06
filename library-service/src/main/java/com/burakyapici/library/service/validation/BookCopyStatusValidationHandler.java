package com.burakyapici.library.service.validation;

import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.exception.BookCopyNotAvailableException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BookCopyStatusValidationHandler extends AbstractBorrowValidationHandler {
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        if(Objects.nonNull(request.bookCopy()) && !BookCopyStatus.AVAILABLE.equals(request.bookCopy().getStatus())){
            throw new BookCopyNotAvailableException(request.bookCopy().getStatus().getDescription());
        }
    }
}
