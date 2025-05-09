package com.burakyapici.library.service.validation;

import com.burakyapici.library.service.validation.borrowing.BorrowBookCopyStatusValidationHandler;
import com.burakyapici.library.service.validation.borrowing.BorrowBookStatusValidationHandler;
import com.burakyapici.library.service.validation.borrowing.BorrowPatronStatusValidationHandler;
import com.burakyapici.library.service.validation.borrowing.BorrowValidationHandler;
import com.burakyapici.library.service.validation.returning.ReturnBookCopyStatusValidationHandler;
import com.burakyapici.library.service.validation.returning.ReturnStatusValidationHandler;
import com.burakyapici.library.service.validation.returning.ReturnValidationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BorrowValidationChainConfig {
    @Bean
    public BorrowValidationHandler borrowValidationChain(
        BorrowPatronStatusValidationHandler borrowPatronStatusValidationHandler,
        BorrowBookStatusValidationHandler bookStatusHandler,
        BorrowBookCopyStatusValidationHandler bookCopyStatusHandler
    ) {
        borrowPatronStatusValidationHandler.setNextHandler(borrowPatronStatusValidationHandler);
        bookStatusHandler.setNextHandler(bookCopyStatusHandler);
        bookCopyStatusHandler.setNextHandler(borrowPatronStatusValidationHandler);

        return bookStatusHandler;
    }

    @Bean
    public ReturnValidationHandler returnValidationChain(
        ReturnStatusValidationHandler returnStatusValidationHandler,
        ReturnBookCopyStatusValidationHandler returnBookCopyStatusHandler
    ) {
        returnStatusValidationHandler.setNextHandler(returnStatusValidationHandler);
        returnBookCopyStatusHandler.setNextHandler(returnBookCopyStatusHandler);

        return returnStatusValidationHandler;
    }
}
