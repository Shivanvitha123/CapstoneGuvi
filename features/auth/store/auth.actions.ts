import { createAction, props } from '@ngrx/store';
import { LoginRequest, RegisterRequest } from '../models/auth.model';
import { User } from '../../../core/models/user.model';

// Login
export const login = createAction(
  '[Auth] Login',
  props<{ request: LoginRequest }>()
);

export const loginSuccess = createAction(
  '[Auth] Login Success',
  props<{ token: string; user: User }>()
);

export const loginFailure = createAction(
  '[Auth] Login Failure',
  props<{ error: string }>()
);

// Register
export const register = createAction(
  '[Auth] Register',
  props<{ request: RegisterRequest }>()
);

export const registerSuccess = createAction(
  '[Auth] Register Success',
  props<{ token: string; user: User }>()
);

export const registerFailure = createAction(
  '[Auth] Register Failure',
  props<{ error: string }>()
);

// Load current user
export const loadCurrentUser = createAction(
  '[Auth] Load Current User'
);

export const currentUserSuccess = createAction(
  '[Auth] Current User Success',
  props<{ user: User }>()
);

export const currentUserFailure = createAction(
  '[Auth] Current User Failure'
);

// Logout
export const logout = createAction(
  '[Auth] Logout'
);