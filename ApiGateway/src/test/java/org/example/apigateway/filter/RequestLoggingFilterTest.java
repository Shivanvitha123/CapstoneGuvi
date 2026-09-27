package org.example.apigateway.filter;


import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RequestLoggingFilterTest {

    private final RequestLoggingFilter filter =
            new RequestLoggingFilter();

    @Test
    void shouldPassRequestToNextFilter() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/business").build()
        );

        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        assertDoesNotThrow(() -> filter.filter(exchange, chain).block());

        verify(chain).filter(exchange);
    }

    @Test
    void shouldCompleteWhenDownstreamCompletes() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/business").build()
        );

        exchange.getResponse().setStatusCode(HttpStatus.OK);

        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        assertDoesNotThrow(() -> filter.filter(exchange, chain).block());
    }

    @Test
    void shouldPropagateDownstreamError() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/business").build()
        );

        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any()))
                .thenReturn(Mono.error(new RuntimeException("Downstream error")));

        assertThrows(
                RuntimeException.class,
                () -> filter.filter(exchange, chain).block()
        );
    }

    @Test
    void shouldReturnCorrectOrder() {
        assertEquals(-200, filter.getOrder());
    }
}
