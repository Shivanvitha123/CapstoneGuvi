import {
  createFeatureSelector,
  createSelector
} from '@ngrx/store';

import { BusinessState } from './business.reducer';

export const selectBusinessState =
  createFeatureSelector<BusinessState>('business');

export const selectBusinesses =
  createSelector(
    selectBusinessState,
    state => state.businesses
  );

export const selectSummaries =
  createSelector(
    selectBusinessState,
    state => state.summaries
  );

export const selectLoading =
  createSelector(
    selectBusinessState,
    state => state.loading
  );

export const selectSaving =
  createSelector(
    selectBusinessState,
    state => state.saving
  );

export const selectBusinessError =
  createSelector(
    selectBusinessState,
    state => state.error
  );