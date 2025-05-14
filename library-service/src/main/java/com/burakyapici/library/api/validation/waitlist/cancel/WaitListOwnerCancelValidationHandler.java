package com.burakyapici.library.api.validation.waitlist.cancel;

import com.burakyapici.library.api.advice.ForbiddenAccessException;
import com.burakyapici.library.domain.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class WaitListOwnerCancelValidationHandler extends AbstractCancelHoldValidationHandler {
    
    @Override
    protected void performValidation(CancelHoldHandlerRequest request) {
        if (!request.patron().getId().equals(request.waitList().getUser().getId()) && 
            Role.PATRON.equals(request.patron().getRole())) {
            throw new ForbiddenAccessException("You cannot cancel a wait list that does not belong to you.");
        }
    }
} 