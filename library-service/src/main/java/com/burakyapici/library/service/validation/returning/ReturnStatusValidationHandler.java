package com.burakyapici.library.service.validation.returning;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ReturnStatusValidationHandler extends AbstractReturnValidationHandler {
    @Override
    protected void performValidation(ReturnHandlerRequest request) {
        if(Objects.isNull(request.borrowing())){
            throw new IllegalStateException("BorrowHandlerRequest must contain a return.");
        }
    }
}
