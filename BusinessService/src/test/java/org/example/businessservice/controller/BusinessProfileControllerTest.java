package org.example.businessservice.controller;

import org.example.businessservice.dto.*;
import org.example.businessservice.service.BusinessProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

class BusinessProfileControllerTest {

    private BusinessProfileService service;
    private BusinessProfileController controller;

    @BeforeEach
    void setUp() {
        service = mock(BusinessProfileService.class);
        controller = new BusinessProfileController(service);
    }

    private Authentication auth(String id, String role) {
        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn(id);

        doReturn(List.of(
                new SimpleGrantedAuthority("ROLE_" + role)
        )).when(authentication).getAuthorities();

        return authentication;
    }

    private BusinessProfileResponse profile() {
        return new BusinessProfileResponse(
                1L, 10L, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null
        );
    }

    private BusinessDeletionRequestResponse deletionResponse() {
        return new BusinessDeletionRequestResponse(
                1L, 2L, 10L, null,
                "No longer operating", null, null, null
        );
    }

    @Test
    void createBusinessProfileShouldReturnCreatedProfile() {
        Authentication authentication = auth("10", "BUSINESS_OWNER");
        CreateBusinessProfileRequest request = mock(CreateBusinessProfileRequest.class);
        BusinessProfileResponse response = profile();

        when(service.createBusinessProfile(10L, request))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.createBusinessProfile(request, authentication))
                .expectNext(response)
                .verifyComplete();

        verify(service).createBusinessProfile(10L, request);
    }

    @Test
    void getMyBusinessProfilesShouldReturnProfiles() {
        Authentication authentication = auth("10", "BUSINESS_OWNER");
        BusinessProfileResponse response = profile();

        when(service.getMyBusinessProfiles(10L))
                .thenReturn(Flux.just(response));

        StepVerifier.create(controller.getMyBusinessProfiles(authentication))
                .expectNext(response)
                .verifyComplete();

        verify(service).getMyBusinessProfiles(10L);
    }

    @Test
    void getBusinessProfileShouldPassUserIdAndRole() {
        Authentication authentication = auth("10", "UNDERWRITER");
        BusinessProfileResponse response = profile();

        when(service.getBusinessProfile(1L, 10L, "UNDERWRITER"))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.getBusinessProfile(1L, authentication))
                .expectNext(response)
                .verifyComplete();

        verify(service).getBusinessProfile(1L, 10L, "UNDERWRITER");
    }

    @Test
    void updateBusinessProfileShouldReturnUpdatedProfile() {
        Authentication authentication = auth("10", "BUSINESS_OWNER");
        UpdateBusinessProfileRequest request = mock(UpdateBusinessProfileRequest.class);
        BusinessProfileResponse response = profile();

        when(service.updateBusinessProfile(1L, 10L, request))
                .thenReturn(Mono.just(response));

        StepVerifier.create(
                controller.updateBusinessProfile(1L, request, authentication)
        ).expectNext(response).verifyComplete();

        verify(service).updateBusinessProfile(1L, 10L, request);
    }

    @Test
    void getAllBusinessProfilesShouldReturnSummaries() {
        Authentication authentication = auth("10", "ADMIN");

        when(service.getAllBusinessProfiles())
                .thenReturn(Flux.empty());

        StepVerifier.create(controller.getAllBusinessProfiles(authentication))
                .verifyComplete();

        verify(service).getAllBusinessProfiles();
    }

    @Test
    void requestDeletionShouldPassReasonAndOwnerId() {
        Authentication authentication = auth("10", "BUSINESS_OWNER");
        BusinessProfileController.DeletionReason request =
                new BusinessProfileController.DeletionReason("No longer operating");
        BusinessDeletionRequestResponse response = deletionResponse();

        when(service.requestDeletion(2L, 10L, "No longer operating"))
                .thenReturn(Mono.just(response));

        StepVerifier.create(
                controller.requestDeletion(2L, request, authentication)
        ).expectNext(response).verifyComplete();

        verify(service).requestDeletion(2L, 10L, "No longer operating");
    }

    @Test
    void requestDeletionShouldHandleMissingRequestBody() {
        Authentication authentication = auth("10", "BUSINESS_OWNER");
        BusinessDeletionRequestResponse response = deletionResponse();

        when(service.requestDeletion(2L, 10L, ""))
                .thenReturn(Mono.just(response));

        StepVerifier.create(
                controller.requestDeletion(2L, null, authentication)
        ).expectNext(response).verifyComplete();

        verify(service).requestDeletion(2L, 10L, "");
    }

    @Test
    void getMyDeletionRequestsShouldReturnRequests() {
        Authentication authentication = auth("10", "BUSINESS_OWNER");
        BusinessDeletionRequestResponse response = deletionResponse();

        when(service.getMyDeletionRequests(10L))
                .thenReturn(Flux.just(response));

        StepVerifier.create(controller.getMyDeletionRequests(authentication))
                .expectNext(response)
                .verifyComplete();

        verify(service).getMyDeletionRequests(10L);
    }

    @Test
    void getPendingDeletionRequestsShouldPassRole() {
        Authentication authentication = auth("20", "UNDERWRITER");
        BusinessDeletionRequestResponse response = deletionResponse();

        when(service.getPendingDeletionRequests("UNDERWRITER"))
                .thenReturn(Flux.just(response));

        StepVerifier.create(
                controller.getPendingDeletionRequests(authentication)
        ).expectNext(response).verifyComplete();

        verify(service).getPendingDeletionRequests("UNDERWRITER");
    }

    @Test
    void approveDeletionRequestShouldPassApprovalAndReviewer() {
        Authentication authentication = auth("20", "UNDERWRITER");
        BusinessDeletionRequestResponse response = deletionResponse();

        when(service.reviewDeletionRequest(5L, 20L, "UNDERWRITER", true))
                .thenReturn(Mono.just(response));

        StepVerifier.create(
                controller.approveDeletionRequest(5L, authentication)
        ).expectNext(response).verifyComplete();

        verify(service).reviewDeletionRequest(5L, 20L, "UNDERWRITER", true);
    }

    @Test
    void rejectDeletionRequestShouldPassRejectionAndReviewer() {
        Authentication authentication = auth("20", "UNDERWRITER");
        BusinessDeletionRequestResponse response = deletionResponse();

        when(service.reviewDeletionRequest(5L, 20L, "UNDERWRITER", false))
                .thenReturn(Mono.just(response));

        StepVerifier.create(
                controller.rejectDeletionRequest(5L, authentication)
        ).expectNext(response).verifyComplete();

        verify(service).reviewDeletionRequest(5L, 20L, "UNDERWRITER", false);
    }
}