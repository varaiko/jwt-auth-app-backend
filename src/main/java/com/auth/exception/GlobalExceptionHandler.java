package com.auth.exception;

import com.auth.dto.request.ExceptionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Resource not found exception handling
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleResourceNotFoundException (ResourceNotFoundException exception, WebRequest webRequest) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
            .message(exception.getMessage())
            .details(webRequest.getDescription(false))
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
            .build();
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }

    // API call exceptions
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionDto> handleApiException (ApiException apiException, WebRequest webRequest) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .message(apiException.getMessage())
                .details(webRequest.getDescription(false))
                .timestamp(LocalDateTime.now())
                .status(apiException.getStatus().value())
                .error(apiException.getStatus().getReasonPhrase())
                .build();
        return new ResponseEntity<>(exceptionDto, apiException.getStatus());
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleAuthenticationException(AuthenticationCredentialsNotFoundException ex, WebRequest webRequest) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .message(ex.getMessage())
                .details(webRequest.getDescription(false))
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .build();

        return new ResponseEntity<>(exceptionDto, HttpStatus.UNAUTHORIZED);
    }

    // Username not found exception
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleUsernameNotFound(UsernameNotFoundException ex, WebRequest request) {
        ExceptionDto dto = ExceptionDto.builder()
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .build();
        return new ResponseEntity<>(dto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ExceptionDto> handleForbiddenOperationException(ForbiddenOperationException ex, WebRequest request) {
        ExceptionDto dto = ExceptionDto.builder()
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .timestamp(LocalDateTime.now())
                .status(ex.getStatus().value())
                .error(ex.getStatus().getReasonPhrase())
                .build();
        return new ResponseEntity<>(dto, ex.getStatus());
    }

    // Global exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleAllExceptions(Exception ex, WebRequest request) {
        ExceptionDto dto = ExceptionDto.builder()
                .message("An unexpected error occurred.")
                .details(request.getDescription(false))
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .build();
        return new ResponseEntity<>(dto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
