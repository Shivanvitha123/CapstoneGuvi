package org.example.apigateway.controller;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class FallbackControllerTest {

    private final FallbackController controller =
            new FallbackController();

    @Test
    void shouldReturnServiceUnavailableResponse() {
        var response = controller.fallback("identity").block();

        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE,
                response.getStatusCode());

        var body = response.getBody();

        assertNotNull(body);
        assertEquals(503, body.get("status"));
        assertEquals("Service Unavailable", body.get("error"));
        assertEquals(
                "The requested service is temporarily unavailable",
                body.get("message")
        );
        assertEquals("identity", body.get("service"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void shouldIncludeRequestedServiceName() {
        var response = controller.fallback("claims").block();

        assertNotNull(response);
        assertEquals("claims", response.getBody().get("service"));
    }
}
