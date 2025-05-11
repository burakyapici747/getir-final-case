package com.burakyapici.library.service.validation.borrowing;

import com.burakyapici.library.api.advice.UnprocessableEntityException;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.repository.BorrowingRepository;
import org.springframework.stereotype.Component;

@Component
public class BorrowSameBookLimitValidationHandler extends AbstractBorrowValidationHandler {
    private final BorrowingRepository borrowingRepository;
    
    public BorrowSameBookLimitValidationHandler(BorrowingRepository borrowingRepository) {
        this.borrowingRepository = borrowingRepository;
    }
    
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        User patron = request.patron();
        Book book = request.book();
        
        int currentlyBorrowedSameBook = borrowingRepository.countActiveByUserIdAndBookId(patron.getId(), book.getId());
        if(currentlyBorrowedSameBook > 0) {
            throw new UnprocessableEntityException("Patron already has a copy of this book checked out");
        }
    }
} 