package org.example.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();

        String method = exchange.getRequest()
                .getMethod()
                .name();

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        log.info(
                "Incoming request: {} {}",
                method,
                path
        );

        return chain.filter(exchange)
                .doFinally(signalType -> {

                    long duration =
                            System.currentTimeMillis()
                                    - startTime;

                    Integer status = null;

                    if (exchange.getResponse()
                            .getStatusCode() != null) {

                        status = exchange.getResponse()
                                .getStatusCode()
                                .value();
                    }

                    log.info(
                            "Completed request: {} {} | status={} | duration={}ms",
                            method,
                            path,
                            status,
                            duration
                    );
                });
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
