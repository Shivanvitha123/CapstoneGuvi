import { createReducer, on } from '@ngrx/store';

import {
  Claim,
  Recovery
} from '../models/claims.models';

import * as ClaimsActions from './claims.actions';

export interface ClaimsState {
  claims: Claim[];
  selectedClaim: Claim | null;

  recoveries: Recovery[];
  selectedClaimRecoveries: Recovery[];

  loading: boolean;
  creating: boolean;
  updating: boolean;

  error: string | null;
  successMessage: string | null;
}

export const initialState: ClaimsState = {
  claims: [],
  selectedClaim: null,

  recoveries: [],
  selectedClaimRecoveries: [],

  loading: false,
  creating: false,
  updating: false,

  error: null,
  successMessage: null
};

export const claimsReducer = createReducer(
  initialState,

  // ---------------- LOAD CLAIMS ----------------

  on(
    ClaimsActions.loadClaims,
    ClaimsActions.loadOwnerClaims,
    state => ({
      ...state,
      loading: true,
      error: null
    })
  ),

  on(
    ClaimsActions.loadClaimsSuccess,
    state => ({
      ...state,
      claims: state.claims,
      loading: false
    })
  ),

  on(
    ClaimsActions.loadClaimsSuccess,
    (state, { claims }) => ({
      ...state,
      claims,
      loading: false
    })
  ),

  on(
    ClaimsActions.loadClaimsFailure,
    (state, { error }) => ({
      ...state,
      loading: false,
      error
    })
  ),

  // ---------------- CREATE CLAIM ----------------

  on(
    ClaimsActions.createClaim,
    state => ({
      ...state,
      creating: true,
      error: null,
      successMessage: null
    })
  ),

  on(
    ClaimsActions.createClaimSuccess,
    (state, { claim }) => ({
      ...state,
      claims: [claim, ...state.claims],
      creating: false,
      successMessage: 'Claim created successfully.'
    })
  ),

  on(
    ClaimsActions.createClaimFailure,
    (state, { error }) => ({
      ...state,
      creating: false,
      error
    })
  ),

  // ---------------- GET CLAIM ----------------

  on(
    ClaimsActions.getClaim,
    state => ({
      ...state,
      loading: true,
      error: null
    })
  ),

  on(
    ClaimsActions.getClaimSuccess,
    (state, { claim }) => ({
      ...state,
      selectedClaim: claim,
      loading: false
    })
  ),

  on(
    ClaimsActions.getClaimFailure,
    (state, { error }) => ({
      ...state,
      loading: false,
      error
    })
  ),

  // ---------------- UPDATE CLAIM ----------------

  on(
    ClaimsActions.updateClaimStatus,
    state => ({
      ...state,
      updating: true,
      error: null,
      successMessage: null
    })
  ),

  on(
    ClaimsActions.updateClaimStatusSuccess,
    (state, { claim }) => ({
      ...state,
      claims: state.claims.map(item =>
        item.id === claim.id ? claim : item
      ),
      selectedClaim:
        state.selectedClaim?.id === claim.id
          ? claim
          : state.selectedClaim,
      updating: false,
      successMessage: 'Claim status updated successfully.'
    })
  ),

  on(
    ClaimsActions.updateClaimStatusFailure,
    (state, { error }) => ({
      ...state,
      updating: false,
      error
    })
  ),

  // ---------------- CREATE RECOVERY ----------------

  on(
    ClaimsActions.createRecovery,
    state => ({
      ...state,
      creating: true,
      error: null,
      successMessage: null
    })
  ),

  on(
    ClaimsActions.createRecoverySuccess,
    (state, { recovery }) => ({
      ...state,
      selectedClaimRecoveries: [
        recovery,
        ...state.selectedClaimRecoveries
      ],
      recoveries: [
        recovery,
        ...state.recoveries
      ],
      creating: false,
      successMessage: 'Recovery created successfully.'
    })
  ),

  on(
    ClaimsActions.createRecoveryFailure,
    (state, { error }) => ({
      ...state,
      creating: false,
      error
    })
  ),

  // ---------------- CLAIM RECOVERIES ----------------

  on(
    ClaimsActions.loadClaimRecoveries,
    state => ({
      ...state,
      loading: true,
      error: null
    })
  ),

  on(
    ClaimsActions.loadClaimRecoveriesSuccess,
    (state, { recoveries }) => ({
      ...state,
      selectedClaimRecoveries: recoveries,
      loading: false
    })
  ),

  on(
    ClaimsActions.loadClaimRecoveriesFailure,
    (state, { error }) => ({
      ...state,
      loading: false,
      error
    })
  ),

  // ---------------- ALL RECOVERIES ----------------

  on(
    ClaimsActions.loadAllRecoveries,
    state => ({
      ...state,
      loading: true,
      error: null
    })
  ),

  on(
    ClaimsActions.loadAllRecoveriesSuccess,
    (state, { recoveries }) => ({
      ...state,
      recoveries,
      loading: false
    })
  ),

  on(
    ClaimsActions.loadAllRecoveriesFailure,
    (state, { error }) => ({
      ...state,
      loading: false,
      error
    })
  ),

  // ---------------- UPDATE RECOVERY ----------------

  on(
    ClaimsActions.updateRecoveryStatus,
    state => ({
      ...state,
      updating: true,
      error: null,
      successMessage: null
    })
  ),

  on(
    ClaimsActions.updateRecoveryStatusSuccess,
    (state, { recovery }) => ({
      ...state,
      recoveries: state.recoveries.map(item =>
        item.id === recovery.id ? recovery : item
      ),
      selectedClaimRecoveries:
        state.selectedClaimRecoveries.map(item =>
          item.id === recovery.id ? recovery : item
        ),
      updating: false,
      successMessage: 'Recovery status updated successfully.'
    })
  ),

  on(
    ClaimsActions.updateRecoveryStatusFailure,
    (state, { error }) => ({
      ...state,
      updating: false,
      error
    })
  ),

  // ---------------- CLEAR ----------------

  on(
    ClaimsActions.clearClaimsMessage,
    state => ({
      ...state,
      successMessage: null
    })
  ),

  on(
    ClaimsActions.clearClaimsError,
    state => ({
      ...state,
      error: null
    })
  )
);

