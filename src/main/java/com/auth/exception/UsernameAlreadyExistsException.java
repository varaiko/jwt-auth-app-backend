package com.auth.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends ApiException {

    public UsernameAlreadyExistsException() {
        super(HttpStatus.CONFLICT, "User with such username already exists");
    }
}
