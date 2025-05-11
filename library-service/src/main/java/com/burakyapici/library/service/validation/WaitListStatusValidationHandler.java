package com.burakyapici.library.service.validation;

import com.burakyapici.library.service.validation.borrowing.AbstractBorrowValidationHandler;
import com.burakyapici.library.service.validation.borrowing.BorrowHandlerRequest;
import org.springframework.stereotype.Component;

@Component
public class WaitListStatusValidationHandler extends AbstractBorrowValidationHandler {
    @Override
    protected void performValidation(BorrowHandlerRequest request) {
        // Bu validasyon, kitabın bekleme listesinde olup olmadığını kontrol eder
        // Eğer kitap bekleme listesindeyse ve belirli bir kullanıcı için ayrılmışsa,
        // bu kullanıcının o kitabı ödünç alabilmesi için bekleme listesinde kaydı olmalıdır
        request.waitList().orElseThrow(
            () -> new IllegalArgumentException("WaitList status is required for this operation.")
        );
    }
}
