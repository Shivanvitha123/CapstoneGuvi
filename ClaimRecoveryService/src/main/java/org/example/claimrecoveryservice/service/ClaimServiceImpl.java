package org.example.claimrecoveryservice.service;

import lombok.RequiredArgsConstructor;

import org.example.claimrecoveryservice.dto.ClaimResponse;
import org.example.claimrecoveryservice.dto.CreateClaimRequest;
import org.example.claimrecoveryservice.dto.UpdateClaimStatusRequest;
import org.example.claimrecoveryservice.entity.Claim;
import org.example.claimrecoveryservice.exception.ClaimAccessDeniedException;
import org.example.claimrecoveryservice.exception.ClaimNotFoundException;
import org.example.claimrecoveryservice.model.ClaimStatus;
import org.example.claimrecoveryservice.repository.ClaimRepository;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository repository;

    // =====================================================
    // CREATE CLAIM
    // =====================================================

    @Override
    public Mono<ClaimResponse> createClaim(
            CreateClaimRequest request,
            Long userId,
            String role) {

        if (!"BUSINESS_OWNER".equals(role)
                && !"ADMIN".equals(role)) {

            return Mono.error(
                    new ClaimAccessDeniedException(
                            "Only BUSINESS_OWNER or ADMIN can create a claim"
                    )
            );
        }

        return repository
                .findByClaimNumber(request.claimNumber())
                .flatMap(existing ->
                        Mono.<ClaimResponse>error(
                                new ClaimAccessDeniedException(
                                        "Claim number already exists"
                                )
                        )
                )
                .switchIfEmpty(
                        Mono.defer(() -> {

                            LocalDateTime now = LocalDateTime.now();

                            Claim claim = Claim.builder()
                                    .businessId(request.businessId())
                                    .policyId(request.policyId())
                                    .ownerId(userId)
                                    .claimNumber(request.claimNumber())
                                    .claimType(request.claimType())
                                    .incidentDate(request.incidentDate())
                                    .reportedDate(request.reportedDate())
                                    .claimedAmount(request.claimedAmount())
                                    .description(request.description())
                                    .status(ClaimStatus.SUBMITTED)
                                    .createdAt(now)
                                    .updatedAt(now)
                                    .build();

                            return repository
                                    .save(claim)
                                    .map(this::toResponse);
                        })
                );
    }

    // =====================================================
    // GET OWNER CLAIMS
    // =====================================================

    @Override
    public Flux<ClaimResponse> getOwnerClaims(
            Long userId,
            String role) {

        if (!"BUSINESS_OWNER".equals(role)) {

            return Flux.error(
                    new ClaimAccessDeniedException(
                            "Only BUSINESS_OWNER can access owner claims"
                    )
            );
        }

        return repository
                .findByOwnerId(userId)
                .map(this::toResponse);
    }

    // =====================================================
    // GET ALL CLAIMS
    // =====================================================

    @Override
    public Flux<ClaimResponse> getAllClaims(
            Long userId,
            String role) {

        if (!isStaff(role)) {

            return Flux.error(
                    new ClaimAccessDeniedException(
                            "You do not have permission to view all claims"
                    )
            );
        }

        return repository
                .findAll()
                .map(this::toResponse);
    }

    // =====================================================
    // GET CLAIM BY ID
    // =====================================================

    @Override
    public Mono<ClaimResponse> getClaim(
            Long claimId,
            Long userId,
            String role) {

        return repository
                .findById(claimId)
                .switchIfEmpty(
                        Mono.error(
                                new ClaimNotFoundException(claimId)
                        )
                )
                .flatMap(claim -> {

                    // Staff can view all claims.
                    if (isStaff(role)) {
                        return Mono.just(toResponse(claim));
                    }

                    // Business owners can view only their own claims.
                    if ("BUSINESS_OWNER".equals(role)
                            && userId != null
                            && userId.equals(claim.getOwnerId())) {

                        return Mono.just(toResponse(claim));
                    }

                    return Mono.error(
                            new ClaimAccessDeniedException(
                                    "You cannot access this claim"
                            )
                    );
                });
    }

    // =====================================================
    // UPDATE CLAIM STATUS
    // =====================================================

    @Override
    public Mono<ClaimResponse> updateClaimStatus(
            Long claimId,
            UpdateClaimStatusRequest request,
            Long userId,
            String role) {

        // Only claims adjusters and admins can update status.
        if (!"CLAIMS_ADJUSTER".equals(role)
                && !"ADMIN".equals(role)) {

            return Mono.error(
                    new ClaimAccessDeniedException(
                            "Only CLAIMS_ADJUSTER or ADMIN can change claim status"
                    )
            );
        }

        return repository
                .findById(claimId)
                .switchIfEmpty(
                        Mono.error(
                                new ClaimNotFoundException(claimId)
                        )
                )
                .flatMap(claim -> {

                    claim.setStatus(request.status());

                    if (request.approvedAmount() != null) {
                        claim.setApprovedAmount(
                                request.approvedAmount()
                        );
                    }

                    if (request.assignedAdjusterId() != null) {
                        claim.setAssignedAdjusterId(
                                request.assignedAdjusterId()
                        );
                    }

                    claim.setUpdatedAt(LocalDateTime.now());

                    return repository
                            .save(claim)
                            .map(this::toResponse);
                });
    }

    // =====================================================
    // STAFF ROLE CHECK
    // =====================================================

    private boolean isStaff(String role) {

        return "ADMIN".equals(role)
                || "CLAIMS_ADJUSTER".equals(role)
                || "RISK_ENGINEER".equals(role)
                || "UNDERWRITER".equals(role);
    }

    // =====================================================
    // ENTITY TO RESPONSE
    // =====================================================

    private ClaimResponse toResponse(Claim claim) {

        return new ClaimResponse(
                claim.getId(),
                claim.getBusinessId(),
                claim.getPolicyId(),
                claim.getOwnerId(),
                claim.getClaimNumber(),
                claim.getClaimType(),
                claim.getIncidentDate(),
                claim.getReportedDate(),
                claim.getClaimedAmount(),
                claim.getApprovedAmount(),
                claim.getDescription(),
                claim.getStatus(),
                claim.getAssignedAdjusterId(),
                claim.getCreatedAt(),
                claim.getUpdatedAt()
        );
    }
}