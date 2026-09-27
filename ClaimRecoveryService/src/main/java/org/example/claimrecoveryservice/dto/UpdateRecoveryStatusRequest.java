package org.example.claimrecoveryservice.dto;


import jakarta.validation.constraints.NotNull;
import org.example.claimrecoveryservice.model.RecoveryStatus;

public record UpdateRecoveryStatusRequest(

        @NotNull(message = "Status is required")
        RecoveryStatus status
) {
}


