package org.example.claimrecoveryservice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.example.claimrecoveryservice.model.RecoveryStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("recoveries")
public class Recovery {

    @Id
    private Long id;

    @Column("claim_id")
    private Long claimId;

    @Column("owner_id")
    private Long ownerId;

    @Column("recovery_amount")
    private BigDecimal recoveryAmount;

    @Column("recovery_source")
    private String recoverySource;

    @Column("description")
    private String description;

    @Column("status")
    private RecoveryStatus status;

    @Column("processed_by")
    private Long processedBy;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}

