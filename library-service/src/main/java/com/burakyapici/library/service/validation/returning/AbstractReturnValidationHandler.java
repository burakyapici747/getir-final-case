package com.burakyapici.library.service.validation.returning;

public abstract class AbstractReturnValidationHandler implements ReturnValidationHandler {
    protected ReturnValidationHandler nextHandler;

    @Override
    public void setNextHandler(ReturnValidationHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    protected void next(ReturnHandlerRequest request) {
        if (nextHandler != null) {
            nextHandler.handle(request);
        }
    }

    @Override
    public void handle(ReturnHandlerRequest request) {
        performValidation(request);
        next(request);
    }

    protected abstract void performValidation(ReturnHandlerRequest request);
}