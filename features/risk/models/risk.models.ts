export interface RiskAssessment {
  id: number;
  businessId: number;
  policyId: number | null;
  riskScore: number;
  riskLevel: string;
  riskFactors: string | null;
  recommendation: string | null;
  assessedBy: number;
  createdAt: string;
  updatedAt: string;
}

export interface RiskAssessmentRequest {
  businessId: number;
  policyId?: number | null;
  riskScore: number;
  riskFactors?: string;
  recommendation?: string;
}

export interface Simulation {
  id: number;
  businessId: number;
  policyId: number | null;
  scenarioName: string;
  scenarioInput: string | null;
  projectedRiskScore: number | null;
  projectedRiskLevel: string | null;
  resultSummary: string | null;
  status: string;
  createdBy: number;
  createdAt: string;
  updatedAt: string;
}

export interface SimulationRequest {
  businessId: number;
  policyId?: number | null;
  scenarioName: string;
  scenarioInput?: string;
}
