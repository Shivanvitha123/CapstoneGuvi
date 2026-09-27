package org.example.underwritingpolicyservice.service;

import lombok.RequiredArgsConstructor;
import org.example.underwritingpolicyservice.dto.CreatePolicyRequest;
import org.example.underwritingpolicyservice.dto.PolicyResponse;
import org.example.underwritingpolicyservice.dto.UpdatePolicyRequest;
import org.example.underwritingpolicyservice.entity.Policy;
import org.example.underwritingpolicyservice.exception.PolicyAccessDeniedException;
import org.example.underwritingpolicyservice.exception.PolicyAlreadyExistsException;
import org.example.underwritingpolicyservice.exception.PolicyNotFoundException;
import org.example.underwritingpolicyservice.model.PolicyStatus;
import org.example.underwritingpolicyservice.repository.PolicyRepository;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;

    @Override
    public Mono<PolicyResponse> createPolicy(
            CreatePolicyRequest request,
            Long userId,
            String role) {

        if (!isRole(role, "BUSINESS_OWNER")) {
            return Mono.error(
                    new PolicyAccessDeniedException(
                            "Only BUSINESS_OWNER can create policies"
                    )
            );
        }

        return policyRepository
                .findByPolicyNumber(request.policyNumber())
                .flatMap(existing ->
                        Mono.<PolicyResponse>error(
                                new PolicyAlreadyExistsException(
                                        "Policy number already exists: "
                                                + request.policyNumber()
                                )
                        )
                )
                .switchIfEmpty(
                        Mono.defer(() -> {
                            LocalDateTime now = LocalDateTime.now();

                            Policy policy = Policy.builder()
                                    .businessId(request.businessId())
                                    .ownerId(userId)
                                    .policyNumber(request.policyNumber())
                                    .policyType(request.policyType())
                                    .coverageAmount(request.coverageAmount())
                                    .premiumAmount(request.premiumAmount())
                                    .startDate(request.startDate())
                                    .endDate(request.endDate())
                                    .status(PolicyStatus.DRAFT.name())
                                    .description(request.description())
                                    .createdAt(now)
                                    .updatedAt(now)
                                    .build();

                            return policyRepository
                                    .save(policy)
                                    .map(this::toResponse);
                        })
                );
    }

    @Override
    public Mono<PolicyResponse> getPolicy(
            Long policyId,
            Long userId,
            String role) {

        return policyRepository
                .findById(policyId)
                .switchIfEmpty(
                        Mono.error(
                                new PolicyNotFoundException(
                                        "Policy not found: " + policyId
                                )
                        )
                )
                .flatMap(policy -> {

                    // Admin, Underwriter, Risk Engineer, and
                    // Claims Adjuster can view any policy.
                    if (isPrivileged(role)) {
                        return Mono.just(toResponse(policy));
                    }

                    // Business Owners can view only their own policies.
                    if (isRole(role, "BUSINESS_OWNER")
                            && userId != null
                            && userId.equals(policy.getOwnerId())) {
                        return Mono.just(toResponse(policy));
                    }

                    return Mono.error(
                            new PolicyAccessDeniedException(
                                    "You do not have access to this policy"
                            )
                    );
                });
    }

    @Override
    public Flux<PolicyResponse> getOwnerPolicies(
            Long userId,
            String role) {

        if (!isRole(role, "BUSINESS_OWNER")) {
            return Flux.error(
                    new PolicyAccessDeniedException(
                            "Only BUSINESS_OWNER can view owner policies"
                    )
            );
        }

        return policyRepository
                .findByOwnerId(userId)
                .map(this::toResponse);
    }

    @Override
    public Flux<PolicyResponse> getAllPolicies(
            Long userId,
            String role) {

        if (!isPrivileged(role)) {
            return Flux.error(
                    new PolicyAccessDeniedException(
                            "You do not have permission to view all policies"
                    )
            );
        }

        return policyRepository
                .findAll()
                .map(this::toResponse);
    }

    @Override
    public Mono<PolicyResponse> updatePolicy(
            Long policyId,
            UpdatePolicyRequest request,
            Long userId,
            String role) {

        if (!isRole(role, "BUSINESS_OWNER")) {
            return Mono.error(
                    new PolicyAccessDeniedException(
                            "Only BUSINESS_OWNER can update policies"
                    )
            );
        }

        return policyRepository
                .findById(policyId)
                .switchIfEmpty(
                        Mono.error(
                                new PolicyNotFoundException(
                                        "Policy not found: " + policyId
                                )
                        )
                )
                .flatMap(policy -> {

                    if (userId == null
                            || !userId.equals(policy.getOwnerId())) {
                        return Mono.error(
                                new PolicyAccessDeniedException(
                                        "You cannot update another owner's policy"
                                )
                        );
                    }

                    if (!PolicyStatus.DRAFT.name()
                            .equals(policy.getStatus())) {
                        return Mono.error(
                                new PolicyAccessDeniedException(
                                        "Only DRAFT policies can be updated"
                                )
                        );
                    }

                    if (request.businessId() != null) {
                        policy.setBusinessId(request.businessId());
                    }

                    if (request.policyNumber() != null
                            && !request.policyNumber().isBlank()) {
                        policy.setPolicyNumber(request.policyNumber());
                    }

                    if (request.policyType() != null
                            && !request.policyType().isBlank()) {
                        policy.setPolicyType(request.policyType());
                    }

                    if (request.coverageAmount() != null) {
                        policy.setCoverageAmount(
                                request.coverageAmount()
                        );
                    }

                    if (request.premiumAmount() != null) {
                        policy.setPremiumAmount(
                                request.premiumAmount()
                        );
                    }

                    if (request.startDate() != null) {
                        policy.setStartDate(request.startDate());
                    }

                    if (request.endDate() != null) {
                        policy.setEndDate(request.endDate());
                    }

                    if (request.description() != null) {
                        policy.setDescription(request.description());
                    }

                    policy.setUpdatedAt(LocalDateTime.now());

                    return policyRepository
                            .save(policy)
                            .map(this::toResponse);
                });
    }

    @Override
    public Mono<PolicyResponse> submitPolicy(
            Long policyId,
            Long userId,
            String role) {

        if (!isRole(role, "BUSINESS_OWNER")) {
            return Mono.error(
                    new PolicyAccessDeniedException(
                            "Only BUSINESS_OWNER can submit policies"
                    )
            );
        }

        return policyRepository
                .findById(policyId)
                .switchIfEmpty(
                        Mono.error(
                                new PolicyNotFoundException(
                                        "Policy not found: " + policyId
                                )
                        )
                )
                .flatMap(policy -> {

                    if (userId == null
                            || !userId.equals(policy.getOwnerId())) {
                        return Mono.error(
                                new PolicyAccessDeniedException(
                                        "You cannot submit another owner's policy"
                                )
                        );
                    }

                    if (!PolicyStatus.DRAFT.name()
                            .equals(policy.getStatus())) {
                        return Mono.error(
                                new PolicyAccessDeniedException(
                                        "Only DRAFT policies can be submitted"
                                )
                        );
                    }

                    policy.setStatus(PolicyStatus.SUBMITTED.name());
                    policy.setUpdatedAt(LocalDateTime.now());

                    return policyRepository
                            .save(policy)
                            .map(this::toResponse);
                });
    }

    @Override
    public Mono<PolicyResponse> updatePolicyStatus(
            Long policyId,
            PolicyStatus status,
            Long userId,
            String role) {

        // Business Owners cannot directly change policy status.
        if (isRole(role, "BUSINESS_OWNER")) {
            return Mono.error(
                    new PolicyAccessDeniedException(
                            "BUSINESS_OWNER cannot change policy status"
                    )
            );
        }

        // Only Underwriters and Admins can change policy status.
        if (!isRole(role, "UNDERWRITER")
                && !isRole(role, "ADMIN")) {
            return Mono.error(
                    new PolicyAccessDeniedException(
                            "Only UNDERWRITER or ADMIN can change policy status"
                    )
            );
        }

        return policyRepository
                .findById(policyId)
                .switchIfEmpty(
                        Mono.error(
                                new PolicyNotFoundException(
                                        "Policy not found: " + policyId
                                )
                        )
                )
                .flatMap(policy -> {

                    if (PolicyStatus.DRAFT.name()
                            .equals(policy.getStatus())) {
                        return Mono.error(
                                new PolicyAccessDeniedException(
                                        "Policy must be submitted before status can be changed"
                                )
                        );
                    }

                    policy.setStatus(status.name());
                    policy.setUpdatedAt(LocalDateTime.now());

                    return policyRepository
                            .save(policy)
                            .map(this::toResponse);
                });
    }

    /**
     * Roles allowed to view all policies and individual policy details.
     */
    private boolean isPrivileged(String role) {
        return isRole(role, "ADMIN")
                || isRole(role, "UNDERWRITER")
                || isRole(role, "RISK_ENGINEER")
                || isRole(role, "CLAIMS_ADJUSTER");
    }

    private boolean isRole(
            String actualRole,
            String expectedRole) {

        return actualRole != null
                && expectedRole.equalsIgnoreCase(
                actualRole.replaceFirst("^ROLE_", "")
        );
    }

    private PolicyResponse toResponse(Policy policy) {
        return new PolicyResponse(
                policy.getId(),
                policy.getBusinessId(),
                policy.getOwnerId(),
                policy.getPolicyNumber(),
                policy.getPolicyType(),
                policy.getCoverageAmount(),
                policy.getPremiumAmount(),
                policy.getStartDate(),
                policy.getEndDate(),
                PolicyStatus.valueOf(policy.getStatus()),
                policy.getDescription(),
                policy.getCreatedAt(),
                policy.getUpdatedAt()
        );
    }
}