package com.burakyapici.library.api.advice;

public class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }

    public DataConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataConflictException(Throwable cause) {
        super(cause);
    }
}
