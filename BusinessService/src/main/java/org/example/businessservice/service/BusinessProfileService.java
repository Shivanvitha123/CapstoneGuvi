package org.example.businessservice.service;

import org.example.businessservice.dto.BusinessDeletionRequestResponse;
import org.example.businessservice.dto.BusinessProfileResponse;
import org.example.businessservice.dto.BusinessSummaryResponse;
import org.example.businessservice.dto.CreateBusinessProfileRequest;
import org.example.businessservice.dto.UpdateBusinessProfileRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BusinessProfileService {

    Mono<BusinessProfileResponse> createBusinessProfile(
            Long ownerId,
            CreateBusinessProfileRequest request
    );

    Mono<BusinessProfileResponse> getBusinessProfile(
            Long businessId,
            Long userId,
            String role
    );

    Flux<BusinessProfileResponse> getMyBusinessProfiles(
            Long ownerId
    );

    Mono<BusinessProfileResponse> updateBusinessProfile(
            Long businessId,
            Long ownerId,
            UpdateBusinessProfileRequest request
    );

    Flux<BusinessSummaryResponse> getAllBusinessProfiles();

    // Business deletion request methods

    Mono<BusinessDeletionRequestResponse> requestDeletion(
            Long businessId,
            Long ownerId,
            String reason
    );

    Flux<BusinessDeletionRequestResponse> getMyDeletionRequests(
            Long ownerId
    );

    Flux<BusinessDeletionRequestResponse> getPendingDeletionRequests(
            String role
    );

    Mono<BusinessDeletionRequestResponse> reviewDeletionRequest(
            Long requestId,
            Long reviewerId,
            String role,
            boolean approve
    );
}