package org.example.claimrecoveryservice.repository;


import org.example.claimrecoveryservice.entity.Claim;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClaimRepository
        extends ReactiveCrudRepository<Claim, Long> {

    Mono<Claim> findByClaimNumber(String claimNumber);

    Flux<Claim> findByOwnerId(Long ownerId);

    Flux<Claim> findByBusinessId(Long businessId);

    Mono<Claim> findByIdAndOwnerId(
            Long id,
            Long ownerId
    );
}

