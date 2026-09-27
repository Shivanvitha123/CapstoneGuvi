package org.example.apigateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import static org.junit.jupiter.api.Assertions.*;

class GlobalErrorWebExceptionHandlerTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper().findAndRegisterModules();

    private final GlobalErrorWebExceptionHandler handler =
            new GlobalErrorWebExceptionHandler(objectMapper);

    @Test
    void shouldReturnInternalServerError() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/business").build()
        );

        handler.handle(
                exchange,
                new RuntimeException("Database failed")
        ).block();

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exchange.getResponse().getStatusCode()
        );

        assertEquals(
                "application/json",
                exchange.getResponse().getHeaders()
                        .getContentType().toString()
        );
    }

    @Test
    void shouldReturnErrorDetailsInJson() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/business").build()
        );

        handler.handle(
                exchange,
                new RuntimeException("Database failed")
        ).block();

        String body = exchange.getResponse()
                .getBodyAsString()
                .block();

        assertNotNull(body);
        assertTrue(body.contains("500"));
        assertTrue(body.contains("Internal Server Error"));
        assertTrue(body.contains("Database failed"));
        assertTrue(body.contains("/api/business"));
    }

    @Test
    void shouldUseDefaultMessageWhenExceptionMessageIsNull() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/test").build()
        );

        handler.handle(
                exchange,
                new RuntimeException((String) null)
        ).block();

        String body = exchange.getResponse()
                .getBodyAsString()
                .block();

        assertNotNull(body);
        assertTrue(body.contains("Unexpected gateway error"));
    }

    @Test
    void shouldPropagateErrorWhenResponseAlreadyCommitted() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/test").build()
        );

        exchange.getResponse().setComplete().block();

        RuntimeException exception =
                new RuntimeException("Already committed");

        assertThrows(
                RuntimeException.class,
                () -> handler.handle(exchange, exception).block()
        );
    }
}