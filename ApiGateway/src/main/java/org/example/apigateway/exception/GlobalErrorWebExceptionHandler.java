package org.example.apigateway.exception;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.apigateway.dto.ApiErrorResponse;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler
        implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalErrorWebExceptionHandler(
            ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(
            ServerWebExchange exchange,
            Throwable exception) {

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(exception);
        }

        exchange.getResponse()
                .setStatusCode(
                        HttpStatus.INTERNAL_SERVER_ERROR
                );

        exchange.getResponse()
                .getHeaders()
                .setContentType(
                        MediaType.APPLICATION_JSON
                );

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(
                        500,
                        "Internal Server Error",
                        exception.getMessage() != null
                                ? exception.getMessage()
                                : "Unexpected gateway error",
                        exchange.getRequest()
                                .getURI()
                                .getPath(),
                        LocalDateTime.now()
                );

        try {

            byte[] bytes =
                    objectMapper.writeValueAsBytes(
                            errorResponse
                    );

            return exchange.getResponse()
                    .writeWith(
                            Mono.just(
                                    exchange.getResponse()
                                            .bufferFactory()
                                            .wrap(bytes)
                            )
                    );

        } catch (Exception serializationException) {

            return Mono.error(serializationException);
        }
    }
}


