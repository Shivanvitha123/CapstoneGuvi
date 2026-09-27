package org.example.identityservice.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleUserAlreadyExists() {
        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> response =
                handler.handleUserAlreadyExists(
                        new UserAlreadyExistsException("User already exists")
                );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().status());
        assertEquals("Conflict", response.getBody().error());
        assertEquals("User already exists", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void shouldHandleUserNotFound() {
        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> response =
                handler.handleUserNotFound(
                        new UserNotFoundException("User not found")
                );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Not Found", response.getBody().error());
        assertEquals("User not found", response.getBody().message());
    }

    @Test
    void shouldHandleInvalidCredentials() {
        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> response =
                handler.handleInvalidCredentials(
                        new InvalidCredentialsException("Invalid credentials")
                );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().status());
        assertEquals("Unauthorized", response.getBody().error());
        assertEquals("Invalid credentials", response.getBody().message());
    }

    @Test
    void shouldHandleGenericException() {
        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> response =
                handler.handleGenericException(
                        new RuntimeException("Internal database details")
                );

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("Internal Server Error", response.getBody().error());
        assertEquals(
                "An unexpected error occurred",
                response.getBody().message()
        );
    }

    @Test
    void shouldIncludeTimestampInErrorResponse() {
        ResponseEntity<GlobalExceptionHandler.ApiErrorResponse> response =
                handler.handleUserNotFound(
                        new UserNotFoundException("Missing user")
                );

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().timestamp());
    }
}
