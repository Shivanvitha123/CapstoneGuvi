
import { createFeatureSelector, createSelector } from '@ngrx/store';

import { ClaimsState } from './claims.reducer';

export const selectClaimsState =
  createFeatureSelector<ClaimsState>('claims');

export const selectClaims =
  createSelector(
    selectClaimsState,
    state => state.claims
  );

export const selectSelectedClaim =
  createSelector(
    selectClaimsState,
    state => state.selectedClaim
  );

export const selectRecoveries =
  createSelector(
    selectClaimsState,
    state => state.recoveries
  );

export const selectSelectedClaimRecoveries =
  createSelector(
    selectClaimsState,
    state => state.selectedClaimRecoveries
  );

export const selectClaimsLoading =
  createSelector(
    selectClaimsState,
    state => state.loading
  );

export const selectClaimsCreating =
  createSelector(
    selectClaimsState,
    state => state.creating
  );

export const selectClaimsUpdating =
  createSelector(
    selectClaimsState,
    state => state.updating
  );

export const selectClaimsError =
  createSelector(
    selectClaimsState,
    state => state.error
  );

export const selectClaimsSuccessMessage =
  createSelector(
    selectClaimsState,
    state => state.successMessage
  );

export const selectSubmittedClaims =
  createSelector(
    selectClaims,
    claims =>
      claims.filter(
        claim => claim.status === 'SUBMITTED'
      )
  );

export const selectUnderReviewClaims =
  createSelector(
    selectClaims,
    claims =>
      claims.filter(
        claim => claim.status === 'UNDER_REVIEW'
      )
  );

export const selectApprovedClaims =
  createSelector(
    selectClaims,
    claims =>
      claims.filter(
        claim => claim.status === 'APPROVED'
      )
  );

export const selectRejectedClaims =
  createSelector(
    selectClaims,
    claims =>
      claims.filter(
        claim => claim.status === 'REJECTED'
      )
  );


