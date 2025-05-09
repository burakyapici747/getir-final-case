package com.burakyapici.library.service.validation.borrowing;

public interface BorrowValidationHandler {
    void setNextHandler(BorrowValidationHandler nextHandler);
    void handle(BorrowHandlerRequest request);
}
