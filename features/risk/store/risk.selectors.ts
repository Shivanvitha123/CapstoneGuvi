import { createFeatureSelector, createSelector } from '@ngrx/store';

import { RiskState } from './risk.reducer';

export const selectRiskState =
  createFeatureSelector<RiskState>('risk');

export const selectAssessments =
  createSelector(
    selectRiskState,
    state => state.assessments
  );

export const selectSimulations =
  createSelector(
    selectRiskState,
    state => state.simulations
  );

export const selectRiskLoading =
  createSelector(
    selectRiskState,
    state => state.loading
  );

export const selectRiskError =
  createSelector(
    selectRiskState,
    state => state.error
  );

export const selectRiskSuccess =
  createSelector(
    selectRiskState,
    state => state.successMessage
  );

