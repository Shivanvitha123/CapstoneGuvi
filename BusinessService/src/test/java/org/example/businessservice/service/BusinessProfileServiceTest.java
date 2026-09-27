package org.example.businessservice.service;

import org.example.businessservice.dto.*;
import org.example.businessservice.entity.BusinessDeletionRequest;
import org.example.businessservice.entity.BusinessProfile;
import org.example.businessservice.exception.BusinessAccessDeniedException;
import org.example.businessservice.exception.BusinessProfileAlreadyExistsException;
import org.example.businessservice.exception.BusinessProfileNotFoundException;
import org.example.businessservice.model.BusinessStatus;
import org.example.businessservice.model.DeletionRequestStatus;
import org.example.businessservice.repository.BusinessDeletionRequestRepository;
import org.example.businessservice.repository.BusinessProfileRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BusinessProfileServiceImplTest {

    private BusinessProfileRepository repository;
    private BusinessDeletionRequestRepository deletionRepository;
    private BusinessProfileServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(BusinessProfileRepository.class);
        deletionRepository = mock(BusinessDeletionRequestRepository.class);

        service = new BusinessProfileServiceImpl(
                repository,
                deletionRepository
        );
    }

    private BusinessProfile business(Long id, Long ownerId) {
        return BusinessProfile.builder()
                .id(id)
                .ownerId(ownerId)
                .businessName("Test Business")
                .registrationNumber("REG123")
                .industry("Technology")
                .address("Test Street")
                .city("Hyderabad")
                .state("Telangana")
                .postalCode("500001")
                .country("India")
                .contactEmail("test@example.com")
                .contactPhone("9999999999")
                .status(BusinessStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private BusinessDeletionRequest deletionRequest(
            Long id,
            Long businessId,
            Long ownerId,
            DeletionRequestStatus status
    ) {
        return BusinessDeletionRequest.builder()
                .id(id)
                .businessId(businessId)
                .ownerId(ownerId)
                .reason("No longer required")
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private CreateBusinessProfileRequest createRequest() {
        CreateBusinessProfileRequest request =
                mock(CreateBusinessProfileRequest.class);

        when(request.registrationNumber()).thenReturn(" REG123 ");
        when(request.businessName()).thenReturn(" Test Business ");
        when(request.industry()).thenReturn(" Technology ");
        when(request.address()).thenReturn(" Test Street ");
        when(request.city()).thenReturn(" Hyderabad ");
        when(request.state()).thenReturn(" Telangana ");
        when(request.postalCode()).thenReturn(" 500001 ");
        when(request.country()).thenReturn(" India ");
        when(request.contactEmail()).thenReturn(" TEST@EXAMPLE.COM ");
        when(request.contactPhone()).thenReturn(" 9999999999 ");

        return request;
    }

    private UpdateBusinessProfileRequest updateRequest() {
        UpdateBusinessProfileRequest request =
                mock(UpdateBusinessProfileRequest.class);

        when(request.businessName()).thenReturn(" Updated Business ");
        when(request.industry()).thenReturn(" Finance ");
        when(request.address()).thenReturn(" New Street ");
        when(request.city()).thenReturn(" Hyderabad ");
        when(request.state()).thenReturn(" Telangana ");
        when(request.postalCode()).thenReturn(" 500002 ");
        when(request.country()).thenReturn(" India ");
        when(request.contactEmail()).thenReturn(" UPDATED@EXAMPLE.COM ");
        when(request.contactPhone()).thenReturn(" 8888888888 ");

        return request;
    }

    // CREATE BUSINESS

    @Test
    void createBusinessProfileSuccessfully() {
        CreateBusinessProfileRequest request = createRequest();

        when(repository.existsByRegistrationNumber("REG123"))
                .thenReturn(Mono.just(false));

        when(repository.save(any(BusinessProfile.class)))
                .thenAnswer(invocation -> {
                    BusinessProfile saved =
                            invocation.getArgument(0);
                    saved.setId(1L);
                    return Mono.just(saved);
                });

        BusinessProfileResponse response =
                service.createBusinessProfile(10L, request).block();

        assertNotNull(response);
        assertEquals(10L, response.ownerId());
        assertEquals("Test Business", response.businessName());
        assertEquals("REG123", response.registrationNumber());
        assertEquals("test@example.com", response.contactEmail());
        assertEquals(BusinessStatus.ACTIVE, response.status());

        verify(repository).existsByRegistrationNumber("REG123");
        verify(repository).save(any(BusinessProfile.class));
    }

    @Test
    void createBusinessProfileWhenRegistrationAlreadyExists() {
        CreateBusinessProfileRequest request = createRequest();

        when(repository.existsByRegistrationNumber("REG123"))
                .thenReturn(Mono.just(true));

        assertThrows(
                BusinessProfileAlreadyExistsException.class,
                () -> service.createBusinessProfile(10L, request).block()
        );

        verify(repository, never()).save(any(BusinessProfile.class));
    }

    // GET BUSINESS

    @Test
    void getBusinessProfileAsAdmin() {
        BusinessProfile business = business(1L, 10L);

        when(repository.findById(1L))
                .thenReturn(Mono.just(business));

        BusinessProfileResponse response =
                service.getBusinessProfile(1L, 99L, "ADMIN").block();

        assertNotNull(response);
        assertEquals(1L, response.id());
        verify(repository).findById(1L);
    }

    @Test
    void getBusinessProfileAsUnderwriter() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(business(1L, 10L)));

        BusinessProfileResponse response =
                service.getBusinessProfile(
                        1L, 99L, "UNDERWRITER"
                ).block();

        assertNotNull(response);
    }

    @Test
    void getBusinessProfileAsRiskEngineer() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(business(1L, 10L)));

        assertNotNull(
                service.getBusinessProfile(
                        1L, 99L, "RISK_ENGINEER"
                ).block()
        );
    }

    @Test
    void getBusinessProfileAsOwner() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(business(1L, 10L)));

        BusinessProfileResponse response =
                service.getBusinessProfile(
                        1L, 10L, "BUSINESS_OWNER"
                ).block();

        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    void denyBusinessProfileAccessToOtherOwner() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(business(1L, 10L)));

        assertThrows(
                BusinessAccessDeniedException.class,
                () -> service.getBusinessProfile(
                        1L, 99L, "BUSINESS_OWNER"
                ).block()
        );
    }

    @Test
    void getBusinessProfileWhenNotFound() {
        when(repository.findById(99L))
                .thenReturn(Mono.empty());

        assertThrows(
                BusinessProfileNotFoundException.class,
                () -> service.getBusinessProfile(
                        99L, 10L, "ADMIN"
                ).block()
        );
    }

    // GET MY BUSINESSES

    @Test
    void getMyBusinessProfilesSuccessfully() {
        when(repository.findByOwnerId(10L))
                .thenReturn(Flux.just(
                        business(1L, 10L),
                        business(2L, 10L)
                ));

        List<BusinessProfileResponse> responses =
                service.getMyBusinessProfiles(10L).collectList().block();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(repository).findByOwnerId(10L);
    }

    @Test
    void getMyBusinessProfilesWhenEmpty() {
        when(repository.findByOwnerId(10L))
                .thenReturn(Flux.empty());

        List<BusinessProfileResponse> responses =
                service.getMyBusinessProfiles(10L).collectList().block();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    // UPDATE BUSINESS

    @Test
    void updateBusinessProfileSuccessfully() {
        BusinessProfile existing = business(1L, 10L);

        when(repository.findByIdAndOwnerId(1L, 10L))
                .thenReturn(Mono.just(existing));

        when(repository.save(any(BusinessProfile.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));

        BusinessProfileResponse response =
                service.updateBusinessProfile(
                        1L, 10L, updateRequest()
                ).block();

        assertNotNull(response);
        assertEquals("Updated Business", response.businessName());
        assertEquals("updated@example.com", response.contactEmail());
        assertEquals("Finance", response.industry());

        verify(repository).save(existing);
    }

    @Test
    void updateBusinessProfileWhenNotOwned() {
        when(repository.findByIdAndOwnerId(1L, 10L))
                .thenReturn(Mono.empty());

        assertThrows(
                BusinessAccessDeniedException.class,
                () -> service.updateBusinessProfile(
                        1L, 10L, updateRequest()
                ).block()
        );

        verify(repository, never()).save(any(BusinessProfile.class));
    }

    // GET ALL BUSINESSES

    @Test
    void getAllBusinessProfilesSuccessfully() {
        when(repository.findAll())
                .thenReturn(Flux.just(
                        business(1L, 10L),
                        business(2L, 20L)
                ));

        List<BusinessSummaryResponse> responses =
                service.getAllBusinessProfiles().collectList().block();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Test Business", responses.get(0).businessName());
    }

    @Test
    void getAllBusinessProfilesWhenEmpty() {
        when(repository.findAll()).thenReturn(Flux.empty());

        List<BusinessSummaryResponse> responses =
                service.getAllBusinessProfiles().collectList().block();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    // REQUEST DELETION

    @Test
    void requestDeletionSuccessfully() {
        when(repository.findByIdAndOwnerId(1L, 10L))
                .thenReturn(Mono.just(business(1L, 10L)));

        when(deletionRepository.existsByBusinessIdAndStatus(
                1L, DeletionRequestStatus.PENDING
        )).thenReturn(Mono.just(false));

        when(deletionRepository.save(any(BusinessDeletionRequest.class)))
                .thenAnswer(invocation -> {
                    BusinessDeletionRequest request =
                            invocation.getArgument(0);
                    request.setId(5L);
                    return Mono.just(request);
                });

        BusinessDeletionRequestResponse response =
                service.requestDeletion(1L, 10L, "  Close business  ")
                        .block();

        assertNotNull(response);
        assertEquals(5L, response.id());
        assertEquals(1L, response.businessId());
        assertEquals(10L, response.ownerId());
        assertEquals("Close business", response.reason());
        assertEquals(
                DeletionRequestStatus.PENDING,
                response.status()
        );

        verify(deletionRepository).save(any(BusinessDeletionRequest.class));
    }

    @Test
    void requestDeletionWithNullReason() {
        when(repository.findByIdAndOwnerId(1L, 10L))
                .thenReturn(Mono.just(business(1L, 10L)));

        when(deletionRepository.existsByBusinessIdAndStatus(
                1L, DeletionRequestStatus.PENDING
        )).thenReturn(Mono.just(false));

        when(deletionRepository.save(any(BusinessDeletionRequest.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));

        BusinessDeletionRequestResponse response =
                service.requestDeletion(1L, 10L, null).block();

        assertNotNull(response);
        assertEquals("", response.reason());
    }

    @Test
    void requestDeletionWhenBusinessNotOwned() {
        when(repository.findByIdAndOwnerId(1L, 10L))
                .thenReturn(Mono.empty());

        assertThrows(
                BusinessAccessDeniedException.class,
                () -> service.requestDeletion(
                        1L, 10L, "Close business"
                ).block()
        );

        verifyNoInteractions(deletionRepository);
    }

    @Test
    void requestDeletionWhenPendingRequestAlreadyExists() {
        when(repository.findByIdAndOwnerId(1L, 10L))
                .thenReturn(Mono.just(business(1L, 10L)));

        when(deletionRepository.existsByBusinessIdAndStatus(
                1L, DeletionRequestStatus.PENDING
        )).thenReturn(Mono.just(true));

        assertThrows(
                BusinessAccessDeniedException.class,
                () -> service.requestDeletion(
                        1L, 10L, "Close business"
                ).block()
        );

        verify(deletionRepository, never())
                .save(any(BusinessDeletionRequest.class));
    }

    // GET DELETION REQUESTS

    @Test
    void getMyDeletionRequestsSuccessfully() {
        when(deletionRepository.findByOwnerId(10L))
                .thenReturn(Flux.just(
                        deletionRequest(
                                1L, 5L, 10L,
                                DeletionRequestStatus.PENDING
                        )
                ));

        List<BusinessDeletionRequestResponse> responses =
                service.getMyDeletionRequests(10L).collectList().block();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).id());
    }

    @Test
    void getPendingDeletionRequestsAsAdmin() {
        when(deletionRepository.findByStatus(
                DeletionRequestStatus.PENDING
        )).thenReturn(Flux.just(
                deletionRequest(
                        1L, 5L, 10L,
                        DeletionRequestStatus.PENDING
                )
        ));

        List<BusinessDeletionRequestResponse> responses =
                service.getPendingDeletionRequests("ADMIN")
                        .collectList().block();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(deletionRepository).findByStatus(
                DeletionRequestStatus.PENDING
        );
    }

    @Test
    void getPendingDeletionRequestsAsUnderwriter() {
        when(deletionRepository.findByStatus(
                DeletionRequestStatus.PENDING
        )).thenReturn(Flux.empty());

        List<BusinessDeletionRequestResponse> responses =
                service.getPendingDeletionRequests("UNDERWRITER")
                        .collectList().block();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void denyPendingDeletionRequestsForBusinessOwner() {
        assertThrows(
                BusinessAccessDeniedException.class,
                () -> service.getPendingDeletionRequests(
                        "BUSINESS_OWNER"
                ).collectList().block()
        );

        verifyNoInteractions(deletionRepository);
    }

    // REVIEW DELETION REQUEST

    @Test
    void approveDeletionRequestSuccessfully() {
        BusinessDeletionRequest request = deletionRequest(
                5L, 1L, 10L, DeletionRequestStatus.PENDING
        );

        when(deletionRepository.findById(5L))
                .thenReturn(Mono.just(request));

        when(deletionRepository.save(any(BusinessDeletionRequest.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));

        when(repository.deleteById(1L))
                .thenReturn(Mono.empty());

        BusinessDeletionRequestResponse response =
                service.reviewDeletionRequest(
                        5L, 20L, "ADMIN", true
                ).block();

        assertNotNull(response);
        assertEquals(DeletionRequestStatus.APPROVED, response.status());
        assertEquals(20L, response.reviewerId());
        assertNotNull(response.reviewedAt());

        verify(deletionRepository).save(request);
        verify(repository).deleteById(1L);
    }

    @Test
    void rejectDeletionRequestSuccessfully() {
        BusinessDeletionRequest request = deletionRequest(
                5L, 1L, 10L, DeletionRequestStatus.PENDING
        );

        when(deletionRepository.findById(5L))
                .thenReturn(Mono.just(request));

        when(deletionRepository.save(any(BusinessDeletionRequest.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));

        BusinessDeletionRequestResponse response =
                service.reviewDeletionRequest(
                        5L, 20L, "UNDERWRITER", false
                ).block();

        assertNotNull(response);
        assertEquals(DeletionRequestStatus.REJECTED, response.status());
        assertEquals(20L, response.reviewerId());
        assertNotNull(response.reviewedAt());

        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void denyDeletionReviewForBusinessOwner() {
        assertThrows(
                BusinessAccessDeniedException.class,
                () -> service.reviewDeletionRequest(
                        5L, 20L, "BUSINESS_OWNER", true
                ).block()
        );

        verifyNoInteractions(deletionRepository);
    }

    @Test
    void reviewDeletionRequestWhenNotFound() {
        when(deletionRepository.findById(99L))
                .thenReturn(Mono.empty());

        assertThrows(
                BusinessProfileNotFoundException.class,
                () -> service.reviewDeletionRequest(
                        99L, 20L, "ADMIN", true
                ).block()
        );
    }

    @Test
    void denyReviewOfAlreadyReviewedRequest() {
        BusinessDeletionRequest request = deletionRequest(
                5L, 1L, 10L, DeletionRequestStatus.APPROVED
        );

        when(deletionRepository.findById(5L))
                .thenReturn(Mono.just(request));

        assertThrows(
                BusinessAccessDeniedException.class,
                () -> service.reviewDeletionRequest(
                        5L, 20L, "ADMIN", true
                ).block()
        );

        verify(deletionRepository, never())
                .save(any(BusinessDeletionRequest.class));
    }

    @Test
    void denyOwnerFromReviewingOwnDeletionRequest() {
        BusinessDeletionRequest request = deletionRequest(
                5L, 1L, 10L, DeletionRequestStatus.PENDING
        );

        when(deletionRepository.findById(5L))
                .thenReturn(Mono.just(request));

        assertThrows(
                BusinessAccessDeniedException.class,
                () -> service.reviewDeletionRequest(
                        5L, 10L, "ADMIN", true
                ).block()
        );

        verify(deletionRepository, never())
                .save(any(BusinessDeletionRequest.class));
    }
}