package com.burakyapici.library.service.validation;

public interface BorrowValidationHandler {
    void setNextHandler(BorrowValidationHandler nextHandler);
    void handle(BorrowHandlerRequest request);
}
