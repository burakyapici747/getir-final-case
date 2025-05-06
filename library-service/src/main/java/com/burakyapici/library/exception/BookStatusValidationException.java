package com.burakyapici.library.exception;

public class BookStatusValidationException extends RuntimeException {
    public BookStatusValidationException(String message) {
        super(message);
    }

    public BookStatusValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
