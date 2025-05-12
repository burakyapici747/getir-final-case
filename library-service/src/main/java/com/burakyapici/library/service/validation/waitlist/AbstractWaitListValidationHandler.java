package com.burakyapici.library.service.validation.waitlist;

public abstract class AbstractWaitListValidationHandler implements WaitListValidationHandler {
    protected WaitListValidationHandler nextHandler;

    @Override
    public void setNextHandler(WaitListValidationHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    protected void next(PlaceHoldHandlerRequest request) {
        if (nextHandler != null) {
            nextHandler.handle(request);
        }
    }

    @Override
    public void handle(PlaceHoldHandlerRequest request) {
        performValidation(request);
        next(request);
    }

    protected abstract void performValidation(PlaceHoldHandlerRequest request);
} 