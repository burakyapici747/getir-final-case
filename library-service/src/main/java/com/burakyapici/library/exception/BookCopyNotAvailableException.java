package com.burakyapici.library.exception;

public class BookCopyNotAvailableException extends RuntimeException {
    public BookCopyNotAvailableException(String message) {
        super(message);
    }

    public BookCopyNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
