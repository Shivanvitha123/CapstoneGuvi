package org.example.riskintelligenceservice.repository;


import org.example.riskintelligenceservice.entity.Simulation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SimulationRepository
        extends ReactiveCrudRepository<Simulation, Long> {

    Flux<Simulation> findByBusinessId(Long businessId);

    Flux<Simulation> findByOwnerId(Long ownerId);

    Mono<Simulation> findByIdAndOwnerId(
            Long id,
            Long ownerId
    );
}

