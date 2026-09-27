package org.example.claimrecoveryservice.service;

import org.example.claimrecoveryservice.dto.ClaimResponse;
import org.example.claimrecoveryservice.dto.CreateClaimRequest;
import org.example.claimrecoveryservice.dto.UpdateClaimStatusRequest;
import org.example.claimrecoveryservice.entity.Claim;
import org.example.claimrecoveryservice.exception.ClaimAccessDeniedException;
import org.example.claimrecoveryservice.exception.ClaimNotFoundException;
import org.example.claimrecoveryservice.model.ClaimStatus;
import org.example.claimrecoveryservice.model.ClaimType;
import org.example.claimrecoveryservice.repository.ClaimRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

    @Mock
    private ClaimRepository repository;

    @InjectMocks
    private ClaimServiceImpl service;

    private Claim claim;
    private CreateClaimRequest request;

    @BeforeEach
    void setUp() {
        request = createRequest();
        claim = createClaim();
    }

    private CreateClaimRequest createRequest() {
        return new CreateClaimRequest(
                10L,
                20L,
                "CLM-001",
                ClaimType.PROPERTY_DAMAGE,
                LocalDate.now(),
                LocalDate.now(),
                new BigDecimal("5000"),
                "Property damage"
        );
    }

    private Claim createClaim() {
        LocalDateTime now = LocalDateTime.now();

        return Claim.builder()
                .id(1L)
                .businessId(10L)
                .policyId(20L)
                .ownerId(100L)
                .claimNumber("CLM-001")
                .claimType(ClaimType.PROPERTY_DAMAGE)
                .incidentDate(LocalDate.now())
                .reportedDate(LocalDate.now())
                .claimedAmount(new BigDecimal("5000"))
                .approvedAmount(new BigDecimal("4000"))
                .description("Property damage")
                .status(ClaimStatus.SUBMITTED)
                .assignedAdjusterId(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    // =====================================================
    // CREATE CLAIM
    // =====================================================

    @Test
    void createClaimSuccessfully() {
        when(repository.findByClaimNumber("CLM-001"))
                .thenReturn(Mono.empty());

        when(repository.save(any(Claim.class)))
                .thenAnswer(invocation -> {
                    Claim saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return Mono.just(saved);
                });

        StepVerifier.create(
                        service.createClaim(
                                request,
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals(10L, response.businessId());
                    assertEquals(20L, response.policyId());
                    assertEquals(100L, response.ownerId());
                    assertEquals("CLM-001", response.claimNumber());
                    assertEquals(
                            ClaimType.PROPERTY_DAMAGE,
                            response.claimType()
                    );
                    assertEquals(
                            ClaimStatus.SUBMITTED,
                            response.status()
                    );
                    assertEquals(
                            new BigDecimal("5000"),
                            response.claimedAmount()
                    );
                    assertEquals("Property damage", response.description());
                    assertNotNull(response.createdAt());
                    assertNotNull(response.updatedAt());
                })
                .verifyComplete();

        verify(repository).findByClaimNumber("CLM-001");
        verify(repository).save(any(Claim.class));
    }

    @Test
    void createClaimAsAdminSuccessfully() {
        when(repository.findByClaimNumber("CLM-001"))
                .thenReturn(Mono.empty());

        when(repository.save(any(Claim.class)))
                .thenAnswer(invocation -> {
                    Claim saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return Mono.just(saved);
                });

        StepVerifier.create(
                        service.createClaim(request, 1L, "ADMIN")
                )
                .assertNext(response ->
                        assertEquals("CLM-001", response.claimNumber())
                )
                .verifyComplete();

        verify(repository).save(any(Claim.class));
    }

    @Test
    void createClaimDeniedForUnderwriter() {
        StepVerifier.create(
                        service.createClaim(request, 100L, "UNDERWRITER")
                )
                .expectError(ClaimAccessDeniedException.class)
                .verify();

        verifyNoInteractions(repository);
    }

    @Test
    void createClaimDeniedForNullRole() {
        StepVerifier.create(
                        service.createClaim(request, 100L, null)
                )
                .expectError(ClaimAccessDeniedException.class)
                .verify();

        verifyNoInteractions(repository);
    }

    @Test
    void createClaimRejectsDuplicateClaimNumber() {
        when(repository.findByClaimNumber("CLM-001"))
                .thenReturn(Mono.just(claim));

        StepVerifier.create(
                        service.createClaim(request, 100L, "BUSINESS_OWNER")
                )
                .expectError(ClaimAccessDeniedException.class)
                .verify();

        verify(repository).findByClaimNumber("CLM-001");
        verify(repository, never()).save(any(Claim.class));
    }

    // =====================================================
    // GET OWNER CLAIMS
    // =====================================================

    @Test
    void getOwnerClaimsSuccessfully() {
        when(repository.findByOwnerId(100L))
                .thenReturn(Flux.just(claim));

        StepVerifier.create(
                        service.getOwnerClaims(100L, "BUSINESS_OWNER")
                )
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals(100L, response.ownerId());
                    assertEquals("CLM-001", response.claimNumber());
                })
                .verifyComplete();

        verify(repository).findByOwnerId(100L);
    }

    @Test
    void getOwnerClaimsReturnsEmptyWhenNoClaimsExist() {
        when(repository.findByOwnerId(100L))
                .thenReturn(Flux.empty());

        StepVerifier.create(
                        service.getOwnerClaims(100L, "BUSINESS_OWNER")
                )
                .verifyComplete();

        verify(repository).findByOwnerId(100L);
    }

    @Test
    void getOwnerClaimsDeniedForAdmin() {
        StepVerifier.create(
                        service.getOwnerClaims(1L, "ADMIN")
                )
                .expectError(ClaimAccessDeniedException.class)
                .verify();

        verifyNoInteractions(repository);
    }

    // =====================================================
    // GET ALL CLAIMS
    // =====================================================

    @Test
    void getAllClaimsSuccessfullyForAdmin() {
        when(repository.findAll())
                .thenReturn(Flux.just(claim));

        StepVerifier.create(
                        service.getAllClaims(1L, "ADMIN")
                )
                .assertNext(response ->
                        assertEquals("CLM-001", response.claimNumber())
                )
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void getAllClaimsSuccessfullyForClaimsAdjuster() {
        when(repository.findAll())
                .thenReturn(Flux.just(claim));

        StepVerifier.create(
                        service.getAllClaims(1L, "CLAIMS_ADJUSTER")
                )
                .expectNextCount(1)
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void getAllClaimsSuccessfullyForRiskEngineer() {
        when(repository.findAll())
                .thenReturn(Flux.just(claim));

        StepVerifier.create(
                        service.getAllClaims(1L, "RISK_ENGINEER")
                )
                .expectNextCount(1)
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void getAllClaimsSuccessfullyForUnderwriter() {
        when(repository.findAll())
                .thenReturn(Flux.just(claim));

        StepVerifier.create(
                        service.getAllClaims(1L, "UNDERWRITER")
                )
                .expectNextCount(1)
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void getAllClaimsDeniedForBusinessOwner() {
        StepVerifier.create(
                        service.getAllClaims(100L, "BUSINESS_OWNER")
                )
                .expectError(ClaimAccessDeniedException.class)
                .verify();

        verifyNoInteractions(repository);
    }

    // =====================================================
    // GET CLAIM BY ID
    // =====================================================

    @Test
    void getClaimSuccessfullyForAdmin() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(claim));

        StepVerifier.create(
                        service.getClaim(1L, 1L, "ADMIN")
                )
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals("CLM-001", response.claimNumber());
                })
                .verifyComplete();

        verify(repository).findById(1L);
    }

    @Test
    void getClaimSuccessfullyForOwner() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(claim));

        StepVerifier.create(
                        service.getClaim(1L, 100L, "BUSINESS_OWNER")
                )
                .assertNext(response ->
                        assertEquals(100L, response.ownerId())
                )
                .verifyComplete();

        verify(repository).findById(1L);
    }

    @Test
    void getClaimDeniedForDifferentOwner() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(claim));

        StepVerifier.create(
                        service.getClaim(1L, 200L, "BUSINESS_OWNER")
                )
                .expectError(ClaimAccessDeniedException.class)
                .verify();

        verify(repository).findById(1L);
    }

    @Test
    void getClaimDeniedForUnauthorizedRole() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(claim));

        StepVerifier.create(
                        service.getClaim(1L, 100L, "UNKNOWN")
                )
                .expectError(ClaimAccessDeniedException.class)
                .verify();

        verify(repository).findById(1L);
    }

    @Test
    void getClaimReturnsNotFound() {
        when(repository.findById(99L))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        service.getClaim(99L, 100L, "ADMIN")
                )
                .expectError(ClaimNotFoundException.class)
                .verify();

        verify(repository).findById(99L);
    }

    // =====================================================
    // UPDATE CLAIM STATUS
    // =====================================================

    @Test
    void updateClaimStatusSuccessfully() {
        UpdateClaimStatusRequest updateRequest =
                new UpdateClaimStatusRequest(
                        ClaimStatus.APPROVED,
                        new BigDecimal("4500"),
                        200L
                );

        when(repository.findById(1L))
                .thenReturn(Mono.just(claim));

        when(repository.save(any(Claim.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        StepVerifier.create(
                        service.updateClaimStatus(
                                1L,
                                updateRequest,
                                200L,
                                "CLAIMS_ADJUSTER"
                        )
                )
                .assertNext(response -> {
                    assertEquals(ClaimStatus.APPROVED, response.status());
                    assertEquals(
                            new BigDecimal("4500"),
                            response.approvedAmount()
                    );
                    assertEquals(200L, response.assignedAdjusterId());
                    assertNotNull(response.updatedAt());
                })
                .verifyComplete();

        verify(repository).findById(1L);
        verify(repository).save(any(Claim.class));
    }

    @Test
    void updateClaimStatusSuccessfullyForAdmin() {
        UpdateClaimStatusRequest updateRequest =
                new UpdateClaimStatusRequest(
                        ClaimStatus.REJECTED,
                        null,
                        null
                );

        when(repository.findById(1L))
                .thenReturn(Mono.just(claim));

        when(repository.save(any(Claim.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        StepVerifier.create(
                        service.updateClaimStatus(
                                1L,
                                updateRequest,
                                1L,
                                "ADMIN"
                        )
                )
                .assertNext(response ->
                        assertEquals(ClaimStatus.REJECTED, response.status())
                )
                .verifyComplete();

        verify(repository).save(any(Claim.class));
    }

    @Test
    void updateClaimStatusDeniedForBusinessOwner() {
        UpdateClaimStatusRequest updateRequest =
                new UpdateClaimStatusRequest(
                        ClaimStatus.APPROVED,
                        null,
                        null
                );

        StepVerifier.create(
                        service.updateClaimStatus(
                                1L,
                                updateRequest,
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectError(ClaimAccessDeniedException.class)
                .verify();

        verifyNoInteractions(repository);
    }

    @Test
    void updateClaimStatusReturnsNotFound() {
        UpdateClaimStatusRequest updateRequest =
                new UpdateClaimStatusRequest(
                        ClaimStatus.APPROVED,
                        null,
                        null
                );

        when(repository.findById(99L))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        service.updateClaimStatus(
                                99L,
                                updateRequest,
                                200L,
                                "CLAIMS_ADJUSTER"
                        )
                )
                .expectError(ClaimNotFoundException.class)
                .verify();

        verify(repository).findById(99L);
        verify(repository, never()).save(any(Claim.class));
    }
}