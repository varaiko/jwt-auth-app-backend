package com.auth.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenOperationException extends ApiException {
    public ForbiddenOperationException(HttpStatus status, String message) {
        super(status, message);
    }
}
