package org.example.underwritingpolicyservice.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFound_returns404() {
        var response = handler.handleNotFound(
                new PolicyNotFoundException("Policy not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Not Found", response.getBody().get("error"));
        assertEquals("Policy not found", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleForbidden_returns403() {
        var response = handler.handleForbidden(
                new PolicyAccessDeniedException("Access denied"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().get("status"));
        assertEquals("Forbidden", response.getBody().get("error"));
        assertEquals("Access denied", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleConflict_returns409() {
        var response = handler.handleConflict(
                new PolicyAlreadyExistsException("Policy already exists"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Conflict", response.getBody().get("error"));
        assertEquals("Policy already exists",
                response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleValidation_returns400WithFieldErrors() {
        WebExchangeBindException exception =
                mock(WebExchangeBindException.class);

        FieldError fieldError = new FieldError(
                "createPolicyRequest",
                "policyNumber",
                "Policy number is required"
        );

        when(exception.getFieldErrors())
                .thenReturn(List.of(fieldError));

        var response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);

        assertEquals(400, body.get("status"));
        assertEquals("Bad Request", body.get("error"));
        assertEquals("Validation failed", body.get("message"));
        assertNotNull(body.get("timestamp"));

        @SuppressWarnings("unchecked")
        Map<String, String> errors =
                (Map<String, String>) body.get("errors");

        assertEquals(1, errors.size());
        assertEquals("Policy number is required",
                errors.get("policyNumber"));
    }

    @Test
    void handleValidation_multipleFieldErrors() {
        WebExchangeBindException exception =
                mock(WebExchangeBindException.class);

        FieldError policyNumberError = new FieldError(
                "request", "policyNumber", "Policy number is required");

        FieldError coverageError = new FieldError(
                "request", "coverageAmount", "Coverage amount is required");

        when(exception.getFieldErrors())
                .thenReturn(List.of(policyNumberError, coverageError));

        var response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> errors =
                (Map<String, String>) response.getBody().get("errors");

        assertEquals(2, errors.size());
        assertEquals("Policy number is required",
                errors.get("policyNumber"));
        assertEquals("Coverage amount is required",
                errors.get("coverageAmount"));
    }

    @Test
    void handleValidation_emptyErrors() {
        WebExchangeBindException exception =
                mock(WebExchangeBindException.class);

        when(exception.getFieldErrors()).thenReturn(List.of());

        var response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> errors =
                (Map<String, String>) response.getBody().get("errors");

        assertTrue(errors.isEmpty());
    }

    @Test
    void handleGeneric_returns500() {
        var response = handler.handleGeneric(
                new RuntimeException("Internal failure"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Internal Server Error",
                response.getBody().get("error"));
        assertEquals("An unexpected error occurred",
                response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }
}