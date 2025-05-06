package com.burakyapici.library.exception;

public class WaitListNotFoundException extends RuntimeException {
    public WaitListNotFoundException(String message) {
        super(message);
    }

    public WaitListNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
