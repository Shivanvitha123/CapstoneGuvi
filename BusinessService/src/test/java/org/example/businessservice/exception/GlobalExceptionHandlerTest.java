package org.example.businessservice.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleAlreadyExists() {
        var ex = new BusinessProfileAlreadyExistsException(
                "Business already exists"
        );

        var response = handler.handleAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT.value(), response.status());
        assertEquals("Conflict", response.error());
        assertEquals("Business already exists", response.message());
        assertNotNull(response.timestamp());
    }

    @Test
    void handleNotFound() {
        var ex = new BusinessProfileNotFoundException(
                "Business not found"
        );

        var response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.status());
        assertEquals("Not Found", response.error());
        assertEquals("Business not found", response.message());
        assertNotNull(response.timestamp());
    }

    @Test
    void handleAccessDenied() {
        var ex = new BusinessAccessDeniedException(
                "Access denied"
        );

        var response = handler.handleAccessDenied(ex);

        assertEquals(HttpStatus.FORBIDDEN.value(), response.status());
        assertEquals("Forbidden", response.error());
        assertEquals("Access denied", response.message());
        assertNotNull(response.timestamp());
    }

    @Test
    void handleValidation() {
        WebExchangeBindException ex =
                mock(WebExchangeBindException.class);

        FieldError fieldError = new FieldError(
                "request",
                "businessName",
                "Business name is required"
        );

        when(ex.getFieldErrors())
                .thenReturn(List.of(fieldError));

        var response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("Bad Request", response.error());
        assertTrue(response.message().contains("businessName"));
        assertTrue(response.message().contains(
                "Business name is required"
        ));
        assertNotNull(response.timestamp());
    }

    @Test
    void handleValidationWithMultipleErrors() {
        WebExchangeBindException ex =
                mock(WebExchangeBindException.class);

        FieldError first = new FieldError(
                "request", "businessName", "Name is required"
        );

        FieldError second = new FieldError(
                "request", "contactEmail", "Invalid email"
        );

        when(ex.getFieldErrors())
                .thenReturn(List.of(first, second));

        var response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertTrue(response.message().contains("businessName"));
        assertTrue(response.message().contains("contactEmail"));
    }

    @Test
    void handleGeneric() {
        var ex = new RuntimeException("Unexpected failure");

        var response = handler.handleGeneric(ex);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                response.status()
        );
        assertEquals("Internal Server Error", response.error());
        assertEquals("Unexpected failure", response.message());
        assertNotNull(response.timestamp());
    }
}