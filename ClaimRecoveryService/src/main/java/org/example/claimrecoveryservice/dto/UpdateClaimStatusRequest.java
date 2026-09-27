package org.example.claimrecoveryservice.dto;


import jakarta.validation.constraints.NotNull;
import org.example.claimrecoveryservice.model.ClaimStatus;

public record UpdateClaimStatusRequest(

        @NotNull(message = "Status is required")
        ClaimStatus status,

        java.math.BigDecimal approvedAmount,

        Long assignedAdjusterId
) {
}



