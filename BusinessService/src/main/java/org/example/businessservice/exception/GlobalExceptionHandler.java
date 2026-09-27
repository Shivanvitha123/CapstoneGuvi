package org.example.businessservice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            BusinessProfileAlreadyExistsException.class
    )
    public ApiErrorResponse handleAlreadyExists(
            BusinessProfileAlreadyExistsException ex
    ) {

        return new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(
            BusinessProfileNotFoundException.class
    )
    public ApiErrorResponse handleNotFound(
            BusinessProfileNotFoundException ex
    ) {

        return new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(
            BusinessAccessDeniedException.class
    )
    public ApiErrorResponse handleAccessDenied(
            BusinessAccessDeniedException ex
    ) {

        return new ApiErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(
            WebExchangeBindException.class
    )
    public ApiErrorResponse handleValidation(
            WebExchangeBindException ex
    ) {

        String message =
                ex.getFieldErrors()
                        .stream()
                        .map(error ->
                                error.getField()
                                        + ": "
                                        + error.getDefaultMessage()
                        )
                        .collect(Collectors.joining(", "));

        return new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(Exception.class)
    public ApiErrorResponse handleGeneric(
            Exception ex
    ) {

        return new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                LocalDateTime.now()
        );
    }

    public record ApiErrorResponse(
            int status,
            String error,
            String message,
            LocalDateTime timestamp
    ) {
    }
}

