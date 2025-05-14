package com.burakyapici.library.api.validation.returning;

import com.burakyapici.library.api.advice.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ReturnStatusValidationHandler extends AbstractReturnValidationHandler {
    @Override
    protected void performValidation(ReturnHandlerRequest request) {
        if(Objects.isNull(request.borrowing())){
            throw new EntityNotFoundException("Borrowing not found");
        }
    }
}
