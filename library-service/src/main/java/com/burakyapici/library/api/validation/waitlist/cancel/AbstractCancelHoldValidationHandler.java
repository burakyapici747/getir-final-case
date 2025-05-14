package com.burakyapici.library.api.validation.waitlist.cancel;

public abstract class AbstractCancelHoldValidationHandler implements CancelHoldValidationHandler {
    protected CancelHoldValidationHandler nextHandler;

    @Override
    public void setNextHandler(CancelHoldValidationHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    protected void next(CancelHoldHandlerRequest request) {
        if (nextHandler != null) {
            nextHandler.handle(request);
        }
    }

    @Override
    public void handle(CancelHoldHandlerRequest request) {
        performValidation(request);
        next(request);
    }

    protected abstract void performValidation(CancelHoldHandlerRequest request);
} 