package com.burakyapici.library.api.validation.waitlist.cancel;

public interface CancelHoldValidationHandler {
    void setNextHandler(CancelHoldValidationHandler nextHandler);
    void handle(CancelHoldHandlerRequest request);
} 