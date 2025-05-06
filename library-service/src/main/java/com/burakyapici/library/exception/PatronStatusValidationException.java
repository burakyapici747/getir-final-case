package com.burakyapici.library.exception;

public class PatronStatusValidationException extends RuntimeException {
    public PatronStatusValidationException(String message) {
        super(message);
    }
    public PatronStatusValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
