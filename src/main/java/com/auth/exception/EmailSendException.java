package com.auth.exception;

import org.springframework.http.HttpStatus;

public class EmailSendException extends ApiException {
    public EmailSendException(HttpStatus status, String message) {
        super(status, message);
    }
}
