package com.burakyapici.library.api.validation.validator;

import com.burakyapici.library.api.dto.request.RegisterRequest;
import com.burakyapici.library.api.validation.annotation.PasswordMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, RegisterRequest> {

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
        if (request.password() == null || request.passwordConfirmation() == null) {
            return false;
        }

        boolean isValid = request.password().equals(request.passwordConfirmation());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("passwordConfirmation")
                .addConstraintViolation();
        }

        return isValid;
    }
}