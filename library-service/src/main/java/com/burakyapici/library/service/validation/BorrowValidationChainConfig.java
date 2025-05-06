package com.burakyapici.library.service.validation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BorrowValidationChainConfig {
    @Bean
    public BorrowValidationHandler borrowValidationChain(
        BookStatusValidationHandler bookStatusHandler,
        BookCopyStatusValidationHandler bookCopyStatusHandler,
        PatronStatusValidationHandler patronStatusValidationHandler
    ) {
        bookStatusHandler.setNextHandler(bookCopyStatusHandler);
        bookCopyStatusHandler.setNextHandler(patronStatusValidationHandler);
        patronStatusValidationHandler.setNextHandler(patronStatusValidationHandler);
        return bookStatusHandler;
    }
}
