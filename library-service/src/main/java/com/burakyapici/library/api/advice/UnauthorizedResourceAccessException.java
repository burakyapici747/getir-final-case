package com.burakyapici.library.api.advice;

public class UnauthorizedResourceAccessException extends RuntimeException {
    private static final String MESSAGE = "You are not authorized to access this resource.";

    public UnauthorizedResourceAccessException() {
        super(MESSAGE);
    }

    public UnauthorizedResourceAccessException(String message) {
        super(message);
    }

    public UnauthorizedResourceAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedResourceAccessException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
