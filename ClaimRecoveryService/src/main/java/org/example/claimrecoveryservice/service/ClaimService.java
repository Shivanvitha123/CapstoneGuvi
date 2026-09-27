package org.example.claimrecoveryservice.service;


import org.example.claimrecoveryservice.dto.ClaimResponse;
import org.example.claimrecoveryservice.dto.CreateClaimRequest;
import org.example.claimrecoveryservice.dto.UpdateClaimStatusRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClaimService {

    Mono<ClaimResponse> createClaim(
            CreateClaimRequest request,
            Long userId,
            String role
    );

    Flux<ClaimResponse> getOwnerClaims(
            Long userId,
            String role
    );

    Flux<ClaimResponse> getAllClaims(
            Long userId,
            String role
    );

    Mono<ClaimResponse> getClaim(
            Long claimId,
            Long userId,
            String role
    );

    Mono<ClaimResponse> updateClaimStatus(
            Long claimId,
            UpdateClaimStatusRequest request,
            Long userId,
            String role
    );
}
