package org.example.riskintelligenceservice.repository;


import org.example.riskintelligenceservice.entity.RiskAssessment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RiskAssessmentRepository
        extends ReactiveCrudRepository<RiskAssessment, Long> {

    Flux<RiskAssessment> findByBusinessId(Long businessId);

    Flux<RiskAssessment> findByOwnerId(Long ownerId);

    Mono<RiskAssessment> findByIdAndOwnerId(
            Long id,
            Long ownerId
    );
}

