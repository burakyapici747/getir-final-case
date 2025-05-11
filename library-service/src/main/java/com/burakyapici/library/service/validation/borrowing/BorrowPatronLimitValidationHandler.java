package com.burakyapici.library.service.validation.borrowing;

import com.burakyapici.library.api.advice.UnprocessableEntityException;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.repository.BorrowingRepository;
import com.burakyapici.library.service.LibraryRulesService;
import org.springframework.stereotype.Component;

@Component
public class BorrowPatronLimitValidationHandler extends AbstractBorrowValidationHandler {
    private final BorrowingRepository borrowingRepository;
    private final LibraryRulesService rulesService;
    
    public BorrowPatronLimitValidationHandler(
        BorrowingRepository borrowingRepository,
        LibraryRulesService rulesService
    ) {
        this.borrowingRepository = borrowingRepository;
        this.rulesService = rulesService;
    }
    
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        User patron = request.patron();
        
        int currentlyBorrowed = borrowingRepository.countActiveByUserId(patron.getId());
        if(!rulesService.canPatronBorrowMoreBooks(currentlyBorrowed)) {
            throw new UnprocessableEntityException("Patron has reached maximum borrowing limit");
        }
    }
} 