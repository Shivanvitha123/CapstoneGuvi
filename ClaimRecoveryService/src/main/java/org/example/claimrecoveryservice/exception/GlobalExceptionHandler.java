package org.example.claimrecoveryservice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClaimNotFoundException.class)
    public ResponseEntity<Map<String, Object>>
    handleClaimNotFound(
            ClaimNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "status", 404,
                        "error", "CLAIM_NOT_FOUND",
                        "message", exception.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(RecoveryNotFoundException.class)
    public ResponseEntity<Map<String, Object>>
    handleRecoveryNotFound(
            RecoveryNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "status", 404,
                        "error", "RECOVERY_NOT_FOUND",
                        "message", exception.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(ClaimAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>>
    handleAccessDenied(
            ClaimAccessDeniedException exception) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "status", 403,
                        "error", "ACCESS_DENIED",
                        "message", exception.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>>
    handleGeneric(Exception exception) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status", 500,
                        "error", "INTERNAL_SERVER_ERROR",
                        "message", "An unexpected error occurred",
                        "timestamp", LocalDateTime.now()
                ));
    }
}

