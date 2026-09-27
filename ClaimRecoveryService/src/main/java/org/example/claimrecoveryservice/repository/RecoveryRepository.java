package org.example.claimrecoveryservice.repository;


import org.example.claimrecoveryservice.entity.Recovery;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecoveryRepository
        extends ReactiveCrudRepository<Recovery, Long> {

    Flux<Recovery> findByClaimId(Long claimId);

    Flux<Recovery> findByOwnerId(Long ownerId);

    Mono<Recovery> findByIdAndOwnerId(
            Long id,
            Long ownerId
    );
}
