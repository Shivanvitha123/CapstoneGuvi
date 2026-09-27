package org.example.riskintelligenceservice.service;

import org.example.riskintelligenceservice.dto.RiskAssessmentRequest;
import org.example.riskintelligenceservice.dto.RiskAssessmentResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RiskAssessmentService {

    Mono<RiskAssessmentResponse> createAssessment(
            RiskAssessmentRequest request,
            Long userId,
            String role
    );

    Flux<RiskAssessmentResponse> getAllAssessments(
            Long userId,
            String role
    );

    Mono<RiskAssessmentResponse> getAssessment(
            Long id,
            Long userId,
            String role
    );

    Flux<RiskAssessmentResponse> getByBusiness(
            Long businessId,
            Long userId,
            String role
    );
}