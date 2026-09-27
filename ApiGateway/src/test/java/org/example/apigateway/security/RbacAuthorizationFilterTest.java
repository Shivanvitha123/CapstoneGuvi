package org.example.apigateway.security;

import org.example.apigateway.filter.RbacAuthorizationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RbacAuthorizationFilterTest {

    private RbacAuthorizationFilter filter;

    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {

        filter = new RbacAuthorizationFilter();

        chain = mock(GatewayFilterChain.class);

        when(chain.filter(any()))
                .thenReturn(Mono.empty());
    }

    @Test
    void adminShouldAccessBusinessEndpoint() {

        MockServerWebExchange exchange =
                createExchange(
                        "/api/business/101",
                        "ADMIN"
                );

        StepVerifier.create(
                        filter.filter(exchange, chain)
                )
                .verifyComplete();

        verify(chain, times(1))
                .filter(exchange);
    }

    @Test
    void businessOwnerShouldAccessBusinessEndpoint() {

        MockServerWebExchange exchange =
                createExchange(
                        "/api/business/101",
                        "BUSINESS_OWNER"
                );

        StepVerifier.create(
                        filter.filter(exchange, chain)
                )
                .verifyComplete();

        verify(chain, times(1))
                .filter(exchange);
    }

    @Test
    void underwriterShouldAccessPolicyEndpoint() {

        MockServerWebExchange exchange =
                createExchange(
                        "/api/policies/101",
                        "UNDERWRITER"
                );

        StepVerifier.create(
                        filter.filter(exchange, chain)
                )
                .verifyComplete();

        verify(chain, times(1))
                .filter(exchange);
    }

    @Test
    void riskEngineerShouldAccessRiskEndpoint() {

        MockServerWebExchange exchange =
                createExchange(
                        "/api/risk/101",
                        "RISK_ENGINEER"
                );

        StepVerifier.create(
                        filter.filter(exchange, chain)
                )
                .verifyComplete();

        verify(chain, times(1))
                .filter(exchange);
    }

    @Test
    void claimsAdjusterShouldAccessClaimsEndpoint() {

        MockServerWebExchange exchange =
                createExchange(
                        "/api/claims/101",
                        "CLAIMS_ADJUSTER"
                );

        StepVerifier.create(
                        filter.filter(exchange, chain)
                )
                .verifyComplete();

        verify(chain, times(1))
                .filter(exchange);
    }

    @Test
    void claimsAdjusterShouldNotAccessRiskEndpoint() {

        MockServerWebExchange exchange =
                createExchange(
                        "/api/risk/101",
                        "CLAIMS_ADJUSTER"
                );

        StepVerifier.create(
                        filter.filter(exchange, chain)
                )
                .verifyComplete();

        assertEquals(
                HttpStatus.FORBIDDEN,
                exchange.getResponse().getStatusCode()
        );

        verify(chain, never())
                .filter(any());
    }

    @Test
    void riskEngineerShouldNotAccessClaimsEndpoint() {

        MockServerWebExchange exchange =
                createExchange(
                        "/api/claims/101",
                        "RISK_ENGINEER"
                );

        StepVerifier.create(
                        filter.filter(exchange, chain)
                )
                .verifyComplete();

        assertEquals(
                HttpStatus.FORBIDDEN,
                exchange.getResponse().getStatusCode()
        );

        verify(chain, never())
                .filter(any());
    }

    @Test
    void underwriterShouldNotAccessClaimsEndpoint() {

        MockServerWebExchange exchange =
                createExchange(
                        "/api/claims/101",
                        "UNDERWRITER"
                );

        StepVerifier.create(
                        filter.filter(exchange, chain)
                )
                .verifyComplete();

        assertEquals(
                HttpStatus.FORBIDDEN,
                exchange.getResponse().getStatusCode()
        );

        verify(chain, never())
                .filter(any());
    }

    @Test
    void requestWithoutRoleShouldBeForbidden() {

        MockServerWebExchange exchange =
                createExchangeWithoutRole(
                        "/api/business/101"
                );

        StepVerifier.create(
                        filter.filter(exchange, chain)
                )
                .verifyComplete();

        assertEquals(
                HttpStatus.FORBIDDEN,
                exchange.getResponse().getStatusCode()
        );

        verify(chain, never())
                .filter(any());
    }

    @Test
    void publicLoginEndpointShouldBeAllowedWithoutRole() {

        MockServerWebExchange exchange =
                createExchangeWithoutRole(
                        "/api/auth/login"
                );

        StepVerifier.create(
                        filter.filter(exchange, chain)
                )
                .verifyComplete();

        verify(chain, times(1))
                .filter(exchange);
    }

    @Test
    void publicRegisterEndpointShouldBeAllowedWithoutRole() {

        MockServerWebExchange exchange =
                createExchangeWithoutRole(
                        "/api/auth/register"
                );

        StepVerifier.create(
                        filter.filter(exchange, chain)
                )
                .verifyComplete();

        verify(chain, times(1))
                .filter(exchange);
    }

    @Test
    void unknownEndpointShouldBeForbidden() {

        MockServerWebExchange exchange =
                createExchange(
                        "/api/unknown/101",
                        "ADMIN"
                );

        StepVerifier.create(
                        filter.filter(exchange, chain)
                )
                .verifyComplete();

        assertEquals(
                HttpStatus.FORBIDDEN,
                exchange.getResponse().getStatusCode()
        );

        verify(chain, never())
                .filter(any());
    }

    private MockServerWebExchange createExchange(
            String path,
            String role) {

        MockServerHttpRequest request =
                MockServerHttpRequest
                        .get(path)
                        .header("X-User-Role", role)
                        .build();

        return MockServerWebExchange
                .from(request);
    }

    private MockServerWebExchange createExchangeWithoutRole(
            String path) {

        MockServerHttpRequest request =
                MockServerHttpRequest
                        .get(path)
                        .build();

        return MockServerWebExchange
                .from(request);
    }
}
