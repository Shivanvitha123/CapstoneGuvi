package org.example.riskintelligenceservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.riskintelligenceservice.dto.SimulationRequest;
import org.example.riskintelligenceservice.dto.SimulationResponse;
import org.example.riskintelligenceservice.service.SimulationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/risk/simulations")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService service;

    @PostMapping
    public Mono<ResponseEntity<SimulationResponse>> createSimulation(
            @Valid @RequestBody SimulationRequest request,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());

        String role = authentication
                .getAuthorities()
                .iterator()
                .next()
                .getAuthority()
                .replace("ROLE_", "");

        return service.createSimulation(request, userId, role)
                .map(response ->
                        ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response)
                );
    }

    @GetMapping("/{id}")
    public Mono<SimulationResponse> getSimulation(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());

        String role = authentication
                .getAuthorities()
                .iterator()
                .next()
                .getAuthority()
                .replace("ROLE_", "");

        return service.getSimulation(id, userId, role);
    }

    @GetMapping
    public Flux<SimulationResponse> getSimulations(
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());

        String role = authentication
                .getAuthorities()
                .iterator()
                .next()
                .getAuthority()
                .replace("ROLE_", "");

        return service.getSimulations(userId, role);
    }
}