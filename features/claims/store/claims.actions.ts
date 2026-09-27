import { createAction, props } from '@ngrx/store';

import {
  Claim,
  CreateClaimRequest,
  UpdateClaimStatusRequest,
  Recovery,
  CreateRecoveryRequest,
  UpdateRecoveryStatusRequest
} from '../models/claims.models';

// ==================== CLAIMS ====================

export const loadClaims = createAction(
  '[Claims] Load Claims'
);

export const loadOwnerClaims = createAction(
  '[Claims] Load Owner Claims'
);

export const loadClaimsSuccess = createAction(
  '[Claims] Load Claims Success',
  props<{ claims: Claim[] }>()
);

export const loadClaimsFailure = createAction(
  '[Claims] Load Claims Failure',
  props<{ error: string }>()
);

export const createClaim = createAction(
  '[Claims] Create Claim',
  props<{ request: CreateClaimRequest }>()
);

export const createClaimSuccess = createAction(
  '[Claims] Create Claim Success',
  props<{ claim: Claim }>()
);

export const createClaimFailure = createAction(
  '[Claims] Create Claim Failure',
  props<{ error: string }>()
);

export const getClaim = createAction(
  '[Claims] Get Claim',
  props<{ claimId: number }>()
);

export const getClaimSuccess = createAction(
  '[Claims] Get Claim Success',
  props<{ claim: Claim }>()
);

export const getClaimFailure = createAction(
  '[Claims] Get Claim Failure',
  props<{ error: string }>()
);

export const updateClaimStatus = createAction(
  '[Claims] Update Claim Status',
  props<{
    claimId: number;
    request: UpdateClaimStatusRequest;
  }>()
);

export const updateClaimStatusSuccess = createAction(
  '[Claims] Update Claim Status Success',
  props<{ claim: Claim }>()
);

export const updateClaimStatusFailure = createAction(
  '[Claims] Update Claim Status Failure',
  props<{ error: string }>()
);

// ==================== RECOVERY ====================

export const createRecovery = createAction(
  '[Claims] Create Recovery',
  props<{
    claimId: number;
    request: CreateRecoveryRequest;
  }>()
);

export const createRecoverySuccess = createAction(
  '[Claims] Create Recovery Success',
  props<{ recovery: Recovery }>()
);

export const createRecoveryFailure = createAction(
  '[Claims] Create Recovery Failure',
  props<{ error: string }>()
);

export const loadClaimRecoveries = createAction(
  '[Claims] Load Claim Recoveries',
  props<{ claimId: number }>()
);

export const loadClaimRecoveriesSuccess = createAction(
  '[Claims] Load Claim Recoveries Success',
  props<{ recoveries: Recovery[] }>()
);

export const loadClaimRecoveriesFailure = createAction(
  '[Claims] Load Claim Recoveries Failure',
  props<{ error: string }>()
);

export const loadAllRecoveries = createAction(
  '[Claims] Load All Recoveries'
);

export const loadAllRecoveriesSuccess = createAction(
  '[Claims] Load All Recoveries Success',
  props<{ recoveries: Recovery[] }>()
);

export const loadAllRecoveriesFailure = createAction(
  '[Claims] Load All Recoveries Failure',
  props<{ error: string }>()
);

export const updateRecoveryStatus = createAction(
  '[Claims] Update Recovery Status',
  props<{
    recoveryId: number;
    request: UpdateRecoveryStatusRequest;
  }>()
);

export const updateRecoveryStatusSuccess = createAction(
  '[Claims] Update Recovery Status Success',
  props<{ recovery: Recovery }>()
);

export const updateRecoveryStatusFailure = createAction(
  '[Claims] Update Recovery Status Failure',
  props<{ error: string }>()
);

export const clearClaimsMessage = createAction(
  '[Claims] Clear Message'
);

export const clearClaimsError = createAction(
  '[Claims] Clear Error'
);


