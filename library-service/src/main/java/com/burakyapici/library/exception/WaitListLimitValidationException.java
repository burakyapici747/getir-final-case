package com.burakyapici.library.exception;

public class WaitListLimitValidationException extends RuntimeException {
    public WaitListLimitValidationException(String message) {
        super(message);
    }

    public WaitListLimitValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 