package org.example.underwritingpolicyservice.controller;

import org.example.underwritingpolicyservice.dto.CreatePolicyRequest;
import org.example.underwritingpolicyservice.dto.PolicyResponse;
import org.example.underwritingpolicyservice.dto.UpdatePolicyRequest;
import org.example.underwritingpolicyservice.model.PolicyStatus;
import org.example.underwritingpolicyservice.service.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyControllerTest {

    @Mock
    private PolicyService policyService;

    @InjectMocks
    private PolicyController controller;

    private PolicyResponse response;
    private Authentication ownerAuth;
    private Authentication adminAuth;

    @BeforeEach
    void setUp() {
        response = new PolicyResponse(
                1L,
                100L,
                10L,
                "POL-001",
                "HEALTH",
                new BigDecimal("100000"),
                new BigDecimal("5000"),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                PolicyStatus.DRAFT,
                "Test policy",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        ownerAuth = new UsernamePasswordAuthenticationToken(
                "10", null,
                List.of(new SimpleGrantedAuthority("ROLE_BUSINESS_OWNER"))
        );

        adminAuth = new UsernamePasswordAuthenticationToken(
                "20", null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    @Test
    void createPolicy_success() {
        CreatePolicyRequest request = new CreatePolicyRequest(
                100L, "POL-001", "HEALTH",
                new BigDecimal("100000"),
                new BigDecimal("5000"),
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "Test policy"
        );

        when(policyService.createPolicy(request, 10L, "BUSINESS_OWNER"))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.createPolicy(request, ownerAuth))
                .assertNext(result -> {
                    assertEquals(HttpStatus.CREATED, result.getStatusCode());
                    assertEquals(response, result.getBody());
                })
                .verifyComplete();

        verify(policyService).createPolicy(request, 10L, "BUSINESS_OWNER");
    }

    @Test
    void getOwnerPolicies_success() {
        when(policyService.getOwnerPolicies(10L, "BUSINESS_OWNER"))
                .thenReturn(Flux.just(response));

        StepVerifier.create(controller.getOwnerPolicies(ownerAuth))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void getAllPolicies_success() {
        when(policyService.getAllPolicies(20L, "ADMIN"))
                .thenReturn(Flux.just(response));

        StepVerifier.create(controller.getAllPolicies(adminAuth))
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void getPolicy_success() {
        when(policyService.getPolicy(1L, 10L, "BUSINESS_OWNER"))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.getPolicy(1L, ownerAuth))
                .assertNext(result -> {
                    assertEquals(HttpStatus.OK, result.getStatusCode());
                    assertEquals(response, result.getBody());
                })
                .verifyComplete();
    }

    @Test
    void updatePolicy_success() {
        UpdatePolicyRequest request = new UpdatePolicyRequest(
                100L,
                "POL-002",
                "LIFE",
                new BigDecimal("200000"),
                new BigDecimal("7000"),
                LocalDate.now(),
                LocalDate.now().plusYears(2),
                "Updated policy"
        );

        when(policyService.updatePolicy(
                1L, request, 10L, "BUSINESS_OWNER"))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.updatePolicy(1L, request, ownerAuth))
                .assertNext(result -> {
                    assertEquals(HttpStatus.OK, result.getStatusCode());
                    assertEquals(response, result.getBody());
                })
                .verifyComplete();
    }

    @Test
    void submitPolicy_success() {
        when(policyService.submitPolicy(1L, 10L, "BUSINESS_OWNER"))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.submitPolicy(1L, ownerAuth))
                .assertNext(result -> {
                    assertEquals(HttpStatus.OK, result.getStatusCode());
                    assertEquals(response, result.getBody());
                })
                .verifyComplete();
    }

    @Test
    void updatePolicyStatus_success() {
        when(policyService.updatePolicyStatus(
                1L, PolicyStatus.APPROVED, 20L, "ADMIN"))
                .thenReturn(Mono.just(response));

        StepVerifier.create(controller.updatePolicyStatus(
                        1L, PolicyStatus.APPROVED, adminAuth))
                .assertNext(result -> {
                    assertEquals(HttpStatus.OK, result.getStatusCode());
                    assertEquals(response, result.getBody());
                })
                .verifyComplete();
    }

    @Test
    void getPolicy_serviceErrorPropagates() {
        when(policyService.getPolicy(999L, 20L, "ADMIN"))
                .thenReturn(Mono.error(
                        new RuntimeException("Policy not found")));

        StepVerifier.create(controller.getPolicy(999L, adminAuth))
                .expectErrorMessage("Policy not found")
                .verify();
    }

    @Test
    void getOwnerPolicies_emptyResult() {
        when(policyService.getOwnerPolicies(10L, "BUSINESS_OWNER"))
                .thenReturn(Flux.empty());

        StepVerifier.create(controller.getOwnerPolicies(ownerAuth))
                .verifyComplete();
    }

    @Test
    void getAllPolicies_emptyResult() {
        when(policyService.getAllPolicies(20L, "ADMIN"))
                .thenReturn(Flux.empty());

        StepVerifier.create(controller.getAllPolicies(adminAuth))
                .verifyComplete();
    }

    @Test
    void getUserId_invalidPrincipalReturnsNull() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "invalid", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

        when(policyService.getAllPolicies(null, "ADMIN"))
                .thenReturn(Flux.empty());

        StepVerifier.create(controller.getAllPolicies(authentication))
                .verifyComplete();

        verify(policyService).getAllPolicies(null, "ADMIN");
    }

    @Test
    void getRole_withoutAuthoritiesReturnsNull() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "10", null, List.of()
                );

        when(policyService.getAllPolicies(10L, null))
                .thenReturn(Flux.empty());

        StepVerifier.create(controller.getAllPolicies(authentication))
                .verifyComplete();

        verify(policyService).getAllPolicies(10L, null);
    }

    @Test
    void getAllPolicies_nullAuthentication() {
        when(policyService.getAllPolicies(null, null))
                .thenReturn(Flux.empty());

        StepVerifier.create(controller.getAllPolicies(null))
                .verifyComplete();

        verify(policyService).getAllPolicies(null, null);
    }
}