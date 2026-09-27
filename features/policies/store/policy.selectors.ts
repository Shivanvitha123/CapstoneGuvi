import { createFeatureSelector, createSelector } from '@ngrx/store';

import { PolicyState } from './policy.reducer';

export const selectPolicyState =
  createFeatureSelector<PolicyState>('policies');

export const selectPolicies =
  createSelector(
    selectPolicyState,
    state => state.policies
  );

export const selectSelectedPolicy =
  createSelector(
    selectPolicyState,
    state => state.selectedPolicy
  );

export const selectPolicyLoading =
  createSelector(
    selectPolicyState,
    state => state.loading
  );

export const selectPolicyError =
  createSelector(
    selectPolicyState,
    state => state.error
  );

export const selectPolicySuccessMessage =
  createSelector(
    selectPolicyState,
    state => state.successMessage
  );

