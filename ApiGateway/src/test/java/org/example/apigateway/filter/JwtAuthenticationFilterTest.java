package org.example.apigateway.filter;
import org.example.apigateway.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final JwtAuthenticationFilter filter =
            new JwtAuthenticationFilter(jwtService);

    private ServerWebExchange exchange(
            HttpMethod method, String path, String token) {

        MockServerHttpRequest.BaseBuilder<?> builder =
                MockServerHttpRequest.method(method, path);

        if (token != null) {
            builder.header(HttpHeaders.AUTHORIZATION, token);
        }

        builder.header("X-User-Id", "fake-user");
        builder.header("X-User-Role", "ADMIN");

        return MockServerWebExchange.from(builder.build());
    }

    @Test
    void shouldAllowPublicEndpointsWithoutToken() {
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        ServerWebExchange exchange =
                exchange(HttpMethod.GET, "/api/auth/login", null);

        filter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldAllowOptionsWithoutToken() {
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        ServerWebExchange exchange =
                exchange(HttpMethod.OPTIONS, "/api/business", null);

        filter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldRejectMissingAuthorizationHeader() {
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        ServerWebExchange exchange =
                exchange(HttpMethod.GET, "/api/business", null);

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode());

        verifyNoInteractions(chain);
    }

    @Test
    void shouldRejectNonBearerAuthorizationHeader() {
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        ServerWebExchange exchange =
                exchange(HttpMethod.GET, "/api/business", "Basic abc");

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode());

        verifyNoInteractions(chain);
    }

    @Test
    void shouldRejectInvalidToken() {
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(jwtService.isValid("bad-token")).thenReturn(false);

        ServerWebExchange exchange =
                exchange(HttpMethod.GET, "/api/business", "Bearer bad-token");

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode());

        verify(jwtService).isValid("bad-token");
        verifyNoInteractions(chain);
    }

    @Test
    void shouldForwardValidTokenWithTrustedHeaders() {
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        when(jwtService.isValid("valid-token")).thenReturn(true);
        when(jwtService.extractUserId("valid-token")).thenReturn("user-123");
        when(jwtService.extractRole("valid-token")).thenReturn("UNDERWRITER");

        ServerWebExchange exchange =
                exchange(HttpMethod.GET, "/api/business",
                        "Bearer valid-token");

        filter.filter(exchange, chain).block();

        var captor =
                org.mockito.ArgumentCaptor.forClass(ServerWebExchange.class);

        verify(chain).filter(captor.capture());

        ServerWebExchange forwarded = captor.getValue();

        assertEquals("user-123",
                forwarded.getRequest().getHeaders().getFirst("X-User-Id"));

        assertEquals("UNDERWRITER",
                forwarded.getRequest().getHeaders().getFirst("X-User-Role"));
    }

    @Test
    void shouldRemoveClientHeadersWhenRoleIsNull() {
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        when(jwtService.isValid("valid-token")).thenReturn(true);
        when(jwtService.extractUserId("valid-token")).thenReturn("user-123");
        when(jwtService.extractRole("valid-token")).thenReturn(null);

        ServerWebExchange exchange =
                exchange(HttpMethod.GET, "/api/business",
                        "Bearer valid-token");

        filter.filter(exchange, chain).block();

        var captor =
                org.mockito.ArgumentCaptor.forClass(ServerWebExchange.class);

        verify(chain).filter(captor.capture());

        var headers = captor.getValue().getRequest().getHeaders();

        assertEquals("user-123", headers.getFirst("X-User-Id"));
        assertNull(headers.getFirst("X-User-Role"));
    }

    @Test
    void shouldRejectWhenTokenProcessingThrowsException() {
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        when(jwtService.isValid("valid-token"))
                .thenThrow(new RuntimeException("JWT error"));

        ServerWebExchange exchange =
                exchange(HttpMethod.GET, "/api/business",
                        "Bearer valid-token");

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode());

        verifyNoInteractions(chain);
    }

    @Test
    void shouldReturnCorrectOrder() {
        assertEquals(-100, filter.getOrder());
    }
}
