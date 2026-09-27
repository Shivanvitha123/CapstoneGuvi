package org.example.riskintelligenceservice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.riskintelligenceservice.model.RiskLevel;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("risk_assessments")
public class RiskAssessment {

    @Id
    private Long id;

    @Column("business_id")
    private Long businessId;

    @Column("policy_id")
    private Long policyId;

    @Column("owner_id")
    private Long ownerId;

    @Column("risk_score")
    private BigDecimal riskScore;

    @Column("risk_level")
    private RiskLevel riskLevel;

    @Column("risk_factors")
    private String riskFactors;

    @Column("recommendation")
    private String recommendation;

    @Column("assessed_by")
    private Long assessedBy;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}

