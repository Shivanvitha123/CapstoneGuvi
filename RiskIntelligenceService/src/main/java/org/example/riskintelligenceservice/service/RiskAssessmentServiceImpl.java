package org.example.riskintelligenceservice.service;

import lombok.RequiredArgsConstructor;
import org.example.riskintelligenceservice.dto.RiskAssessmentRequest;
import org.example.riskintelligenceservice.dto.RiskAssessmentResponse;
import org.example.riskintelligenceservice.entity.RiskAssessment;
import org.example.riskintelligenceservice.exception.RiskAccessDeniedException;
import org.example.riskintelligenceservice.exception.RiskAssessmentNotFoundException;
import org.example.riskintelligenceservice.model.RiskLevel;
import org.example.riskintelligenceservice.repository.RiskAssessmentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RiskAssessmentServiceImpl
        implements RiskAssessmentService {

    private final RiskAssessmentRepository repository;

    @Override
    public Mono<RiskAssessmentResponse> createAssessment(
            RiskAssessmentRequest request,
            Long userId,
            String role) {

        // Only Risk Engineers can create assessments.
        if (!"RISK_ENGINEER".equals(role)) {
            return Mono.error(new RiskAccessDeniedException());
        }

        if (request.riskScore() == null
                || request.riskScore().compareTo(BigDecimal.ZERO) < 0
                || request.riskScore().compareTo(
                BigDecimal.valueOf(100)) > 0) {
            return Mono.error(new IllegalArgumentException(
                    "Risk score must be between 0 and 100"
            ));
        }

        LocalDateTime now = LocalDateTime.now();

        RiskAssessment assessment = RiskAssessment.builder()
                .businessId(request.businessId())
                .policyId(request.policyId())
                .ownerId(userId)
                .riskScore(request.riskScore())
                .riskLevel(calculateRiskLevel(request.riskScore()))
                .riskFactors(request.riskFactors())
                .recommendation(request.recommendation())
                .assessedBy(userId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return repository.save(assessment)
                .map(this::toResponse);
    }

    @Override
    public Flux<RiskAssessmentResponse> getAllAssessments(
            Long userId,
            String role) {

        if (!canViewAssessments(role)) {
            return Flux.error(new RiskAccessDeniedException());
        }

        return repository.findAll()
                .map(this::toResponse);
    }

    @Override
    public Mono<RiskAssessmentResponse> getAssessment(
            Long id,
            Long userId,
            String role) {

        if (!canViewAssessments(role)) {
            return Mono.error(new RiskAccessDeniedException());
        }

        return repository.findById(id)
                .switchIfEmpty(Mono.error(
                        new RiskAssessmentNotFoundException(id)
                ))
                .map(this::toResponse);
    }

    @Override
    public Flux<RiskAssessmentResponse> getByBusiness(
            Long businessId,
            Long userId,
            String role) {

        if (!canViewAssessments(role)) {
            return Flux.error(new RiskAccessDeniedException());
        }

        return repository.findByBusinessId(businessId)
                .map(this::toResponse);
    }

    /**
     * Roles allowed to view risk assessments.
     */
    private boolean canViewAssessments(String role) {
        return "ADMIN".equals(role)
                || "BUSINESS_OWNER".equals(role)
                || "UNDERWRITER".equals(role)
                || "RISK_ENGINEER".equals(role)
                || "CLAIMS_ADJUSTER".equals(role);
    }

    private RiskLevel calculateRiskLevel(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(25)) < 0) {
            return RiskLevel.LOW;
        }

        if (score.compareTo(BigDecimal.valueOf(50)) < 0) {
            return RiskLevel.MEDIUM;
        }

        if (score.compareTo(BigDecimal.valueOf(75)) < 0) {
            return RiskLevel.HIGH;
        }

        return RiskLevel.CRITICAL;
    }

    private RiskAssessmentResponse toResponse(
            RiskAssessment assessment) {

        return new RiskAssessmentResponse(
                assessment.getId(),
                assessment.getBusinessId(),
                assessment.getPolicyId(),
                assessment.getRiskScore(),
                assessment.getRiskLevel(),
                assessment.getRiskFactors(),
                assessment.getRecommendation(),
                assessment.getAssessedBy(),
                assessment.getCreatedAt(),
                assessment.getUpdatedAt()
        );
    }
}