package org.example.apigateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RbacAuthorizationFilterTest {

    private final RbacAuthorizationFilter filter =
            new RbacAuthorizationFilter();

    private ServerWebExchange exchange(
            HttpMethod method, String path, String role) {

        MockServerHttpRequest.BaseBuilder<?> builder =
                MockServerHttpRequest.method(method, path);

        if (role != null) {
            builder.header("X-User-Role", role);
        }

        return MockServerWebExchange.from(builder.build());
    }

    private void check(
            HttpMethod method,
            String path,
            String role,
            boolean allowed) {

        ServerWebExchange exchange = exchange(method, path, role);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();

        if (allowed) {
            verify(chain).filter(any(ServerWebExchange.class));
            assertNull(exchange.getResponse().getStatusCode());
        } else {
            verify(chain, never()).filter(any(ServerWebExchange.class));
            assertEquals(
                    HttpStatus.FORBIDDEN,
                    exchange.getResponse().getStatusCode()
            );
        }
    }

    @Test
    void shouldAllowPublicPathsAndOptions() {
        List<String> paths = List.of(
                "/api/auth/login",
                "/api/auth/register",
                "/actuator/health",
                "/actuator/info",
                "/fallback/identity",
                "/fallback/business",
                "/fallback/policy",
                "/fallback/risk",
                "/fallback/claims"
        );

        for (String path : paths) {
            check(HttpMethod.GET, path, null, true);
        }

        check(HttpMethod.OPTIONS, "/api/business", null, true);
    }

    @Test
    void shouldDenyMissingOrBlankRole() {
        check(HttpMethod.GET, "/api/business", null, false);
        check(HttpMethod.GET, "/api/business", " ", false);
    }

    @Test
    void shouldAllowAuthenticatedUserEndpoint() {
        check(HttpMethod.GET, "/api/auth/me", "BUSINESS_OWNER", true);
        check(HttpMethod.GET, "/api/auth/me", "ADMIN", true);
    }

    @Test
    void shouldRestrictUserManagementToAdmin() {
        check(HttpMethod.GET, "/api/auth/users", "ADMIN", true);
        check(HttpMethod.GET, "/api/auth/users/12", "ADMIN", true);
        check(HttpMethod.GET, "/api/auth/users", "UNDERWRITER", false);
        check(HttpMethod.POST, "/api/auth/users", "ADMIN", false);
    }

    @Test
    void shouldApplyBusinessPermissions() {
        check(HttpMethod.POST, "/api/business", "BUSINESS_OWNER", true);
        check(HttpMethod.POST, "/api/business", "ADMIN", false);

        check(HttpMethod.GET, "/api/business", "ADMIN", true);
        check(HttpMethod.GET, "/api/business", "UNDERWRITER", true);
        check(HttpMethod.GET, "/api/business", "RISK_ENGINEER", true);
        check(HttpMethod.GET, "/api/business", "BUSINESS_OWNER", false);

        check(HttpMethod.GET, "/api/business/12", "BUSINESS_OWNER", true);
        check(HttpMethod.PUT, "/api/business/12", "BUSINESS_OWNER", true);
        check(HttpMethod.DELETE, "/api/business/12", "ADMIN", true);
        check(HttpMethod.DELETE, "/api/business/12", "BUSINESS_OWNER", false);

        check(HttpMethod.GET, "/api/business/me", "BUSINESS_OWNER", true);
        check(HttpMethod.GET, "/api/business/me", "ADMIN", false);

        check(HttpMethod.GET,
                "/api/business/deletion-requests/me",
                "BUSINESS_OWNER", true);

        check(HttpMethod.GET,
                "/api/business/deletion-requests/pending",
                "UNDERWRITER", true);

        check(HttpMethod.POST,
                "/api/business/12/deletion-requests",
                "BUSINESS_OWNER", true);

        check(HttpMethod.PUT,
                "/api/business/deletion-requests/12/approve",
                "UNDERWRITER", true);

        check(HttpMethod.PUT,
                "/api/business/deletion-requests/12/reject",
                "ADMIN", true);

        check(HttpMethod.GET, "/api/business/not-a-number",
                "ADMIN", false);
    }

    @Test
    void shouldApplyPolicyPermissions() {
        check(HttpMethod.POST, "/api/policies", "BUSINESS_OWNER", true);
        check(HttpMethod.POST, "/api/policies", "ADMIN", false);

        check(HttpMethod.GET, "/api/policies", "UNDERWRITER", true);
        check(HttpMethod.GET, "/api/policies", "CLAIMS_ADJUSTER", true);
        check(HttpMethod.GET, "/api/policies", "BUSINESS_OWNER", false);

        check(HttpMethod.GET, "/api/policies/owner",
                "BUSINESS_OWNER", true);

        check(HttpMethod.GET, "/api/policies/12",
                "BUSINESS_OWNER", true);

        check(HttpMethod.PUT, "/api/policies/12",
                "BUSINESS_OWNER", true);

        check(HttpMethod.POST, "/api/policies/12/submit",
                "BUSINESS_OWNER", true);

        check(HttpMethod.PATCH, "/api/policies/12/status",
                "UNDERWRITER", true);

        check(HttpMethod.PATCH, "/api/policies/12/status",
                "BUSINESS_OWNER", false);
    }

    @Test
    void shouldApplyRiskPermissions() {
        check(HttpMethod.GET, "/api/risk/assessments",
                "BUSINESS_OWNER", true);

        check(HttpMethod.GET, "/api/risk/assessments/12",
                "RISK_ENGINEER", true);

        check(HttpMethod.GET, "/api/risk/assessments/business/12",
                "CLAIMS_ADJUSTER", true);

        check(HttpMethod.POST, "/api/risk/assessments",
                "RISK_ENGINEER", true);

        check(HttpMethod.POST, "/api/risk/assessments",
                "ADMIN", false);

        check(HttpMethod.POST, "/api/risk/simulations",
                "ADMIN", true);

        check(HttpMethod.POST, "/api/risk/simulations",
                "UNDERWRITER", false);

        check(HttpMethod.GET, "/api/risk/simulations",
                "UNDERWRITER", true);

        check(HttpMethod.GET, "/api/risk/simulations/12",
                "RISK_ENGINEER", true);

        check(HttpMethod.GET, "/api/risk/simulations/12",
                "BUSINESS_OWNER", false);
    }

    @Test
    void shouldApplyClaimPermissions() {
        check(HttpMethod.POST, "/api/claims",
                "BUSINESS_OWNER", true);

        check(HttpMethod.GET, "/api/claims",
                "UNDERWRITER", true);

        check(HttpMethod.GET, "/api/claims",
                "BUSINESS_OWNER", false);

        check(HttpMethod.GET, "/api/claims/owner",
                "BUSINESS_OWNER", true);

        check(HttpMethod.GET, "/api/claims/12",
                "BUSINESS_OWNER", true);

        check(HttpMethod.POST, "/api/claims/12/documents",
                "CLAIMS_ADJUSTER", true);

        check(HttpMethod.GET, "/api/claims/12/documents",
                "BUSINESS_OWNER", true);

        check(HttpMethod.GET, "/api/claims/12/documents/file.pdf",
                "RISK_ENGINEER", true);

        check(HttpMethod.PATCH, "/api/claims/12/status",
                "CLAIMS_ADJUSTER", true);

        check(HttpMethod.PATCH, "/api/claims/12/status",
                "BUSINESS_OWNER", false);

        check(HttpMethod.POST, "/api/claims/12/recovery",
                "ADMIN", true);

        check(HttpMethod.GET, "/api/claims/12/recovery",
                "BUSINESS_OWNER", true);

        check(HttpMethod.GET, "/api/claims/recovery",
                "UNDERWRITER", true);

        check(HttpMethod.PATCH, "/api/claims/recovery/12/status",
                "CLAIMS_ADJUSTER", true);
    }

    @Test
    void shouldApplyNotificationAndAuditPermissions() {
        check(HttpMethod.GET, "/api/notifications",
                "BUSINESS_OWNER", true);

        check(HttpMethod.GET, "/api/notifications/unread",
                "RISK_ENGINEER", true);

        check(HttpMethod.PATCH, "/api/notifications/12/read",
                "BUSINESS_OWNER", true);

        check(HttpMethod.GET, "/api/audit",
                "ADMIN", true);

        check(HttpMethod.GET, "/api/audit",
                "UNDERWRITER", false);

        check(HttpMethod.POST, "/api/audit",
                "BUSINESS_OWNER", true);

        check(HttpMethod.POST, "/api/events",
                "CLAIMS_ADJUSTER", true);
    }

    @Test
    void shouldNormalizeRoleAndTrailingSlash() {
        check(HttpMethod.GET, "/api/business/",
                "ROLE_ADMIN", true);
    }

    @Test
    void shouldDenyUnknownPathsAndUnsupportedMethods() {
        check(HttpMethod.GET, "/unknown/path",
                "ADMIN", false);

        check(HttpMethod.DELETE, "/api/policies/12",
                "ADMIN", false);

        check(HttpMethod.POST, "/api/notifications",
                "ADMIN", false);
    }

    @Test
    void shouldReturnCorrectOrder() {
        assertEquals(-50, filter.getOrder());
    }
}
