import {
  createFeatureSelector,
  createSelector
} from '@ngrx/store';

import { AuthState } from './auth.reducer';

export const selectAuth =
  createFeatureSelector<AuthState>('auth');

export const selectUser =
  createSelector(
    selectAuth,
    state => state.user
  );

export const selectRole =
  createSelector(
    selectUser,
    user => user?.role ?? null
  );

export const selectLoading =
  createSelector(
    selectAuth,
    state => state.loading
  );

export const selectError =
  createSelector(
    selectAuth,
    state => state.error
  );


