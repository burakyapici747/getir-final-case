package com.burakyapici.library.service.validation;

import com.burakyapici.library.service.validation.borrowing.*;
import com.burakyapici.library.service.validation.returning.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BorrowValidationChainConfig {
    @Bean
    public BorrowValidationHandler borrowValidationChain(
        BorrowPatronStatusValidationHandler borrowPatronStatusValidationHandler,
        BorrowPatronLimitValidationHandler borrowPatronLimitValidationHandler,
        BorrowSameBookLimitValidationHandler borrowSameBookLimitValidationHandler,
        BorrowBookStatusValidationHandler bookStatusHandler,
        BorrowBookCopyStatusValidationHandler bookCopyStatusHandler
    ) {
        borrowPatronStatusValidationHandler.setNextHandler(borrowPatronLimitValidationHandler);
        borrowPatronLimitValidationHandler.setNextHandler(borrowSameBookLimitValidationHandler);
        borrowSameBookLimitValidationHandler.setNextHandler(bookStatusHandler);
        bookStatusHandler.setNextHandler(bookCopyStatusHandler);

        return borrowPatronStatusValidationHandler;
    }

    @Bean
    public ReturnValidationHandler returnValidationChain(
        ReturnStatusValidationHandler returnStatusValidationHandler,
        ReturnBookCopyStatusValidationHandler returnBookCopyStatusHandler,
        ReturnBorrowMatchValidationHandler returnBorrowMatchValidationHandler
    ) {
        returnStatusValidationHandler.setNextHandler(returnBookCopyStatusHandler);
        returnBookCopyStatusHandler.setNextHandler(returnBorrowMatchValidationHandler);

        return returnStatusValidationHandler;
    }
}
