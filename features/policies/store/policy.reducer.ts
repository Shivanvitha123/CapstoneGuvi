import { createReducer, on } from '@ngrx/store';

import {
  clearPolicyMessages,
  createPolicy,
  createPolicySuccess,
  loadAllPolicies,
  loadAllPoliciesSuccess,
  loadOwnerPolicies,
  loadOwnerPoliciesSuccess,
  loadPolicy,
  loadPolicySuccess,
  policyFailure,
  submitPolicy,
  submitPolicySuccess,
  updatePolicy,
  updatePolicyStatus,
  updatePolicyStatusSuccess,
  updatePolicySuccess
} from './policy.actions';

import { Policy } from '../models/policy.models';

export interface PolicyState {
  policies: Policy[];
  selectedPolicy: Policy | null;
  loading: boolean;
  error: string | null;
  successMessage: string | null;
}

export const initialPolicyState: PolicyState = {
  policies: [],
  selectedPolicy: null,
  loading: false,
  error: null,
  successMessage: null
};

export const policyReducer = createReducer(

  initialPolicyState,

  on(
    loadOwnerPolicies,
    loadAllPolicies,
    loadPolicy,
    createPolicy,
    updatePolicy,
    submitPolicy,
    updatePolicyStatus,

    state => ({
      ...state,
      loading: true,
      error: null,
      successMessage: null
    })
  ),

  on(
    loadOwnerPoliciesSuccess,
    (state, { policies }) => ({
      ...state,
      policies,
      loading: false
    })
  ),

  on(
    loadAllPoliciesSuccess,
    (state, { policies }) => ({
      ...state,
      policies,
      loading: false
    })
  ),

  on(
    loadPolicySuccess,
    (state, { policy }) => ({
      ...state,
      selectedPolicy: policy,
      loading: false
    })
  ),

  on(
    createPolicySuccess,
    (state, { policy }) => ({
      ...state,
      policies: [policy, ...state.policies],
      selectedPolicy: policy,
      loading: false,
      successMessage: 'Policy created successfully.'
    })
  ),

  on(
    updatePolicySuccess,
    (state, { policy }) => ({
      ...state,
      policies: state.policies.map(existing =>
        existing.id === policy.id
          ? policy
          : existing
      ),
      selectedPolicy: policy,
      loading: false,
      successMessage: 'Policy updated successfully.'
    })
  ),

  on(
    submitPolicySuccess,
    (state, { policy }) => ({
      ...state,
      policies: state.policies.map(existing =>
        existing.id === policy.id
          ? policy
          : existing
      ),
      selectedPolicy: policy,
      loading: false,
      successMessage: 'Policy submitted successfully.'
    })
  ),

  on(
    updatePolicyStatusSuccess,
    (state, { policy }) => ({
      ...state,
      policies: state.policies.map(existing =>
        existing.id === policy.id
          ? policy
          : existing
      ),
      selectedPolicy: policy,
      loading: false,
      successMessage: 'Policy status updated successfully.'
    })
  ),

  on(
    policyFailure,
    (state, { error }) => ({
      ...state,
      loading: false,
      error
    })
  ),

  on(
    clearPolicyMessages,
    state => ({
      ...state,
      error: null,
      successMessage: null
    })
  )
);
