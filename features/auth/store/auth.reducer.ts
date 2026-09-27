import { createReducer, on } from '@ngrx/store';

import * as Auth from './auth.actions';

import { User } from '../../../core/models/user.model';

export interface AuthState {
  user: User | null;
  loading: boolean;
  error: string | null;
}

export const initialState: AuthState = {
  user: null,
  loading: false,
  error: null
};

export const authReducer = createReducer(
  initialState,

  // Start loading
  on(
    Auth.login,
    Auth.register,
    Auth.loadCurrentUser,
    state => ({
      ...state,
      loading: true,
      error: null
    })
  ),

  // Successful authentication
  on(
    Auth.loginSuccess,
    Auth.registerSuccess,
    Auth.currentUserSuccess,
    (state, { user }) => ({
      ...state,
      user,
      loading: false,
      error: null
    })
  ),

  // Login or registration failure
  on(
    Auth.loginFailure,
    Auth.registerFailure,
    (state, { error }) => ({
      ...state,
      loading: false,
      error
    })
  ),

  // Current user could not be loaded
  on(
    Auth.currentUserFailure,
    state => ({
      ...state,
      loading: false,
      error: null
    })
  ),

  // Logout
  on(
    Auth.logout,
    () => initialState
  )
);