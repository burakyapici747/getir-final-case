package com.burakyapici.library.api.validation.returning;

import org.springframework.stereotype.Component;

@Component
public interface ReturnValidationHandler {
    void setNextHandler(ReturnValidationHandler nextHandler);
    void handle(ReturnHandlerRequest request);
}
