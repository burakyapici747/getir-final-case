package com.burakyapici.library.service.validation;

import com.burakyapici.library.service.validation.waitlist.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WaitListValidationChainConfig {
    @Bean
    public WaitListValidationHandler waitListValidationChain(
        PatronStatusValidationHandler patronStatusValidationHandler,
        BookStatusValidationHandler bookStatusValidationHandler,
        ExistingWaitListValidationHandler existingWaitListValidationHandler,
        AvailableCopyValidationHandler availableCopyValidationHandler,
        WaitListLimitValidationHandler waitListLimitValidationHandler
    ) {
        patronStatusValidationHandler.setNextHandler(bookStatusValidationHandler);
        bookStatusValidationHandler.setNextHandler(existingWaitListValidationHandler);
        existingWaitListValidationHandler.setNextHandler(availableCopyValidationHandler);
        availableCopyValidationHandler.setNextHandler(waitListLimitValidationHandler);

        return patronStatusValidationHandler;
    }
    
    @Bean
    public CancelHoldValidationHandler cancelHoldValidationChain(
        WaitListOwnerCancelValidationHandler waitListOwnerCancelValidationHandler,
        WaitListStatusCancelValidationHandler waitListStatusCancelValidationHandler
    ) {
        waitListOwnerCancelValidationHandler.setNextHandler(waitListStatusCancelValidationHandler);
        
        return waitListOwnerCancelValidationHandler;
    }
} 