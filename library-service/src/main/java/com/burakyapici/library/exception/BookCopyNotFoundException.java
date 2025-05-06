package com.burakyapici.library.exception;

public class BookCopyNotFoundException extends RuntimeException {
    public BookCopyNotFoundException(String message) {
        super(message);
    }

    public BookCopyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
