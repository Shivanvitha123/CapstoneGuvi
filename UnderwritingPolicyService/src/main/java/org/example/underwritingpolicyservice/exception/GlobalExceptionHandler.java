package org.example.underwritingpolicyservice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PolicyNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            PolicyNotFoundException ex) {

        return build(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(PolicyAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(
            PolicyAccessDeniedException ex) {

        return build(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
    }

    @ExceptionHandler(PolicyAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(
            PolicyAlreadyExistsException ex) {

        return build(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            WebExchangeBindException ex) {

        Map<String, String> errors =
                new LinkedHashMap<>();

        ex.getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put(
                "status",
                HttpStatus.BAD_REQUEST.value()
        );

        body.put(
                "error",
                HttpStatus.BAD_REQUEST.getReasonPhrase()
        );

        body.put(
                "message",
                "Validation failed"
        );

        body.put(
                "errors",
                errors
        );

        body.put(
                "timestamp",
                LocalDateTime.now()
        );

        return ResponseEntity
                .badRequest()
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(
            Exception ex) {

        ex.printStackTrace();

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
    }

    private ResponseEntity<Map<String, Object>> build(
            HttpStatus status,
            String message) {

        Map<String, Object> body =
                new LinkedHashMap<>();

        body.put(
                "status",
                status.value()
        );

        body.put(
                "error",
                status.getReasonPhrase()
        );

        body.put(
                "message",
                message
        );

        body.put(
                "timestamp",
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(status)
                .body(body);
    }
}






