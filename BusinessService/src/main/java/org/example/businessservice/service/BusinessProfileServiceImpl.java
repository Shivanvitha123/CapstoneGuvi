package org.example.businessservice.service;

import org.example.businessservice.dto.BusinessDeletionRequestResponse;
import org.example.businessservice.dto.BusinessProfileResponse;
import org.example.businessservice.dto.BusinessSummaryResponse;
import org.example.businessservice.dto.CreateBusinessProfileRequest;
import org.example.businessservice.dto.UpdateBusinessProfileRequest;

import org.example.businessservice.entity.BusinessDeletionRequest;
import org.example.businessservice.entity.BusinessProfile;

import org.example.businessservice.exception.BusinessAccessDeniedException;
import org.example.businessservice.exception.BusinessProfileAlreadyExistsException;
import org.example.businessservice.exception.BusinessProfileNotFoundException;

import org.example.businessservice.model.BusinessStatus;
import org.example.businessservice.model.DeletionRequestStatus;

import org.example.businessservice.repository.BusinessDeletionRequestRepository;
import org.example.businessservice.repository.BusinessProfileRepository;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class BusinessProfileServiceImpl
        implements BusinessProfileService {

    private final BusinessProfileRepository repository;
    private final BusinessDeletionRequestRepository deletionRepository;

    public BusinessProfileServiceImpl(
            BusinessProfileRepository repository,
            BusinessDeletionRequestRepository deletionRepository
    ) {
        this.repository = repository;
        this.deletionRepository = deletionRepository;
    }

    @Override
    public Mono<BusinessProfileResponse> createBusinessProfile(
            Long ownerId,
            CreateBusinessProfileRequest request
    ) {
        String registrationNumber =
                request.registrationNumber().trim();

        return repository
                .existsByRegistrationNumber(registrationNumber)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(
                                new BusinessProfileAlreadyExistsException(
                                        "Business with registration number "
                                                + registrationNumber
                                                + " already exists"
                                )
                        );
                    }

                    LocalDateTime now = LocalDateTime.now();

                    BusinessProfile business =
                            BusinessProfile.builder()
                                    .ownerId(ownerId)
                                    .businessName(
                                            request.businessName().trim()
                                    )
                                    .registrationNumber(registrationNumber)
                                    .businessType(request.businessType())
                                    .industry(request.industry().trim())
                                    .address(request.address().trim())
                                    .city(request.city().trim())
                                    .state(request.state().trim())
                                    .postalCode(request.postalCode().trim())
                                    .country(request.country().trim())
                                    .contactEmail(
                                            request.contactEmail()
                                                    .trim()
                                                    .toLowerCase()
                                    )
                                    .contactPhone(
                                            request.contactPhone().trim()
                                    )
                                    .annualRevenue(request.annualRevenue())
                                    .employeeCount(request.employeeCount())
                                    .establishedDate(request.establishedDate())
                                    .status(BusinessStatus.ACTIVE)
                                    .createdAt(now)
                                    .updatedAt(now)
                                    .build();

                    return repository.save(business)
                            .map(this::toResponse);
                });
    }

    @Override
    public Mono<BusinessProfileResponse> getBusinessProfile(
            Long businessId,
            Long userId,
            String role
    ) {
        return repository.findById(businessId)
                .switchIfEmpty(
                        Mono.error(
                                new BusinessProfileNotFoundException(
                                        "Business profile not found: "
                                                + businessId
                                )
                        )
                )
                .flatMap(business -> {
                    if (isPrivilegedRole(role)) {
                        return Mono.just(toResponse(business));
                    }

                    if ("BUSINESS_OWNER".equals(role)
                            && business.getOwnerId().equals(userId)) {
                        return Mono.just(toResponse(business));
                    }

                    return Mono.error(
                            new BusinessAccessDeniedException(
                                    "You do not have access to this business profile"
                            )
                    );
                });
    }

    @Override
    public Flux<BusinessProfileResponse> getMyBusinessProfiles(
            Long ownerId
    ) {
        return repository.findByOwnerId(ownerId)
                .map(this::toResponse);
    }

    @Override
    public Mono<BusinessProfileResponse> updateBusinessProfile(
            Long businessId,
            Long ownerId,
            UpdateBusinessProfileRequest request
    ) {
        return repository.findByIdAndOwnerId(businessId, ownerId)
                .switchIfEmpty(
                        Mono.error(
                                new BusinessAccessDeniedException(
                                        "Business profile not found or you do not own it"
                                )
                        )
                )
                .flatMap(existing -> {
                    existing.setBusinessName(
                            request.businessName().trim()
                    );
                    existing.setBusinessType(request.businessType());
                    existing.setIndustry(request.industry().trim());
                    existing.setAddress(request.address().trim());
                    existing.setCity(request.city().trim());
                    existing.setState(request.state().trim());
                    existing.setPostalCode(request.postalCode().trim());
                    existing.setCountry(request.country().trim());
                    existing.setContactEmail(
                            request.contactEmail()
                                    .trim()
                                    .toLowerCase()
                    );
                    existing.setContactPhone(
                            request.contactPhone().trim()
                    );
                    existing.setAnnualRevenue(request.annualRevenue());
                    existing.setEmployeeCount(request.employeeCount());
                    existing.setEstablishedDate(request.establishedDate());
                    existing.setUpdatedAt(LocalDateTime.now());

                    return repository.save(existing)
                            .map(this::toResponse);
                });
    }

    @Override
    public Flux<BusinessSummaryResponse> getAllBusinessProfiles() {
        return repository.findAll()
                .map(this::toSummaryResponse);
    }

    // Business deletion request workflow

    @Override
    public Mono<BusinessDeletionRequestResponse> requestDeletion(
            Long businessId,
            Long ownerId,
            String reason
    ) {
        return repository.findByIdAndOwnerId(businessId, ownerId)
                .switchIfEmpty(
                        Mono.error(
                                new BusinessAccessDeniedException(
                                        "Business not found or you do not own it"
                                )
                        )
                )
                .flatMap(business ->
                        deletionRepository.existsByBusinessIdAndStatus(
                                businessId,
                                DeletionRequestStatus.PENDING
                        ).flatMap(exists -> {
                            if (exists) {
                                return Mono.error(
                                        new BusinessAccessDeniedException(
                                                "A pending deletion request already exists"
                                        )
                                );
                            }

                            BusinessDeletionRequest request =
                                    BusinessDeletionRequest.builder()
                                            .businessId(businessId)
                                            .ownerId(ownerId)
                                            .reason(
                                                    reason == null
                                                            ? ""
                                                            : reason.trim()
                                            )
                                            .status(
                                                    DeletionRequestStatus.PENDING
                                            )
                                            .createdAt(LocalDateTime.now())
                                            .build();

                            return deletionRepository.save(request)
                                    .map(this::toDeletionResponse);
                        })
                );
    }

    @Override
    public Flux<BusinessDeletionRequestResponse> getMyDeletionRequests(
            Long ownerId
    ) {
        return deletionRepository.findByOwnerId(ownerId)
                .map(this::toDeletionResponse);
    }

    @Override
    public Flux<BusinessDeletionRequestResponse> getPendingDeletionRequests(
            String role
    ) {
        if (!isDeletionReviewer(role)) {
            return Flux.error(
                    new BusinessAccessDeniedException(
                            "Only underwriters or admins can review deletion requests"
                    )
            );
        }

        return deletionRepository.findByStatus(
                        DeletionRequestStatus.PENDING
                )
                .map(this::toDeletionResponse);
    }

    @Override
    public Mono<BusinessDeletionRequestResponse> reviewDeletionRequest(
            Long requestId,
            Long reviewerId,
            String role,
            boolean approve
    ) {
        if (!isDeletionReviewer(role)) {
            return Mono.error(
                    new BusinessAccessDeniedException(
                            "Only underwriters or admins can review deletion requests"
                    )
            );
        }

        return deletionRepository.findById(requestId)
                .switchIfEmpty(
                        Mono.error(
                                new BusinessProfileNotFoundException(
                                        "Deletion request not found: "
                                                + requestId
                                )
                        )
                )
                .flatMap(request -> {
                    if (request.getStatus()
                            != DeletionRequestStatus.PENDING) {
                        return Mono.error(
                                new BusinessAccessDeniedException(
                                        "Deletion request has already been reviewed"
                                )
                        );
                    }

                    if (request.getOwnerId().equals(reviewerId)) {
                        return Mono.error(
                                new BusinessAccessDeniedException(
                                        "You cannot review your own deletion request"
                                )
                        );
                    }

                    request.setStatus(
                            approve
                                    ? DeletionRequestStatus.APPROVED
                                    : DeletionRequestStatus.REJECTED
                    );
                    request.setReviewerId(reviewerId);
                    request.setReviewedAt(LocalDateTime.now());

                    return deletionRepository.save(request)
                            .flatMap(saved -> {
                                if (!approve) {
                                    return Mono.just(
                                            toDeletionResponse(saved)
                                    );
                                }

                                return repository.deleteById(
                                                saved.getBusinessId()
                                        )
                                        .thenReturn(
                                                toDeletionResponse(saved)
                                        );
                            });
                });
    }

    private boolean isDeletionReviewer(String role) {
        return "UNDERWRITER".equals(role)
                || "ADMIN".equals(role);
    }

    private boolean isPrivilegedRole(String role) {
        return "ADMIN".equals(role)
                || "UNDERWRITER".equals(role)
                || "RISK_ENGINEER".equals(role);
    }

    private BusinessDeletionRequestResponse toDeletionResponse(
            BusinessDeletionRequest request
    ) {
        return new BusinessDeletionRequestResponse(
                request.getId(),
                request.getBusinessId(),
                request.getOwnerId(),
                request.getReviewerId(),
                request.getReason(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getReviewedAt()
        );
    }

    private BusinessProfileResponse toResponse(
            BusinessProfile business
    ) {
        return new BusinessProfileResponse(
                business.getId(),
                business.getOwnerId(),
                business.getBusinessName(),
                business.getRegistrationNumber(),
                business.getBusinessType(),
                business.getIndustry(),
                business.getAddress(),
                business.getCity(),
                business.getState(),
                business.getPostalCode(),
                business.getCountry(),
                business.getContactEmail(),
                business.getContactPhone(),
                business.getAnnualRevenue(),
                business.getEmployeeCount(),
                business.getEstablishedDate(),
                business.getStatus(),
                business.getCreatedAt(),
                business.getUpdatedAt()
        );
    }

    private BusinessSummaryResponse toSummaryResponse(
            BusinessProfile business
    ) {
        return new BusinessSummaryResponse(
                business.getId(),
                business.getOwnerId(),
                business.getBusinessName(),
                business.getRegistrationNumber(),
                business.getBusinessType(),
                business.getIndustry(),
                business.getStatus()
        );
    }
}