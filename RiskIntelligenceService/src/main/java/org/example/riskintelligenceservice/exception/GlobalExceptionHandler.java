package org.example.riskintelligenceservice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RiskAssessmentNotFoundException.class)
    public Mono<org.springframework.http.ResponseEntity<Map<String, Object>>>
    handleRiskNotFound(
            RiskAssessmentNotFoundException exception) {

        return Mono.just(
                org.springframework.http.ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "status", 404,
                                "message", exception.getMessage(),
                                "timestamp", LocalDateTime.now()
                        ))
        );
    }

    @ExceptionHandler(SimulationNotFoundException.class)
    public Mono<org.springframework.http.ResponseEntity<Map<String, Object>>>
    handleSimulationNotFound(
            SimulationNotFoundException exception) {

        return Mono.just(
                org.springframework.http.ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "status", 404,
                                "message", exception.getMessage(),
                                "timestamp", LocalDateTime.now()
                        ))
        );
    }

    @ExceptionHandler(RiskAccessDeniedException.class)
    public Mono<org.springframework.http.ResponseEntity<Map<String, Object>>>
    handleAccessDenied(
            RiskAccessDeniedException exception) {

        return Mono.just(
                org.springframework.http.ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "status", 403,
                                "message", exception.getMessage(),
                                "timestamp", LocalDateTime.now()
                        ))
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<org.springframework.http.ResponseEntity<Map<String, Object>>>
    handleGenericException(Exception exception) {

        return Mono.just(
                org.springframework.http.ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "status", 500,
                                "message", "An unexpected error occurred",
                                "timestamp", LocalDateTime.now()
                        ))
        );
    }
}

