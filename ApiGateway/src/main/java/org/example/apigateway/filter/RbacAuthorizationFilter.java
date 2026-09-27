package org.example.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class RbacAuthorizationFilter implements GlobalFilter, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(RbacAuthorizationFilter.class);

    private static final Set<String> ADMIN =
            Set.of("ADMIN");

    private static final Set<String> OWNER =
            Set.of("BUSINESS_OWNER");

    private static final Set<String> UNDERWRITING =
            Set.of("ADMIN", "UNDERWRITER");

    private static final Set<String> BUSINESS_READ =
            Set.of("ADMIN", "UNDERWRITER", "RISK_ENGINEER");

    // Roles allowed to view the policy list.
    private static final Set<String> POLICY_READ =
            Set.of(
                    "ADMIN",
                    "UNDERWRITER",
                    "RISK_ENGINEER",
                    "CLAIMS_ADJUSTER"
            );

    // Roles allowed to view individual policies.
    private static final Set<String> POLICY_DETAILS =
            Set.of(
                    "ADMIN",
                    "BUSINESS_OWNER",
                    "UNDERWRITER",
                    "RISK_ENGINEER",
                    "CLAIMS_ADJUSTER"
            );

    // Roles allowed to view risk assessments.
    private static final Set<String> RISK_READ =
            Set.of(
                    "ADMIN",
                    "BUSINESS_OWNER",
                    "UNDERWRITER",
                    "RISK_ENGINEER",
                    "CLAIMS_ADJUSTER"
            );

    // Roles allowed to view risk simulations.
    private static final Set<String> RISK_ANALYST =
            Set.of(
                    "ADMIN",
                    "UNDERWRITER",
                    "RISK_ENGINEER"
            );

    // Roles allowed to view all claims.
    private static final Set<String> CLAIMS_READ =
            Set.of(
                    "ADMIN",
                    "UNDERWRITER",
                    "CLAIMS_ADJUSTER",
                    "RISK_ENGINEER"
            );

    // Roles allowed to view individual claims and their documents.
    private static final Set<String> CLAIM_DETAILS =
            Set.of(
                    "ADMIN",
                    "BUSINESS_OWNER",
                    "UNDERWRITER",
                    "CLAIMS_ADJUSTER",
                    "RISK_ENGINEER"
            );

    // Only admins and claims adjusters can modify claims.
    private static final Set<String> CLAIMS_MANAGE =
            Set.of("ADMIN", "CLAIMS_ADJUSTER");

    private static final Pattern ID =
            Pattern.compile("\\d+");

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        // Normalize trailing slashes.
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        HttpMethod method = exchange.getRequest().getMethod();

        // Allow preflight requests and public endpoints.
        if (method == HttpMethod.OPTIONS || isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // Read the role added by JwtAuthenticationFilter.
        String role = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Role");

        if (role == null || role.isBlank()) {
            log.warn(
                    "RBAC denied: missing role, method={}, path={}",
                    method,
                    path
            );

            return forbidden(
                    exchange,
                    "Authenticated user role is missing"
            );
        }

        role = role.trim()
                .toUpperCase()
                .replaceFirst("^ROLE_", "");

        if (isAllowed(path, method, role)) {
            log.debug(
                    "RBAC allowed role={} method={} path={}",
                    role,
                    method,
                    path
            );

            return chain.filter(exchange);
        }

        log.warn(
                "RBAC denied role={} method={} path={}",
                role,
                method,
                path
        );

        return forbidden(
                exchange,
                "You do not have permission to perform this operation"
        );
    }

    private boolean isAllowed(
            String path,
            HttpMethod method,
            String role) {

        // =====================================================
        // IDENTITY
        // =====================================================

        if (path.equals("/api/auth/me")) {
            return true;
        }

        if (path.equals("/api/auth/users")
                && method == HttpMethod.GET) {
            return ADMIN.contains(role);
        }

        if (path.matches("^/api/auth/users/[^/]+$")
                && method == HttpMethod.GET) {
            return ADMIN.contains(role);
        }

        // =====================================================
        // BUSINESS
        // =====================================================

        if (path.equals("/api/business")) {

            if (method == HttpMethod.POST) {
                return OWNER.contains(role);
            }

            if (method == HttpMethod.GET) {
                return BUSINESS_READ.contains(role);
            }

            return false;
        }

        if (path.equals("/api/business/me")
                && method == HttpMethod.GET) {
            return OWNER.contains(role);
        }

        if (path.equals("/api/business/deletion-requests/me")
                && method == HttpMethod.GET) {
            return OWNER.contains(role);
        }

        if (path.equals("/api/business/deletion-requests/pending")
                && method == HttpMethod.GET) {
            return UNDERWRITING.contains(role);
        }

        if (path.matches(
                "^/api/business/\\d+/deletion-requests$"
        ) && method == HttpMethod.POST) {
            return OWNER.contains(role);
        }

        if (path.matches(
                "^/api/business/deletion-requests/\\d+/approve$"
        ) && method == HttpMethod.PUT) {
            return UNDERWRITING.contains(role);
        }

        if (path.matches(
                "^/api/business/deletion-requests/\\d+/reject$"
        ) && method == HttpMethod.PUT) {
            return UNDERWRITING.contains(role);
        }

        if (isIdPath(path, "/api/business/")) {

            if (method == HttpMethod.GET) {
                return OWNER.contains(role)
                        || BUSINESS_READ.contains(role);
            }

            if (method == HttpMethod.PUT) {
                return OWNER.contains(role);
            }

            if (method == HttpMethod.DELETE) {
                return ADMIN.contains(role);
            }

            return false;
        }

        // =====================================================
        // POLICIES
        // =====================================================

        if (path.equals("/api/policies")) {

            if (method == HttpMethod.POST) {
                return OWNER.contains(role);
            }

            if (method == HttpMethod.GET) {
                return POLICY_READ.contains(role);
            }

            return false;
        }

        if (path.equals("/api/policies/owner")
                && method == HttpMethod.GET) {
            return OWNER.contains(role);
        }

        if (path.matches("^/api/policies/\\d+$")) {

            if (method == HttpMethod.GET) {
                return POLICY_DETAILS.contains(role);
            }

            if (method == HttpMethod.PUT) {
                return OWNER.contains(role);
            }

            return false;
        }

        if (path.matches("^/api/policies/\\d+/submit$")
                && method == HttpMethod.POST) {
            return OWNER.contains(role);
        }

        if (path.matches("^/api/policies/\\d+/status$")
                && method == HttpMethod.PATCH) {
            return UNDERWRITING.contains(role);
        }

        // =====================================================
        // RISK ASSESSMENTS
        // =====================================================

        if (path.equals("/api/risk/assessments")
                && method == HttpMethod.GET) {
            return RISK_READ.contains(role);
        }

        if (path.matches("^/api/risk/assessments/\\d+$")
                && method == HttpMethod.GET) {
            return RISK_READ.contains(role);
        }

        if (path.matches(
                "^/api/risk/assessments/business/\\d+$"
        ) && method == HttpMethod.GET) {
            return RISK_READ.contains(role);
        }

        // Only Risk Engineers can create assessments.
        if (path.equals("/api/risk/assessments")
                && method == HttpMethod.POST) {
            return role.equals("RISK_ENGINEER");
        }

        // =====================================================
        // RISK SIMULATIONS
        // =====================================================

        if (path.equals("/api/risk/simulations")
                && method == HttpMethod.POST) {
            return Set.of("ADMIN", "RISK_ENGINEER")
                    .contains(role);
        }

        if ((path.equals("/api/risk/simulations")
                || path.matches("^/api/risk/simulations/\\d+$"))
                && method == HttpMethod.GET) {
            return RISK_ANALYST.contains(role);
        }

        // =====================================================
        // CLAIMS
        // =====================================================

        // Business Owners can submit claims.
        // Admins, Underwriters, Claims Adjusters, and
        // Risk Engineers can view all claims.
        if (path.equals("/api/claims")) {

            if (method == HttpMethod.POST) {
                return OWNER.contains(role);
            }

            if (method == HttpMethod.GET) {
                return CLAIMS_READ.contains(role);
            }

            return false;
        }

        // Business Owners can view their own claims.
        if (path.equals("/api/claims/owner")
                && method == HttpMethod.GET) {
            return OWNER.contains(role);
        }

        // View individual claim details.
        if (path.matches("^/api/claims/\\d+$")
                && method == HttpMethod.GET) {
            return CLAIM_DETAILS.contains(role);
        }

        // Upload claim documents.
        if (path.matches("^/api/claims/\\d+/documents$")
                && method == HttpMethod.POST) {
            return CLAIM_DETAILS.contains(role);
        }

        // FIX: View the list of documents for a claim.
        // This was missing and caused GET /documents to return 403.
        if (path.matches("^/api/claims/\\d+/documents$")
                && method == HttpMethod.GET) {
            return CLAIM_DETAILS.contains(role);
        }

        // Download an individual claim document.
        if (path.matches(
                "^/api/claims/\\d+/documents/[^/]+$"
        ) && method == HttpMethod.GET) {
            return CLAIM_DETAILS.contains(role);
        }

        // Only Admins and Claims Adjusters can update claim status.
        if (path.matches("^/api/claims/\\d+/status$")
                && method == HttpMethod.PATCH) {
            return CLAIMS_MANAGE.contains(role);
        }

        // =====================================================
        // RECOVERY
        // =====================================================

        if (path.matches("^/api/claims/\\d+/recovery$")
                && method == HttpMethod.POST) {
            return CLAIMS_MANAGE.contains(role);
        }

        if (path.matches("^/api/claims/\\d+/recovery$")
                && method == HttpMethod.GET) {
            return CLAIM_DETAILS.contains(role);
        }

        if (path.equals("/api/claims/recovery")
                && method == HttpMethod.GET) {
            return CLAIMS_READ.contains(role);
        }

        if (path.matches(
                "^/api/claims/recovery/\\d+/status$"
        ) && method == HttpMethod.PATCH) {
            return CLAIMS_MANAGE.contains(role);
        }

        // =====================================================
        // NOTIFICATIONS
        // =====================================================

        if (path.equals("/api/notifications")
                || path.equals("/api/notifications/unread")) {
            return method == HttpMethod.GET;
        }

        if (path.matches("^/api/notifications/\\d+/read$")
                && method == HttpMethod.PATCH) {
            return true;
        }

        // =====================================================
        // AUDIT
        // =====================================================

        if (path.equals("/api/audit")
                && method == HttpMethod.GET) {
            return ADMIN.contains(role);
        }

        if (path.equals("/api/audit")
                && method == HttpMethod.POST) {
            return true;
        }

        if (path.equals("/api/events")
                && method == HttpMethod.POST) {
            return true;
        }

        // Deny unknown paths and unsupported methods.
        return false;
    }

    private boolean isIdPath(String path, String prefix) {
        if (!path.startsWith(prefix)) {
            return false;
        }

        String id = path.substring(prefix.length());

        return ID.matcher(id).matches();
    }

    private boolean isPublicPath(String path) {
        return path.equals("/api/auth/login")
                || path.equals("/api/auth/register")
                || path.equals("/actuator/health")
                || path.equals("/actuator/info")
                || path.equals("/fallback/identity")
                || path.equals("/fallback/business")
                || path.equals("/fallback/policy")
                || path.equals("/fallback/risk")
                || path.equals("/fallback/claims");
    }

    private Mono<Void> forbidden(
            ServerWebExchange exchange,
            String message) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.FORBIDDEN);

        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        String response = """
                {
                    "status": 403,
                    "error": "Forbidden",
                    "message": "%s",
                    "path": "%s"
                }
                """.formatted(
                escapeJson(message),
                escapeJson(path)
        );

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        return exchange.getResponse().writeWith(
                Mono.just(
                        exchange.getResponse()
                                .bufferFactory()
                                .wrap(bytes)
                )
        );
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    @Override
    public int getOrder() {
        return -50;
    }
}