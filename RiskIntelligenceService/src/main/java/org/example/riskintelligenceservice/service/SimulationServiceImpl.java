package org.example.riskintelligenceservice.service;


import lombok.RequiredArgsConstructor;
import org.example.riskintelligenceservice.dto.SimulationRequest;
import org.example.riskintelligenceservice.dto.SimulationResponse;
import org.example.riskintelligenceservice.entity.Simulation;
import org.example.riskintelligenceservice.exception.RiskAccessDeniedException;
import org.example.riskintelligenceservice.exception.SimulationNotFoundException;
import org.example.riskintelligenceservice.model.RiskLevel;
import org.example.riskintelligenceservice.model.SimulationStatus;
import org.example.riskintelligenceservice.repository.SimulationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SimulationServiceImpl
        implements SimulationService {

    private final SimulationRepository repository;

    @Override
    public Mono<SimulationResponse> createSimulation(
            SimulationRequest request,
            Long userId,
            String role) {

        /*
         * Simple deterministic Risk Twin simulation.
         * The scenario name/input influence the projected score
         * in a predictable way while keeping the service
         * self-contained for the current implementation.
         */

        BigDecimal projectedScore =
                calculateProjectedScore(
                        request.scenarioName(),
                        request.scenarioInput()
                );

        RiskLevel projectedLevel =
                calculateRiskLevel(projectedScore);

        String summary =
                "Simulation completed for scenario: "
                        + request.scenarioName()
                        + ". Projected risk score: "
                        + projectedScore;

        Simulation simulation =
                Simulation.builder()
                        .businessId(request.businessId())
                        .policyId(request.policyId())
                        .ownerId(userId)
                        .scenarioName(
                                request.scenarioName()
                        )
                        .scenarioInput(
                                request.scenarioInput()
                        )
                        .projectedRiskScore(
                                projectedScore
                        )
                        .projectedRiskLevel(
                                projectedLevel
                        )
                        .resultSummary(summary)
                        .status(
                                SimulationStatus.COMPLETED
                        )
                        .createdBy(userId)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        return repository
                .save(simulation)
                .map(this::toResponse);
    }

    @Override
    public Mono<SimulationResponse> getSimulation(
            Long id,
            Long userId,
            String role) {

        return repository
                .findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new SimulationNotFoundException(id)
                        )
                )
                .flatMap(simulation -> {

                    if (isStaff(role) ||
                            simulation
                                    .getOwnerId()
                                    .equals(userId)) {

                        return Mono.just(
                                toResponse(simulation)
                        );
                    }

                    return Mono.error(
                            new RiskAccessDeniedException()
                    );
                });
    }

    @Override
    public Flux<SimulationResponse> getSimulations(
            Long userId,
            String role) {

        if (isStaff(role)) {

            return repository
                    .findAll()
                    .map(this::toResponse);
        }

        return repository
                .findByOwnerId(userId)
                .map(this::toResponse);
    }

    private BigDecimal calculateProjectedScore(
            String scenarioName,
            String scenarioInput) {

        int hash =
                Math.abs(
                        (scenarioName
                                + String.valueOf(
                                scenarioInput)
                        ).hashCode()
                );

        double score =
                20 + (hash % 7000) / 100.0;

        if (score > 100) {
            score = 100;
        }

        return BigDecimal
                .valueOf(score)
                .setScale(
                        2,
                        java.math.RoundingMode.HALF_UP
                );
    }

    private RiskLevel calculateRiskLevel(
            BigDecimal score) {

        double value = score.doubleValue();

        if (value < 25) {
            return RiskLevel.LOW;
        }

        if (value < 50) {
            return RiskLevel.MEDIUM;
        }

        if (value < 75) {
            return RiskLevel.HIGH;
        }

        return RiskLevel.CRITICAL;
    }

    private boolean isStaff(String role) {

        return "ADMIN".equals(role)
                || "UNDERWRITER".equals(role)
                || "RISK_ENGINEER".equals(role);
    }

    private SimulationResponse toResponse(
            Simulation simulation) {

        return new SimulationResponse(
                simulation.getId(),
                simulation.getBusinessId(),
                simulation.getPolicyId(),
                simulation.getScenarioName(),
                simulation.getScenarioInput(),
                simulation.getProjectedRiskScore(),
                simulation.getProjectedRiskLevel(),
                simulation.getResultSummary(),
                simulation.getStatus(),
                simulation.getCreatedBy(),
                simulation.getCreatedAt(),
                simulation.getUpdatedAt()
        );
    }
}

