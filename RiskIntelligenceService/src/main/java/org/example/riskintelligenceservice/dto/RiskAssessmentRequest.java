package org.example.riskintelligenceservice.dto;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RiskAssessmentRequest(

        @NotNull(message = "Business ID is required")
        Long businessId,

        Long policyId,

        @NotNull(message = "Risk score is required")
        @DecimalMin(value = "0.0")
        @DecimalMax(value = "100.0")
        BigDecimal riskScore,

        String riskFactors,

        String recommendation
) {
}


