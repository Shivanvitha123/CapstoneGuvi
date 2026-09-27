import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';

import {
  Actions,
  createEffect,
  ofType
} from '@ngrx/effects';

import {
  catchError,
  map,
  of,
  switchMap,
  tap
} from 'rxjs';

import * as Auth from './auth.actions';

import { AuthApiService } from '../../../core/services/auth-api.service';
import { TokenService } from '../../../core/services/token.service';
import { NotificationService } from '../../../core/services/notification.service';

@Injectable()
export class AuthEffects {
  private readonly actions$ = inject(Actions);
  private readonly api = inject(AuthApiService);
  private readonly token = inject(TokenService);
  private readonly router = inject(Router);
  private readonly notification = inject(NotificationService);

  // Login
  login$ = createEffect(() =>
    this.actions$.pipe(
      ofType(Auth.login),
      switchMap(({ request }) =>
        this.api.login(request).pipe(
          map(response =>
            Auth.loginSuccess({
              token: response.token,
              user: {
                userId: response.userId,
                name: response.name,
                email: response.email,
                role: response.role
              }
            })
          ),
          catchError(error =>
            of(
              Auth.loginFailure({
                error: error.error?.message ?? 'Login failed.'
              })
            )
          )
        )
      )
    )
  );

  // Login success
  loginSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(Auth.loginSuccess),
        tap(({ token }) => this.token.set(token)),
        tap(() => this.notification.success('Welcome back!')),
        tap(() => this.router.navigateByUrl('/dashboard'))
      ),
    { dispatch: false }
  );

  // Register
  register$ = createEffect(() =>
    this.actions$.pipe(
      ofType(Auth.register),
      switchMap(({ request }) =>
        this.api.register(request).pipe(
          map(response =>
            Auth.registerSuccess({
              token: response.token,
              user: {
                userId: response.userId,
                name: response.name,
                email: response.email,
                role: response.role
              }
            })
          ),
          catchError(error =>
            of(
              Auth.registerFailure({
                error:
                  error.error?.message ??
                  'Registration failed.'
              })
            )
          )
        )
      )
    )
  );

  // Registration success
  registerSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(Auth.registerSuccess),
        tap(({ token }) => this.token.set(token)),
        tap(() =>
          this.notification.success(
            'Account created successfully.'
          )
        ),
        tap(() => this.router.navigateByUrl('/dashboard'))
      ),
    { dispatch: false }
  );

  // Load current user
  loadUser$ = createEffect(() =>
    this.actions$.pipe(
      ofType(Auth.loadCurrentUser),
      switchMap(() =>
        this.api.me().pipe(
          map(response =>
            Auth.currentUserSuccess({
              user: {
                userId: response.userId,
                name: response.name,
                email: response.email,
                role: response.role
              }
            })
          ),
          catchError(() =>
            of(Auth.currentUserFailure())
          )
        )
      )
    )
  );

  // Logout
  logout$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(Auth.logout),
        tap(() => this.token.clear()),
        tap(() => this.router.navigateByUrl('/'))
      ),
    { dispatch: false }
  );
}