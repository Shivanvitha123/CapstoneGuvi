package org.example.apigateway.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping(
            value = "/{service}",
            method = {
                    RequestMethod.GET,
                    RequestMethod.POST,
                    RequestMethod.PUT,
                    RequestMethod.DELETE,
                    RequestMethod.PATCH
            }
    )
    public Mono<ResponseEntity<Map<String, Object>>> fallback(
            @PathVariable String service) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("status", 503);
        response.put("error", "Service Unavailable");
        response.put(
                "message",
                "The requested service is temporarily unavailable"
        );
        response.put("service", service);
        response.put("timestamp", LocalDateTime.now());

        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(response)
        );
    }
}


