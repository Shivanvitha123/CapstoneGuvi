package org.example.claimrecoveryservice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.example.claimrecoveryservice.model.ClaimStatus;
import org.example.claimrecoveryservice.model.ClaimType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("claims")
public class Claim {

    @Id
    private Long id;

    @Column("business_id")
    private Long businessId;

    @Column("policy_id")
    private Long policyId;

    @Column("owner_id")
    private Long ownerId;

    @Column("claim_number")
    private String claimNumber;

    @Column("claim_type")
    private ClaimType claimType;

    @Column("incident_date")
    private LocalDate incidentDate;

    @Column("reported_date")
    private LocalDate reportedDate;

    @Column("claimed_amount")
    private BigDecimal claimedAmount;

    @Column("approved_amount")
    private BigDecimal approvedAmount;

    @Column("description")
    private String description;

    @Column("status")
    private ClaimStatus status;

    @Column("assigned_adjuster_id")
    private Long assignedAdjusterId;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}


