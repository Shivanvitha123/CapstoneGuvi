package org.example.riskintelligenceservice.service;

import org.example.riskintelligenceservice.dto.RiskAssessmentRequest;
import org.example.riskintelligenceservice.entity.RiskAssessment;
import org.example.riskintelligenceservice.exception.RiskAccessDeniedException;
import org.example.riskintelligenceservice.exception.RiskAssessmentNotFoundException;
import org.example.riskintelligenceservice.model.RiskLevel;
import org.example.riskintelligenceservice.repository.RiskAssessmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RiskAssessmentServiceImplTest {

    @Mock
    private RiskAssessmentRepository repository;

    @InjectMocks
    private RiskAssessmentServiceImpl service;

    private RiskAssessmentRequest request(BigDecimal score) {
        RiskAssessmentRequest request =
                mock(RiskAssessmentRequest.class);

        when(request.businessId()).thenReturn(100L);
        when(request.policyId()).thenReturn(200L);
        when(request.riskScore()).thenReturn(score);
        when(request.riskFactors())
                .thenReturn("Weather, location");
        when(request.recommendation())
                .thenReturn("Review coverage");

        return request;
    }

    private RiskAssessment assessment(BigDecimal score) {
        LocalDateTime now = LocalDateTime.now();

        RiskLevel level;

        if (score.compareTo(BigDecimal.valueOf(25)) < 0) {
            level = RiskLevel.LOW;
        } else if (score.compareTo(BigDecimal.valueOf(50)) < 0) {
            level = RiskLevel.MEDIUM;
        } else if (score.compareTo(BigDecimal.valueOf(75)) < 0) {
            level = RiskLevel.HIGH;
        } else {
            level = RiskLevel.CRITICAL;
        }

        return RiskAssessment.builder()
                .id(1L)
                .businessId(100L)
                .policyId(200L)
                .ownerId(10L)
                .riskScore(score)
                .riskLevel(level)
                .riskFactors("Weather, location")
                .recommendation("Review coverage")
                .assessedBy(10L)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    // CREATE ASSESSMENT TESTS

    @Test
    void createAssessment_success() {
        RiskAssessmentRequest request =
                request(new BigDecimal("20"));

        when(repository.save(any(RiskAssessment.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));

        StepVerifier.create(
                        service.createAssessment(
                                request,
                                10L,
                                "RISK_ENGINEER"))
                .assertNext(response -> {
                    assertEquals(100L, response.businessId());
                    assertEquals(200L, response.policyId());
                    assertEquals(
                            new BigDecimal("20"),
                            response.riskScore());
                    assertEquals(
                            RiskLevel.LOW,
                            response.riskLevel());
                    assertEquals(
                            10L,
                            response.assessedBy());
                    assertEquals(
                            "Weather, location",
                            response.riskFactors());
                    assertEquals(
                            "Review coverage",
                            response.recommendation());
                    assertNotNull(response.createdAt());
                    assertNotNull(response.updatedAt());
                })
                .verifyComplete();

        verify(repository).save(any(RiskAssessment.class));
    }

    @Test
    void createAssessment_mediumRisk() {
        when(repository.save(any(RiskAssessment.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));

        StepVerifier.create(
                        service.createAssessment(
                                request(new BigDecimal("25")),
                                10L,
                                "RISK_ENGINEER"))
                .assertNext(response ->
                        assertEquals(
                                RiskLevel.MEDIUM,
                                response.riskLevel()))
                .verifyComplete();
    }

    @Test
    void createAssessment_highRisk() {
        when(repository.save(any(RiskAssessment.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));

        StepVerifier.create(
                        service.createAssessment(
                                request(new BigDecimal("50")),
                                10L,
                                "RISK_ENGINEER"))
                .assertNext(response ->
                        assertEquals(
                                RiskLevel.HIGH,
                                response.riskLevel()))
                .verifyComplete();
    }

    @Test
    void createAssessment_criticalRisk() {
        when(repository.save(any(RiskAssessment.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));

        StepVerifier.create(
                        service.createAssessment(
                                request(new BigDecimal("75")),
                                10L,
                                "RISK_ENGINEER"))
                .assertNext(response ->
                        assertEquals(
                                RiskLevel.CRITICAL,
                                response.riskLevel()))
                .verifyComplete();
    }

    @Test
    void createAssessment_zeroScoreIsLow() {
        when(repository.save(any(RiskAssessment.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));

        StepVerifier.create(
                        service.createAssessment(
                                request(BigDecimal.ZERO),
                                10L,
                                "RISK_ENGINEER"))
                .assertNext(response ->
                        assertEquals(
                                RiskLevel.LOW,
                                response.riskLevel()))
                .verifyComplete();
    }

    @Test
    void createAssessment_maximumScoreIsCritical() {
        when(repository.save(any(RiskAssessment.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0)));

        StepVerifier.create(
                        service.createAssessment(
                                request(BigDecimal.valueOf(100)),
                                10L,
                                "RISK_ENGINEER"))
                .assertNext(response ->
                        assertEquals(
                                RiskLevel.CRITICAL,
                                response.riskLevel()))
                .verifyComplete();
    }

    @Test
    void createAssessment_wrongRoleDenied() {
        StepVerifier.create(
                        service.createAssessment(
                                request(BigDecimal.TEN),
                                10L,
                                "ADMIN"))
                .expectError(RiskAccessDeniedException.class)
                .verify();

        verifyNoInteractions(repository);
    }

    @Test
    void createAssessment_nullScoreRejected() {
        StepVerifier.create(
                        service.createAssessment(
                                request(null),
                                10L,
                                "RISK_ENGINEER"))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never())
                .save(any(RiskAssessment.class));
    }

    @Test
    void createAssessment_negativeScoreRejected() {
        StepVerifier.create(
                        service.createAssessment(
                                request(new BigDecimal("-1")),
                                10L,
                                "RISK_ENGINEER"))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never())
                .save(any(RiskAssessment.class));
    }

    @Test
    void createAssessment_scoreAbove100Rejected() {
        StepVerifier.create(
                        service.createAssessment(
                                request(new BigDecimal("101")),
                                10L,
                                "RISK_ENGINEER"))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(repository, never())
                .save(any(RiskAssessment.class));
    }

    // GET ALL ASSESSMENTS TESTS

    @Test
    void getAllAssessments_success() {
        when(repository.findAll())
                .thenReturn(
                        Flux.just(assessment(
                                new BigDecimal("60"))));

        StepVerifier.create(
                        service.getAllAssessments(
                                10L,
                                "ADMIN"))
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals(
                            RiskLevel.HIGH,
                            response.riskLevel());
                })
                .verifyComplete();
    }

    @Test
    void getAllAssessments_allPermittedRoles() {
        String[] roles = {
                "ADMIN",
                "BUSINESS_OWNER",
                "UNDERWRITER",
                "RISK_ENGINEER",
                "CLAIMS_ADJUSTER"
        };

        for (String role : roles) {
            when(repository.findAll())
                    .thenReturn(Flux.empty());

            StepVerifier.create(
                            service.getAllAssessments(10L, role))
                    .verifyComplete();
        }

        verify(repository, times(roles.length)).findAll();
    }

    @Test
    void getAllAssessments_unauthorizedRoleDenied() {
        StepVerifier.create(
                        service.getAllAssessments(
                                10L,
                                "UNKNOWN"))
                .expectError(RiskAccessDeniedException.class)
                .verify();

        verifyNoInteractions(repository);
    }

    // GET ASSESSMENT TESTS

    @Test
    void getAssessment_success() {
        when(repository.findById(1L))
                .thenReturn(
                        Mono.just(assessment(
                                new BigDecimal("30"))));

        StepVerifier.create(
                        service.getAssessment(
                                1L,
                                10L,
                                "UNDERWRITER"))
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals(
                            RiskLevel.MEDIUM,
                            response.riskLevel());
                })
                .verifyComplete();
    }

    @Test
    void getAssessment_notFound() {
        when(repository.findById(99L))
                .thenReturn(Mono.empty());

        StepVerifier.create(
                        service.getAssessment(
                                99L,
                                10L,
                                "ADMIN"))
                .expectError(
                        RiskAssessmentNotFoundException.class)
                .verify();
    }

    @Test
    void getAssessment_unauthorizedRoleDenied() {
        StepVerifier.create(
                        service.getAssessment(
                                1L,
                                10L,
                                "UNKNOWN"))
                .expectError(RiskAccessDeniedException.class)
                .verify();

        verifyNoInteractions(repository);
    }

    // GET BY BUSINESS TESTS

    @Test
    void getByBusiness_success() {
        when(repository.findByBusinessId(100L))
                .thenReturn(
                        Flux.just(assessment(
                                new BigDecimal("80"))));

        StepVerifier.create(
                        service.getByBusiness(
                                100L,
                                10L,
                                "BUSINESS_OWNER"))
                .assertNext(response -> {
                    assertEquals(100L, response.businessId());
                    assertEquals(
                            RiskLevel.CRITICAL,
                            response.riskLevel());
                })
                .verifyComplete();
    }

    @Test
    void getByBusiness_emptyResult() {
        when(repository.findByBusinessId(999L))
                .thenReturn(Flux.empty());

        StepVerifier.create(
                        service.getByBusiness(
                                999L,
                                10L,
                                "ADMIN"))
                .verifyComplete();
    }

    @Test
    void getByBusiness_unauthorizedRoleDenied() {
        StepVerifier.create(
                        service.getByBusiness(
                                100L,
                                10L,
                                "UNKNOWN"))
                .expectError(RiskAccessDeniedException.class)
                .verify();

        verifyNoInteractions(repository);
    }
}