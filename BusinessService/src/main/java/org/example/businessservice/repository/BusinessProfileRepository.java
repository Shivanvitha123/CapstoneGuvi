package org.example.businessservice.repository;


import org.example.businessservice.entity.BusinessProfile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BusinessProfileRepository
        extends ReactiveCrudRepository<BusinessProfile, Long> {

    Mono<BusinessProfile> findByRegistrationNumber(
            String registrationNumber
    );

    Mono<Boolean> existsByRegistrationNumber(
            String registrationNumber
    );

    Flux<BusinessProfile> findByOwnerId(Long ownerId);

    @Query("""
            SELECT *
            FROM business_profiles
            WHERE id = :businessId
              AND owner_id = :ownerId
            """)
    Mono<BusinessProfile> findByIdAndOwnerId(
            Long businessId,
            Long ownerId
    );
}

