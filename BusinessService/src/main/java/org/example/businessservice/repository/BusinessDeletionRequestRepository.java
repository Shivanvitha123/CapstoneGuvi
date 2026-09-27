package org.example.businessservice.repository;

import org.example.businessservice.entity.BusinessDeletionRequest;
import org.example.businessservice.model.DeletionRequestStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BusinessDeletionRequestRepository
        extends ReactiveCrudRepository<BusinessDeletionRequest, Long> {

    Flux<BusinessDeletionRequest> findByOwnerId(Long ownerId);

    Flux<BusinessDeletionRequest> findByStatus(
            DeletionRequestStatus status
    );

    Mono<Boolean> existsByBusinessIdAndStatus(
            Long businessId,
            DeletionRequestStatus status
    );
}