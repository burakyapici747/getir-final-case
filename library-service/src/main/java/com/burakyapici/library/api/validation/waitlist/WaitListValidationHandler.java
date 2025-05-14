package com.burakyapici.library.api.validation.waitlist;

public interface WaitListValidationHandler {
    void setNextHandler(WaitListValidationHandler nextHandler);
    void handle(PlaceHoldHandlerRequest request);
} 