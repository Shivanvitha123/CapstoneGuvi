package org.example.riskintelligenceservice.dto;


import org.example.riskintelligenceservice.model.RiskLevel;
import org.example.riskintelligenceservice.model.SimulationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SimulationResponse(

        Long id,

        Long businessId,

        Long policyId,

        String scenarioName,

        String scenarioInput,

        BigDecimal projectedRiskScore,

        RiskLevel projectedRiskLevel,

        String resultSummary,

        SimulationStatus status,

        Long createdBy,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}


