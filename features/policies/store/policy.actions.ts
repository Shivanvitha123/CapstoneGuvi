import { createAction, props } from '@ngrx/store';

import {
  CreatePolicyRequest,
  Policy,
  UpdatePolicyRequest
} from '../models/policy.models';

export const loadOwnerPolicies = createAction(
  '[Policy] Load Owner Policies'
);

export const loadAllPolicies = createAction(
  '[Policy] Load All Policies'
);

export const loadPolicy = createAction(
  '[Policy] Load Policy',
  props<{ policyId: number }>()
);

export const createPolicy = createAction(
  '[Policy] Create Policy',
  props<{ request: CreatePolicyRequest }>()
);

export const updatePolicy = createAction(
  '[Policy] Update Policy',
  props<{
    policyId: number;
    request: UpdatePolicyRequest;
  }>()
);

export const submitPolicy = createAction(
  '[Policy] Submit Policy',
  props<{ policyId: number }>()
);

export const updatePolicyStatus = createAction(
  '[Policy] Update Policy Status',
  props<{
    policyId: number;
    status: string;
  }>()
);

export const clearPolicyMessages = createAction(
  '[Policy] Clear Messages'
);

export const loadOwnerPoliciesSuccess = createAction(
  '[Policy] Load Owner Policies Success',
  props<{ policies: Policy[] }>()
);

export const loadAllPoliciesSuccess = createAction(
  '[Policy] Load All Policies Success',
  props<{ policies: Policy[] }>()
);

export const loadPolicySuccess = createAction(
  '[Policy] Load Policy Success',
  props<{ policy: Policy }>()
);

export const createPolicySuccess = createAction(
  '[Policy] Create Policy Success',
  props<{ policy: Policy }>()
);

export const updatePolicySuccess = createAction(
  '[Policy] Update Policy Success',
  props<{ policy: Policy }>()
);

export const submitPolicySuccess = createAction(
  '[Policy] Submit Policy Success',
  props<{ policy: Policy }>()
);

export const updatePolicyStatusSuccess = createAction(
  '[Policy] Update Policy Status Success',
  props<{ policy: Policy }>()
);

export const policyFailure = createAction(
  '[Policy] Failure',
  props<{ error: string }>()
);


