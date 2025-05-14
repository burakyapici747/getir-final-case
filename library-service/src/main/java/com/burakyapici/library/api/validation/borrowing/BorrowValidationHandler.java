package com.burakyapici.library.api.validation.borrowing;

public interface BorrowValidationHandler {
    void setNextHandler(BorrowValidationHandler nextHandler);
    void handle(BorrowHandlerRequest request);
}
