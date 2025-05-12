package com.burakyapici.library.service.validation.waitlist;

import com.burakyapici.library.api.advice.UnauthorizedResourceAccessException;
import com.burakyapici.library.domain.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class WaitListOwnerCancelValidationHandler extends AbstractCancelHoldValidationHandler {
    
    @Override
    protected void performValidation(CancelHoldHandlerRequest request) {
        if (!request.patron().getId().equals(request.waitList().getUser().getId()) && 
            Role.PATRON.equals(request.patron().getRole())) {
            throw new UnauthorizedResourceAccessException("You cannot cancel a wait list that does not belong to you.");
        }
    }
} 