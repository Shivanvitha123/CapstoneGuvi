package org.example.riskintelligenceservice.dto;

import org.example.riskintelligenceservice.model.RiskLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RiskAssessmentResponse(

        Long id,

        Long businessId,

        Long policyId,

        BigDecimal riskScore,

        RiskLevel riskLevel,

        String riskFactors,

        String recommendation,

        Long assessedBy,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}

