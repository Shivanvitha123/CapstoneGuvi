package org.example.claimrecoveryservice.service;

import org.example.claimrecoveryservice.dto.CreateRecoveryRequest;
import org.example.claimrecoveryservice.dto.RecoveryResponse;
import org.example.claimrecoveryservice.dto.UpdateRecoveryStatusRequest;
import org.example.claimrecoveryservice.entity.Claim;
import org.example.claimrecoveryservice.entity.Recovery;
import org.example.claimrecoveryservice.exception.ClaimAccessDeniedException;
import org.example.claimrecoveryservice.exception.ClaimNotFoundException;
import org.example.claimrecoveryservice.exception.RecoveryNotFoundException;
import org.example.claimrecoveryservice.model.RecoveryStatus;
import org.example.claimrecoveryservice.repository.ClaimRepository;
import org.example.claimrecoveryservice.repository.RecoveryRepository;

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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecoveryServiceImplTest {

    @Mock
    private RecoveryRepository recoveryRepository;

    @Mock
    private ClaimRepository claimRepository;

    @InjectMocks
    private RecoveryServiceImpl service;

    private Claim claim;
    private Recovery recovery;
    private CreateRecoveryRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new CreateRecoveryRequest(
                new BigDecimal("3000"),
                "INSURANCE",
                "Recovery for damaged property"
        );

        claim = Claim.builder()
                .id(1L)
                .businessId(10L)
                .policyId(20L)
                .ownerId(100L)
                .claimNumber("CLM-001")
                .build();

        recovery = Recovery.builder()
                .id(1L)
                .claimId(1L)
                .ownerId(100L)
                .recoveryAmount(new BigDecimal("3000"))
                .recoverySource("INSURANCE")
                .description("Recovery for damaged property")
                .status(RecoveryStatus.INITIATED)
                .processedBy(200L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // CREATE RECOVERY

    @Test
    void createRecoverySuccessfullyForClaimsAdjuster() {
        when(claimRepository.findById(1L))
                .thenReturn(Mono.just(claim));

        when(recoveryRepository.save(any(Recovery.class)))
                .thenAnswer(invocation -> {
                    Recovery saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return Mono.just(saved);
                });

        StepVerifier.create(
                        service.createRecovery(
                                1L, createRequest, 200L, "CLAIMS_ADJUSTER"
                        )
                )
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals(1L, response.claimId());
                    assertEquals(100L, response.ownerId());
                    assertEquals(
                            new BigDecimal("3000"),
                            response.recoveryAmount()
                    );
                    assertEquals("INSURANCE", response.recoverySource());
                    assertEquals(
                            RecoveryStatus.INITIATED,
                            response.status()
                    );
                    assertEquals(200L, response.processedBy());
                    assertNotNull(response.createdAt());
                    assertNotNull(response.updatedAt());
                })
                .verifyComplete();

        verify(claimRepository).findById(1L);
        verify(recoveryRepository).save(any(Recovery.class));
    }

    @Test
    void createRecoverySuccessfullyForAdmin() {
        when(claimRepository.findById(1L))
                .thenReturn(Mono.just(claim));

        when(recoveryRepository.save(any(Recovery.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        StepVerifier.create(
                        service.createRecovery(
                                1L, createRequest, 1L, "ADMIN"
                        )
                )
                .assertNext(response ->
                        assertEquals(
                                RecoveryStatus.INITIATED,
                                response.status()
                        )
                )
                .verifyComplete();

        verify(recoveryRepository).save(any(Recovery.class));
    }

    @Test
    void createRecoveryDeniedForBusinessOwner() {
        StepVerifier.create(
                        service.createRecovery(
                                1L, createRequest, 100L, "BUSINESS_OWNER"
                        )
                )
                .expectError(ClaimAccessDeniedException.class)
                .verify();

        verifyNoInteractions(claimRepository, recoveryRepository);
    }

    @Test
    void createRecoveryDeniedForNullRole() {
        StepVerifier.create(
                        service.createRecovery(
                                1L, createRequest, 200L, null
                        )
                )
                .expectError(ClaimAccessDeniedException.class)
                .verify();

        verifyNoInteractions(claimRepository, recoveryRepository);
    }

    @Test
    void createRecoveryReturnsClaimNotFound() {
        when(claimRepository.findById(99L))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        service.createRecovery(
                                99L, createRequest, 200L, "ADMIN"
                        )
                )
                .expectError(ClaimNotFoundException.class)
                .verify();

        verify(claimRepository).findById(99L);
        verify(recoveryRepository, never()).save(any(Recovery.class));
    }

    // GET RECOVERIES FOR A CLAIM

    @Test
    void getClaimRecoveriesSuccessfullyForAdmin() {
        when(claimRepository.findById(1L))
                .thenReturn(Mono.just(claim));

        when(recoveryRepository.findByClaimId(1L))
                .thenReturn(Flux.just(recovery));

        StepVerifier.create(
                        service.getClaimRecoveries(1L, 1L, "ADMIN")
                )
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals(1L, response.claimId());
                    assertEquals(
                            RecoveryStatus.INITIATED,
                            response.status()
                    );
                })
                .verifyComplete();

        verify(claimRepository).findById(1L);
        verify(recoveryRepository).findByClaimId(1L);
    }

    @Test
    void getClaimRecoveriesSuccessfullyForClaimsAdjuster() {
        when(claimRepository.findById(1L))
                .thenReturn(Mono.just(claim));

        when(recoveryRepository.findByClaimId(1L))
                .thenReturn(Flux.just(recovery));

        StepVerifier.create(
                        service.getClaimRecoveries(
                                1L, 200L, "CLAIMS_ADJUSTER"
                        )
                )
                .expectNextCount(1)
                .verifyComplete();

        verify(recoveryRepository).findByClaimId(1L);
    }

    @Test
    void getClaimRecoveriesSuccessfullyForOwner() {
        when(claimRepository.findById(1L))
                .thenReturn(Mono.just(claim));

        when(recoveryRepository.findByClaimId(1L))
                .thenReturn(Flux.just(recovery));

        StepVerifier.create(
                        service.getClaimRecoveries(
                                1L, 100L, "BUSINESS_OWNER"
                        )
                )
                .assertNext(response ->
                        assertEquals(100L, response.ownerId())
                )
                .verifyComplete();

        verify(recoveryRepository).findByClaimId(1L);
    }

    @Test
    void getClaimRecoveriesDeniedForDifferentOwner() {
        when(claimRepository.findById(1L))
                .thenReturn(Mono.just(claim));

        StepVerifier.create(
                        service.getClaimRecoveries(
                                1L, 999L, "BUSINESS_OWNER"
                        )
                )
                .expectError(ClaimAccessDeniedException.class)
                .verify();

        verify(recoveryRepository, never()).findByClaimId(any());
    }

    @Test
    void getClaimRecoveriesReturnsClaimNotFound() {
        when(claimRepository.findById(99L))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        service.getClaimRecoveries(
                                99L, 100L, "ADMIN"
                        )
                )
                .expectError(ClaimNotFoundException.class)
                .verify();

        verify(recoveryRepository, never()).findByClaimId(any());
    }

    @Test
    void getClaimRecoveriesReturnsEmptyWhenNoRecoveriesExist() {
        when(claimRepository.findById(1L))
                .thenReturn(Mono.just(claim));

        when(recoveryRepository.findByClaimId(1L))
                .thenReturn(Flux.empty());

        StepVerifier.create(
                        service.getClaimRecoveries(
                                1L, 100L, "BUSINESS_OWNER"
                        )
                )
                .verifyComplete();
    }

    // GET ALL RECOVERIES

    @Test
    void getAllRecoveriesSuccessfullyForAdmin() {
        when(recoveryRepository.findAll())
                .thenReturn(Flux.just(recovery));

        StepVerifier.create(
                        service.getAllRecoveries(1L, "ADMIN")
                )
                .assertNext(response ->
                        assertEquals(1L, response.id())
                )
                .verifyComplete();

        verify(recoveryRepository).findAll();
    }

    @Test
    void getAllRecoveriesSuccessfullyForClaimsAdjuster() {
        when(recoveryRepository.findAll())
                .thenReturn(Flux.just(recovery));

        StepVerifier.create(
                        service.getAllRecoveries(200L, "CLAIMS_ADJUSTER")
                )
                .expectNextCount(1)
                .verifyComplete();

        verify(recoveryRepository).findAll();
    }

    @Test
    void getAllRecoveriesDeniedForBusinessOwner() {
        StepVerifier.create(
                        service.getAllRecoveries(100L, "BUSINESS_OWNER")
                )
                .expectError(ClaimAccessDeniedException.class)
                .verify();

        verifyNoInteractions(recoveryRepository);
    }

    @Test
    void getAllRecoveriesDeniedForUnderwriter() {
        StepVerifier.create(
                        service.getAllRecoveries(200L, "UNDERWRITER")
                )
                .expectError(ClaimAccessDeniedException.class)
                .verify();

        verifyNoInteractions(recoveryRepository);
    }

    @Test
    void getAllRecoveriesReturnsEmptyWhenNoneExist() {
        when(recoveryRepository.findAll())
                .thenReturn(Flux.empty());

        StepVerifier.create(
                        service.getAllRecoveries(1L, "ADMIN")
                )
                .verifyComplete();

        verify(recoveryRepository).findAll();
    }

    // UPDATE RECOVERY STATUS

    @Test
    void updateRecoveryStatusSuccessfully() {
        UpdateRecoveryStatusRequest request =
                new UpdateRecoveryStatusRequest(
                        RecoveryStatus.RECOVERED
                );

        when(recoveryRepository.findById(1L))
                .thenReturn(Mono.just(recovery));

        when(recoveryRepository.save(any(Recovery.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        StepVerifier.create(
                        service.updateRecoveryStatus(
                                1L, request, 200L, "CLAIMS_ADJUSTER"
                        )
                )
                .assertNext(response -> {
                    assertEquals(
                            RecoveryStatus.RECOVERED,
                            response.status()
                    );
                    assertEquals(200L, response.processedBy());
                    assertNotNull(response.updatedAt());
                })
                .verifyComplete();

        verify(recoveryRepository).findById(1L);
        verify(recoveryRepository).save(any(Recovery.class));
    }

    @Test
    void updateRecoveryStatusSuccessfullyForAdmin() {
        UpdateRecoveryStatusRequest request =
                new UpdateRecoveryStatusRequest(
                        RecoveryStatus.IN_PROGRESS
                );

        when(recoveryRepository.findById(1L))
                .thenReturn(Mono.just(recovery));

        when(recoveryRepository.save(any(Recovery.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        StepVerifier.create(
                        service.updateRecoveryStatus(
                                1L, request, 1L, "ADMIN"
                        )
                )
                .assertNext(response ->
                        assertEquals(
                                RecoveryStatus.IN_PROGRESS,
                                response.status()
                        )
                )
                .verifyComplete();

        verify(recoveryRepository).save(any(Recovery.class));
    }

    @Test
    void updateRecoveryStatusDeniedForBusinessOwner() {
        UpdateRecoveryStatusRequest request =
                new UpdateRecoveryStatusRequest(
                        RecoveryStatus.RECOVERED
                );

        StepVerifier.create(
                        service.updateRecoveryStatus(
                                1L, request, 100L, "BUSINESS_OWNER"
                        )
                )
                .expectError(ClaimAccessDeniedException.class)
                .verify();

        verifyNoInteractions(recoveryRepository);
    }

    @Test
    void updateRecoveryStatusReturnsNotFound() {
        UpdateRecoveryStatusRequest request =
                new UpdateRecoveryStatusRequest(
                        RecoveryStatus.RECOVERED
                );

        when(recoveryRepository.findById(99L))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        service.updateRecoveryStatus(
                                99L, request, 200L, "ADMIN"
                        )
                )
                .expectError(RecoveryNotFoundException.class)
                .verify();

        verify(recoveryRepository).findById(99L);
        verify(recoveryRepository, never()).save(any(Recovery.class));
    }
}