package org.example.identityservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionClassesTest {

    @Test
    void shouldCreateInvalidCredentialsException() {
        InvalidCredentialsException exception =
                new InvalidCredentialsException("Invalid credentials");

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void shouldCreateUserAlreadyExistsException() {
        UserAlreadyExistsException exception =
                new UserAlreadyExistsException("User already exists");

        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void shouldCreateUserNotFoundException() {
        UserNotFoundException exception =
                new UserNotFoundException("User not found");

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldCreateApiErrorResponse() {
        var timestamp = java.time.LocalDateTime.now();

        var response = new GlobalExceptionHandler.ApiErrorResponse(
                404,
                "Not Found",
                "User not found",
                timestamp
        );

        assertEquals(404, response.status());
        assertEquals("Not Found", response.error());
        assertEquals("User not found", response.message());
        assertEquals(timestamp, response.timestamp());
    }
}