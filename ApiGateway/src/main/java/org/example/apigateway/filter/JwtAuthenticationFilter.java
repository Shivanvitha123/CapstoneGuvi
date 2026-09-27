package org.example.apigateway.filter;

import org.example.apigateway.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/actuator/health",
            "/actuator/info",
            "/fallback"
    );

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String path = exchange
                .getRequest()
                .getURI()
                .getPath();

        // Allow CORS preflight
        if ("OPTIONS".equalsIgnoreCase(
                exchange.getRequest().getMethod().name())) {

            return chain.filter(exchange);
        }

        // Allow public endpoints
        if (isPublicPath(path)) {

            return chain.filter(exchange);
        }

        String authorizationHeader =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            log.warn("Missing JWT for request: {}", path);

            return unauthorized(exchange);
        }

        String token =
                authorizationHeader.substring(7);

        try {

            if (!jwtService.isValid(token)) {

                log.warn("Invalid JWT for request: {}", path);

                return unauthorized(exchange);
            }

            String userId =
                    jwtService.extractUserId(token);

            String role =
                    jwtService.extractRole(token);

            log.debug(
                    "Authenticated request. userId={}, role={}, path={}",
                    userId,
                    role,
                    path
            );

            ServerHttpRequest mutatedRequest =
                    exchange.getRequest()
                            .mutate()
                            .headers(headers -> {

                                // Remove client supplied headers
                                headers.remove("X-User-Id");
                                headers.remove("X-User-Role");

                                // Add trusted headers
                                headers.add("X-User-Id", userId);

                                if (role != null) {
                                    headers.add("X-User-Role", role);
                                }
                            })
                            .build();

            ServerWebExchange mutatedExchange =
                    exchange.mutate()
                            .request(mutatedRequest)
                            .build();

            return chain.filter(mutatedExchange);

        } catch (Exception exception) {

            log.error(
                    "JWT processing failed for path {}",
                    path,
                    exception
            );

            return unauthorized(exchange);
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream()
                .anyMatch(publicPath ->
                        path.equals(publicPath)
                                || path.startsWith(publicPath + "/")
                );
    }

    private Mono<Void> unauthorized(
            ServerWebExchange exchange) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        String response = """
                {
                    "status": 401,
                    "error": "Unauthorized",
                    "message": "Valid JWT token is required",
                    "path": "%s"
                }
                """.formatted(
                exchange.getRequest()
                        .getURI()
                        .getPath()
        );

        byte[] bytes = response.getBytes();

        return exchange.getResponse()
                .writeWith(
                        Mono.just(
                                exchange.getResponse()
                                        .bufferFactory()
                                        .wrap(bytes)
                        )
                );
    }

    @Override
    public int getOrder() {

        return -100;
    }
}



