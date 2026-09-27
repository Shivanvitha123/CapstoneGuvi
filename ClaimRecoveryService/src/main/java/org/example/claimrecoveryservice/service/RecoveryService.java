package org.example.claimrecoveryservice.service;


import org.example.claimrecoveryservice.dto.CreateRecoveryRequest;
import org.example.claimrecoveryservice.dto.RecoveryResponse;
import org.example.claimrecoveryservice.dto.UpdateRecoveryStatusRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecoveryService {

    Mono<RecoveryResponse> createRecovery(
            Long claimId,
            CreateRecoveryRequest request,
            Long userId,
            String role
    );

    Flux<RecoveryResponse> getClaimRecoveries(
            Long claimId,
            Long userId,
            String role
    );

    Flux<RecoveryResponse> getAllRecoveries(
            Long userId,
            String role
    );

    Mono<RecoveryResponse> updateRecoveryStatus(
            Long recoveryId,
            UpdateRecoveryStatusRequest request,
            Long userId,
            String role
    );
}


