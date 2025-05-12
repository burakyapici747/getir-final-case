package com.burakyapici.library.service.validation.waitlist.cancel;

import com.burakyapici.library.api.advice.UnprocessableEntityException;
import com.burakyapici.library.domain.enums.WaitListStatus;
import org.springframework.stereotype.Component;

@Component
public class WaitListStatusCancelValidationHandler extends AbstractCancelHoldValidationHandler {
    
    @Override
    protected void performValidation(CancelHoldHandlerRequest request) {
        if (!(WaitListStatus.WAITING.equals(request.waitList().getStatus()) || 
              WaitListStatus.READY_FOR_PICKUP.equals(request.waitList().getStatus()))) {
            throw new UnprocessableEntityException("You can only cancel a wait list that is in WAITING or READY_FOR_PICKUP status.");
        }
    }
} 