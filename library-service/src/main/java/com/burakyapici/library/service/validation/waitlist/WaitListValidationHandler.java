package com.burakyapici.library.service.validation.waitlist;

public interface WaitListValidationHandler {
    void setNextHandler(WaitListValidationHandler nextHandler);
    void handle(PlaceHoldHandlerRequest request);
} 