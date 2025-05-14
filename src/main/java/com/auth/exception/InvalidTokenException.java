package com.auth.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends ApiException {

    public InvalidTokenException() {
        super(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
    }

    public InvalidTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
