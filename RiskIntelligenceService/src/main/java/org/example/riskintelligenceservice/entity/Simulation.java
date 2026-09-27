package org.example.riskintelligenceservice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.riskintelligenceservice.model.RiskLevel;
import org.example.riskintelligenceservice.model.SimulationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("simulations")
public class Simulation {

    @Id
    private Long id;

    @Column("business_id")
    private Long businessId;

    @Column("policy_id")
    private Long policyId;

    @Column("owner_id")
    private Long ownerId;

    @Column("scenario_name")
    private String scenarioName;

    @Column("scenario_input")
    private String scenarioInput;

    @Column("projected_risk_score")
    private BigDecimal projectedRiskScore;

    @Column("projected_risk_level")
    private RiskLevel projectedRiskLevel;

    @Column("result_summary")
    private String resultSummary;

    @Column("status")
    private SimulationStatus status;

    @Column("created_by")
    private Long createdBy;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}


