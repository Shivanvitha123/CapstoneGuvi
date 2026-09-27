export type ClaimStatus =
  | 'SUBMITTED'
  | 'UNDER_REVIEW'
  | 'APPROVED'
  | 'REJECTED'
  | 'SETTLED';

export type ClaimType =
  | 'PROPERTY_DAMAGE'
  | 'FIRE'
  | 'FLOOD'
  | 'THEFT'
  | 'LIABILITY'
  | 'ACCIDENT'
  | 'OTHER';

export interface Claim {
  id: number;
  businessId: number;
  policyId: number;
  ownerId: number;
  claimNumber: string;
  claimType: ClaimType;
  incidentDate: string;
  reportedDate: string;
  claimedAmount: number;
  approvedAmount: number | null;
  description: string | null;
  status: ClaimStatus;
  assignedAdjusterId: number | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateClaimRequest {
  businessId: number;
  policyId: number;
  claimNumber: string;
  claimType: ClaimType;
  incidentDate: string;
  reportedDate: string;
  claimedAmount: number;
  description?: string;
}

export interface UpdateClaimStatusRequest {
  status: ClaimStatus;
  approvedAmount?: number;
  assignedAdjusterId?: number;
}

export interface Recovery {
  id: number;
  claimId: number;
  ownerId: number;
  recoveryAmount: number;
  recoverySource: string | null;
  description: string | null;
  status: string;
  processedBy: number | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateRecoveryRequest {
  recoveryAmount: number;
  recoverySource?: string;
  description?: string;
}

export interface UpdateRecoveryStatusRequest {
  status: string;
}

