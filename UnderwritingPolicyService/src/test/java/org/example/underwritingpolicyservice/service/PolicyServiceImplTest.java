package org.example.underwritingpolicyservice.service;

import org.example.underwritingpolicyservice.dto.CreatePolicyRequest;
import org.example.underwritingpolicyservice.dto.PolicyResponse;
import org.example.underwritingpolicyservice.dto.UpdatePolicyRequest;
import org.example.underwritingpolicyservice.entity.Policy;
import org.example.underwritingpolicyservice.exception.PolicyAccessDeniedException;
import org.example.underwritingpolicyservice.exception.PolicyAlreadyExistsException;
import org.example.underwritingpolicyservice.exception.PolicyNotFoundException;
import org.example.underwritingpolicyservice.model.PolicyStatus;
import org.example.underwritingpolicyservice.repository.PolicyRepository;
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
class PolicyServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyServiceImpl policyService;

    private Policy policy;
    private CreatePolicyRequest createRequest;
    private UpdatePolicyRequest updateRequest;

    private final Long ownerId = 10L;
    private final Long policyId = 1L;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        policy = Policy.builder()
                .id(policyId)
                .businessId(100L)
                .ownerId(ownerId)
                .policyNumber("POL-001")
                .policyType("HEALTH")
                .coverageAmount(new BigDecimal("100000"))
                .premiumAmount(new BigDecimal("5000"))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .status(PolicyStatus.DRAFT.name())
                .description("Test policy")
                .createdAt(now)
                .updatedAt(now)
                .build();

        createRequest = new CreatePolicyRequest(
                100L,
                "POL-001",
                "HEALTH",
                new BigDecimal("100000"),
                new BigDecimal("5000"),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "Test policy"
        );

        updateRequest = new UpdatePolicyRequest(
                200L,
                "POL-002",
                "LIFE",
                new BigDecimal("200000"),
                new BigDecimal("7000"),
                LocalDate.now(),
                LocalDate.now().plusYears(2),
                "Updated policy"
        );
    }

    private void mockSave() {
        when(policyRepository.save(any(Policy.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));
    }

    // CREATE POLICY

    @Test
    void createPolicy_success() {
        mockSave();
        when(policyRepository.findByPolicyNumber("POL-001"))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        policyService.createPolicy(
                                createRequest, ownerId, "BUSINESS_OWNER"))
                .assertNext(response -> {
                    assertEquals("POL-001", response.policyNumber());
                    assertEquals(ownerId, response.ownerId());
                    assertEquals(100L, response.businessId());
                    assertEquals(PolicyStatus.DRAFT, response.status());
                })
                .verifyComplete();

        verify(policyRepository).save(any(Policy.class));
    }

    @Test
    void createPolicy_withRolePrefix_success() {
        mockSave();
        when(policyRepository.findByPolicyNumber("POL-001"))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        policyService.createPolicy(
                                createRequest, ownerId, "ROLE_BUSINESS_OWNER"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void createPolicy_wrongRole_denied() {
        StepVerifier.create(
                        policyService.createPolicy(
                                createRequest, ownerId, "UNDERWRITER"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();

        verifyNoInteractions(policyRepository);
    }

    @Test
    void createPolicy_nullRole_denied() {
        StepVerifier.create(
                        policyService.createPolicy(createRequest, ownerId, null))
                .expectError(PolicyAccessDeniedException.class)
                .verify();
    }

    @Test
    void createPolicy_duplicateNumber_throwsException() {
        when(policyRepository.findByPolicyNumber("POL-001"))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.createPolicy(
                                createRequest, ownerId, "BUSINESS_OWNER"))
                .expectError(PolicyAlreadyExistsException.class)
                .verify();

        verify(policyRepository, never()).save(any());
    }

    // GET POLICY

    @Test
    void getPolicy_ownerCanViewOwnPolicy() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.getPolicy(
                                policyId, ownerId, "BUSINESS_OWNER"))
                .assertNext(response -> {
                    assertEquals(policyId, response.id());
                    assertEquals("POL-001", response.policyNumber());
                })
                .verifyComplete();
    }

    @Test
    void getPolicy_adminCanViewAnyPolicy() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.getPolicy(policyId, 999L, "ADMIN"))
                .assertNext(response ->
                        assertEquals("POL-001", response.policyNumber()))
                .verifyComplete();
    }

    @Test
    void getPolicy_underwriterCanViewAnyPolicy() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.getPolicy(policyId, 999L, "UNDERWRITER"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getPolicy_riskEngineerCanViewAnyPolicy() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.getPolicy(policyId, 999L, "RISK_ENGINEER"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getPolicy_claimsAdjusterCanViewAnyPolicy() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.getPolicy(policyId, 999L, "CLAIMS_ADJUSTER"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getPolicy_ownerCannotViewAnotherOwnersPolicy() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.getPolicy(
                                policyId, 999L, "BUSINESS_OWNER"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();
    }

    @Test
    void getPolicy_nullUserId_deniedForOwner() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.getPolicy(
                                policyId, null, "BUSINESS_OWNER"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();
    }

    @Test
    void getPolicy_notFound() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        policyService.getPolicy(policyId, ownerId, "ADMIN"))
                .expectError(PolicyNotFoundException.class)
                .verify();
    }

    // GET OWNER POLICIES

    @Test
    void getOwnerPolicies_success() {
        when(policyRepository.findByOwnerId(ownerId))
                .thenReturn(Flux.just(policy));

        StepVerifier.create(
                        policyService.getOwnerPolicies(
                                ownerId, "BUSINESS_OWNER"))
                .assertNext(response ->
                        assertEquals("POL-001", response.policyNumber()))
                .verifyComplete();
    }

    @Test
    void getOwnerPolicies_emptyResult() {
        when(policyRepository.findByOwnerId(ownerId))
                .thenReturn(Flux.empty());

        StepVerifier.create(
                        policyService.getOwnerPolicies(
                                ownerId, "BUSINESS_OWNER"))
                .verifyComplete();
    }

    @Test
    void getOwnerPolicies_wrongRole_denied() {
        StepVerifier.create(
                        policyService.getOwnerPolicies(ownerId, "ADMIN"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();

        verifyNoInteractions(policyRepository);
    }

    // GET ALL POLICIES

    @Test
    void getAllPolicies_adminSuccess() {
        when(policyRepository.findAll())
                .thenReturn(Flux.just(policy));

        StepVerifier.create(
                        policyService.getAllPolicies(1L, "ADMIN"))
                .assertNext(response ->
                        assertEquals("POL-001", response.policyNumber()))
                .verifyComplete();
    }

    @Test
    void getAllPolicies_underwriterSuccess() {
        when(policyRepository.findAll())
                .thenReturn(Flux.just(policy));

        StepVerifier.create(
                        policyService.getAllPolicies(1L, "UNDERWRITER"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllPolicies_riskEngineerSuccess() {
        when(policyRepository.findAll())
                .thenReturn(Flux.just(policy));

        StepVerifier.create(
                        policyService.getAllPolicies(1L, "RISK_ENGINEER"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllPolicies_claimsAdjusterSuccess() {
        when(policyRepository.findAll())
                .thenReturn(Flux.just(policy));

        StepVerifier.create(
                        policyService.getAllPolicies(1L, "CLAIMS_ADJUSTER"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllPolicies_businessOwnerDenied() {
        StepVerifier.create(
                        policyService.getAllPolicies(ownerId, "BUSINESS_OWNER"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();

        verifyNoInteractions(policyRepository);
    }

    // UPDATE POLICY

    @Test
    void updatePolicy_success() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));
        mockSave();

        StepVerifier.create(
                        policyService.updatePolicy(
                                policyId, updateRequest, ownerId, "BUSINESS_OWNER"))
                .assertNext(response -> {
                    assertEquals(200L, response.businessId());
                    assertEquals("POL-002", response.policyNumber());
                    assertEquals("LIFE", response.policyType());
                    assertEquals(new BigDecimal("200000"),
                            response.coverageAmount());
                    assertEquals(new BigDecimal("7000"),
                            response.premiumAmount());
                    assertEquals("Updated policy", response.description());
                })
                .verifyComplete();
    }

    @Test
    void updatePolicy_wrongRole_denied() {
        StepVerifier.create(
                        policyService.updatePolicy(
                                policyId, updateRequest, ownerId, "ADMIN"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();

        verifyNoInteractions(policyRepository);
    }

    @Test
    void updatePolicy_notFound() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        policyService.updatePolicy(
                                policyId, updateRequest, ownerId, "BUSINESS_OWNER"))
                .expectError(PolicyNotFoundException.class)
                .verify();
    }

    @Test
    void updatePolicy_anotherOwnerDenied() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.updatePolicy(
                                policyId, updateRequest, 999L, "BUSINESS_OWNER"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();

        verify(policyRepository, never()).save(any());
    }

    @Test
    void updatePolicy_nullOwnerIdDenied() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.updatePolicy(
                                policyId, updateRequest, null, "BUSINESS_OWNER"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();
    }

    @Test
    void updatePolicy_nonDraftPolicyDenied() {
        policy.setStatus(PolicyStatus.SUBMITTED.name());
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.updatePolicy(
                                policyId, updateRequest, ownerId, "BUSINESS_OWNER"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();

        verify(policyRepository, never()).save(any());
    }

    @Test
    void updatePolicy_nullFieldsRemainUnchanged() {
        UpdatePolicyRequest request = new UpdatePolicyRequest(
                null, null, null, null, null, null, null, null
        );

        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));
        mockSave();

        StepVerifier.create(
                        policyService.updatePolicy(
                                policyId, request, ownerId, "BUSINESS_OWNER"))
                .assertNext(response -> {
                    assertEquals(100L, response.businessId());
                    assertEquals("POL-001", response.policyNumber());
                    assertEquals("HEALTH", response.policyType());
                })
                .verifyComplete();
    }

    // SUBMIT POLICY

    @Test
    void submitPolicy_success() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));
        mockSave();

        StepVerifier.create(
                        policyService.submitPolicy(
                                policyId, ownerId, "BUSINESS_OWNER"))
                .assertNext(response ->
                        assertEquals(PolicyStatus.SUBMITTED, response.status()))
                .verifyComplete();
    }

    @Test
    void submitPolicy_wrongRole_denied() {
        StepVerifier.create(
                        policyService.submitPolicy(
                                policyId, ownerId, "UNDERWRITER"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();

        verifyNoInteractions(policyRepository);
    }

    @Test
    void submitPolicy_notFound() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        policyService.submitPolicy(
                                policyId, ownerId, "BUSINESS_OWNER"))
                .expectError(PolicyNotFoundException.class)
                .verify();
    }

    @Test
    void submitPolicy_anotherOwnerDenied() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.submitPolicy(
                                policyId, 999L, "BUSINESS_OWNER"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();

        verify(policyRepository, never()).save(any());
    }

    @Test
    void submitPolicy_nullUserIdDenied() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.submitPolicy(
                                policyId, null, "BUSINESS_OWNER"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();
    }

    @Test
    void submitPolicy_nonDraftPolicyDenied() {
        policy.setStatus(PolicyStatus.SUBMITTED.name());
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.submitPolicy(
                                policyId, ownerId, "BUSINESS_OWNER"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();

        verify(policyRepository, never()).save(any());
    }

    // UPDATE STATUS

    @Test
    void updatePolicyStatus_underwriterSuccess() {
        policy.setStatus(PolicyStatus.SUBMITTED.name());
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));
        mockSave();

        StepVerifier.create(
                        policyService.updatePolicyStatus(
                                policyId, PolicyStatus.DRAFT,
                                20L, "UNDERWRITER"))
                .assertNext(response ->
                        assertEquals(PolicyStatus.DRAFT, response.status()))
                .verifyComplete();
    }

    @Test
    void updatePolicyStatus_adminSuccess() {
        policy.setStatus(PolicyStatus.SUBMITTED.name());
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));
        mockSave();

        StepVerifier.create(
                        policyService.updatePolicyStatus(
                                policyId, PolicyStatus.SUBMITTED,
                                1L, "ADMIN"))
                .assertNext(response ->
                        assertEquals(PolicyStatus.SUBMITTED, response.status()))
                .verifyComplete();
    }

    @Test
    void updatePolicyStatus_businessOwnerDenied() {
        StepVerifier.create(
                        policyService.updatePolicyStatus(
                                policyId, PolicyStatus.SUBMITTED,
                                ownerId, "BUSINESS_OWNER"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();

        verifyNoInteractions(policyRepository);
    }

    @Test
    void updatePolicyStatus_riskEngineerDenied() {
        StepVerifier.create(
                        policyService.updatePolicyStatus(
                                policyId, PolicyStatus.SUBMITTED,
                                20L, "RISK_ENGINEER"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();
    }

    @Test
    void updatePolicyStatus_notFound() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        policyService.updatePolicyStatus(
                                policyId, PolicyStatus.SUBMITTED,
                                1L, "ADMIN"))
                .expectError(PolicyNotFoundException.class)
                .verify();
    }

    @Test
    void updatePolicyStatus_draftPolicyDenied() {
        when(policyRepository.findById(policyId))
                .thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.updatePolicyStatus(
                                policyId, PolicyStatus.SUBMITTED,
                                1L, "ADMIN"))
                .expectError(PolicyAccessDeniedException.class)
                .verify();

        verify(policyRepository, never()).save(any());
    }

    @Test
    void updatePolicyStatus_nullRoleDenied() {
        StepVerifier.create(
                        policyService.updatePolicyStatus(
                                policyId, PolicyStatus.SUBMITTED,
                                1L, null))
                .expectError(PolicyAccessDeniedException.class)
                .verify();
    }
}