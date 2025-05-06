package com.burakyapici.library.service.validation;

public abstract class AbstractBorrowValidationHandler implements BorrowValidationHandler {
    protected BorrowValidationHandler nextHandler;

    @Override
    public void setNextHandler(BorrowValidationHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    protected void next(BorrowHandlerRequest request) {
        if (nextHandler != null) {
            nextHandler.handle(request);
        }
    }

    @Override
    public void handle(BorrowHandlerRequest request) {
        performValidation(request);
        next(request);
    }

    protected abstract void performValidation(BorrowHandlerRequest request);
}