package org.example.riskintelligenceservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SimulationRequest(

        @NotNull(message = "Business id is required")
        Long businessId,

        Long policyId,

        @NotBlank(message = "Scenario name is required")
        String scenarioName,

        String scenarioInput

) {
}