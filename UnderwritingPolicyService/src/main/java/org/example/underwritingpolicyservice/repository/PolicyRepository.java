package org.example.underwritingpolicyservice.repository;



import org.example.underwritingpolicyservice.entity.Policy;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PolicyRepository
        extends ReactiveCrudRepository<Policy, Long> {

    Mono<Policy> findByPolicyNumber(String policyNumber);

    Mono<Boolean> existsByPolicyNumber(String policyNumber);

    Flux<Policy> findByOwnerId(Long ownerId);

    Flux<Policy> findByBusinessId(Long businessId);
}



