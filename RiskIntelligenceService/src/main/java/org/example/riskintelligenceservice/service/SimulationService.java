package org.example.riskintelligenceservice.service;


import org.example.riskintelligenceservice.dto.SimulationRequest;
import org.example.riskintelligenceservice.dto.SimulationResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SimulationService {

    Mono<SimulationResponse> createSimulation(
            SimulationRequest request,
            Long userId,
            String role
    );

    Mono<SimulationResponse> getSimulation(
            Long id,
            Long userId,
            String role
    );

    Flux<SimulationResponse> getSimulations(
            Long userId,
            String role
    );
}
