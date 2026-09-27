package org.example.identityservice.security;


import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationWebFilterTest {

    private JwtService jwtService;
    private JwtAuthenticationWebFilter filter;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        filter = new JwtAuthenticationWebFilter(jwtService);
    }

    private ServerWebExchange exchange(String authorization) {
        MockServerHttpRequest.BaseBuilder<?> builder =
                MockServerHttpRequest.get("/api/auth/me");

        if (authorization != null) {
            builder.header(HttpHeaders.AUTHORIZATION, authorization);
        }

        return MockServerWebExchange.from(builder.build());
    }

    @Test
    void shouldContinueWithoutAuthorizationHeader() {
        ServerWebExchange exchange = exchange(null);
        WebFilterChain chain = mock(WebFilterChain.class);

        when(chain.filter(exchange)).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldContinueWithNonBearerHeader() {
        ServerWebExchange exchange = exchange("Basic abc");
        WebFilterChain chain = mock(WebFilterChain.class);

        when(chain.filter(exchange)).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldRejectInvalidToken() {
        ServerWebExchange exchange = exchange("Bearer invalid");
        WebFilterChain chain = mock(WebFilterChain.class);

        when(jwtService.validateToken("invalid"))
                .thenThrow(new RuntimeException("Invalid token"));

        filter.filter(exchange, chain).block();

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode()
        );

        verify(chain, never()).filter(any(ServerWebExchange.class));
    }

    @Test
    void shouldRejectTokenWithMissingSubject() {
        ServerWebExchange exchange = exchange("Bearer valid");
        WebFilterChain chain = mock(WebFilterChain.class);
        Claims claims = mock(Claims.class);

        when(jwtService.validateToken("valid")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(null);
        when(claims.get("role", String.class)).thenReturn("ADMIN");

        filter.filter(exchange, chain).block();

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode()
        );

        verify(chain, never()).filter(any(ServerWebExchange.class));
    }

    @Test
    void shouldRejectTokenWithMissingRole() {
        ServerWebExchange exchange = exchange("Bearer valid");
        WebFilterChain chain = mock(WebFilterChain.class);
        Claims claims = mock(Claims.class);

        when(jwtService.validateToken("valid")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("1");
        when(claims.get("role", String.class)).thenReturn(null);

        filter.filter(exchange, chain).block();

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exchange.getResponse().getStatusCode()
        );

        verify(chain, never()).filter(any(ServerWebExchange.class));
    }

    @Test
    void shouldAuthenticateValidToken() {
        ServerWebExchange exchange = exchange("Bearer valid");
        WebFilterChain chain = mock(WebFilterChain.class);
        Claims claims = mock(Claims.class);

        when(jwtService.validateToken("valid")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("25");
        when(claims.get("role", String.class)).thenReturn("ADMIN");

        when(chain.filter(exchange)).thenAnswer(invocation ->
                ReactiveSecurityContextHolder.getContext()
                        .doOnNext(securityContext -> {
                            Authentication authentication =
                                    securityContext.getAuthentication();

                            assertNotNull(authentication);
                            assertEquals("25", authentication.getName());

                            assertTrue(authentication.getAuthorities().stream()
                                    .anyMatch(authority ->
                                            authority.getAuthority()
                                                    .equals("ROLE_ADMIN")));
                        })
                        .then()
        );

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(jwtService).validateToken("valid");
        verify(chain).filter(exchange);
    }
}
