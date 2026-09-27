package org.example.claimrecoveryservice.service;

import lombok.RequiredArgsConstructor;

import org.example.claimrecoveryservice.dto.CreateRecoveryRequest;
import org.example.claimrecoveryservice.dto.RecoveryResponse;
import org.example.claimrecoveryservice.dto.UpdateRecoveryStatusRequest;
import org.example.claimrecoveryservice.entity.Recovery;
import org.example.claimrecoveryservice.exception.ClaimAccessDeniedException;
import org.example.claimrecoveryservice.exception.ClaimNotFoundException;
import org.example.claimrecoveryservice.exception.RecoveryNotFoundException;
import org.example.claimrecoveryservice.model.RecoveryStatus;
import org.example.claimrecoveryservice.repository.ClaimRepository;
import org.example.claimrecoveryservice.repository.RecoveryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecoveryServiceImpl
        implements RecoveryService {

    private final RecoveryRepository recoveryRepository;
    private final ClaimRepository claimRepository;

    @Override
    public Mono<RecoveryResponse> createRecovery(
            Long claimId,
            CreateRecoveryRequest request,
            Long userId,
            String role) {

        if (!"CLAIMS_ADJUSTER".equals(role)
                && !"ADMIN".equals(role)) {

            return Mono.error(
                    new ClaimAccessDeniedException(
                            "Only CLAIMS_ADJUSTER or ADMIN can create recovery records"
                    )
            );
        }

        return claimRepository
                .findById(claimId)
                .switchIfEmpty(
                        Mono.error(
                                new ClaimNotFoundException(
                                        claimId
                                )
                        )
                )
                .flatMap(claim -> {

                    LocalDateTime now =
                            LocalDateTime.now();

                    Recovery recovery =
                            Recovery.builder()
                                    .claimId(claimId)
                                    .ownerId(
                                            claim.getOwnerId()
                                    )
                                    .recoveryAmount(
                                            request.recoveryAmount()
                                    )
                                    .recoverySource(
                                            request.recoverySource()
                                    )
                                    .description(
                                            request.description()
                                    )
                                    .status(
                                            RecoveryStatus.INITIATED
                                    )
                                    .processedBy(userId)
                                    .createdAt(now)
                                    .updatedAt(now)
                                    .build();

                    return recoveryRepository
                            .save(recovery)
                            .map(this::toResponse);
                });
    }

    @Override
    public Flux<RecoveryResponse> getClaimRecoveries(
            Long claimId,
            Long userId,
            String role) {

        return claimRepository
                .findById(claimId)
                .switchIfEmpty(
                        Mono.error(
                                new ClaimNotFoundException(
                                        claimId
                                )
                        )
                )
                .flatMapMany(claim -> {

                    if (isStaff(role)) {

                        return recoveryRepository
                                .findByClaimId(claimId)
                                .map(this::toResponse);
                    }

                    if ("BUSINESS_OWNER".equals(role)
                            && userId.equals(
                            claim.getOwnerId())) {

                        return recoveryRepository
                                .findByClaimId(claimId)
                                .map(this::toResponse);
                    }

                    return Flux.error(
                            new ClaimAccessDeniedException(
                                    "You cannot access recovery records for this claim"
                            )
                    );
                });
    }

    @Override
    public Flux<RecoveryResponse> getAllRecoveries(
            Long userId,
            String role) {

        if (!isStaff(role)) {

            return Flux.error(
                    new ClaimAccessDeniedException(
                            "You do not have permission to view all recovery records"
                    )
            );
        }

        return recoveryRepository
                .findAll()
                .map(this::toResponse);
    }

    @Override
    public Mono<RecoveryResponse> updateRecoveryStatus(
            Long recoveryId,
            UpdateRecoveryStatusRequest request,
            Long userId,
            String role) {

        if (!"CLAIMS_ADJUSTER".equals(role)
                && !"ADMIN".equals(role)) {

            return Mono.error(
                    new ClaimAccessDeniedException(
                            "Only CLAIMS_ADJUSTER or ADMIN can update recovery status"
                    )
            );
        }

        return recoveryRepository
                .findById(recoveryId)
                .switchIfEmpty(
                        Mono.error(
                                new RecoveryNotFoundException(
                                        recoveryId
                                )
                        )
                )
                .flatMap(recovery -> {

                    recovery.setStatus(
                            request.status()
                    );

                    recovery.setProcessedBy(userId);

                    recovery.setUpdatedAt(
                            LocalDateTime.now()
                    );

                    return recoveryRepository
                            .save(recovery)
                            .map(this::toResponse);
                });
    }

    private boolean isStaff(String role) {

        return "ADMIN".equals(role)
                || "CLAIMS_ADJUSTER".equals(role);
    }

    private RecoveryResponse toResponse(
            Recovery recovery) {

        return new RecoveryResponse(
                recovery.getId(),
                recovery.getClaimId(),
                recovery.getOwnerId(),
                recovery.getRecoveryAmount(),
                recovery.getRecoverySource(),
                recovery.getDescription(),
                recovery.getStatus(),
                recovery.getProcessedBy(),
                recovery.getCreatedAt(),
                recovery.getUpdatedAt()
        );
    }
}



