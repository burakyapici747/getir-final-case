package com.burakyapici.library.service.validation.waitlist.cancel;

public interface CancelHoldValidationHandler {
    void setNextHandler(CancelHoldValidationHandler nextHandler);
    void handle(CancelHoldHandlerRequest request);
} 