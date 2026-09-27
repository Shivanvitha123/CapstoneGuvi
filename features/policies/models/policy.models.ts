export interface CreatePolicyRequest {
  businessId: number;
  policyNumber: string;
  policyType: string;
  coverageAmount: number;
  premiumAmount: number;
  startDate: string;
  endDate: string;
  description?: string;
}

export interface UpdatePolicyRequest {
  businessId?: number;
  policyNumber?: string;
  policyType?: string;
  coverageAmount?: number;
  premiumAmount?: number;
  startDate?: string;
  endDate?: string;
  description?: string;
}

export interface Policy {
  id: number;
  businessId: number;
  ownerId: number;
  policyNumber: string;
  policyType: string;
  coverageAmount: number;
  premiumAmount: number;
  startDate: string;
  endDate: string;
  status: string;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}

