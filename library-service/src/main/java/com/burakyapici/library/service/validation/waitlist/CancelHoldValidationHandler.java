package com.burakyapici.library.service.validation.waitlist;

public interface CancelHoldValidationHandler {
    void setNextHandler(CancelHoldValidationHandler nextHandler);
    void handle(CancelHoldHandlerRequest request);
} 