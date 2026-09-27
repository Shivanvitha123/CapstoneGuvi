import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';

import { PolicyApiService } from '../services/policy-api.service';

import * as PolicyActions from './policy.actions';

@Injectable()
export class PolicyEffects {

  private readonly actions$ = inject(Actions);
  private readonly policyApi = inject(PolicyApiService);

  loadOwnerPolicies$ = createEffect(() =>
    this.actions$.pipe(

      ofType(PolicyActions.loadOwnerPolicies),

      switchMap(() =>
        this.policyApi.getOwnerPolicies().pipe(

          map(policies =>
            PolicyActions.loadOwnerPoliciesSuccess({
              policies
            })
          ),

          catchError(error =>
            of(
              PolicyActions.policyFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  loadAllPolicies$ = createEffect(() =>
    this.actions$.pipe(

      ofType(PolicyActions.loadAllPolicies),

      switchMap(() =>
        this.policyApi.getAllPolicies().pipe(

          map(policies =>
            PolicyActions.loadAllPoliciesSuccess({
              policies
            })
          ),

          catchError(error =>
            of(
              PolicyActions.policyFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  loadPolicy$ = createEffect(() =>
    this.actions$.pipe(

      ofType(PolicyActions.loadPolicy),

      switchMap(({ policyId }) =>
        this.policyApi.getPolicy(policyId).pipe(

          map(policy =>
            PolicyActions.loadPolicySuccess({
              policy
            })
          ),

          catchError(error =>
            of(
              PolicyActions.policyFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  createPolicy$ = createEffect(() =>
    this.actions$.pipe(

      ofType(PolicyActions.createPolicy),

      switchMap(({ request }) =>
        this.policyApi.createPolicy(request).pipe(

          map(policy =>
            PolicyActions.createPolicySuccess({
              policy
            })
          ),

          catchError(error =>
            of(
              PolicyActions.policyFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  updatePolicy$ = createEffect(() =>
    this.actions$.pipe(

      ofType(PolicyActions.updatePolicy),

      switchMap(({ policyId, request }) =>
        this.policyApi.updatePolicy(
          policyId,
          request
        ).pipe(

          map(policy =>
            PolicyActions.updatePolicySuccess({
              policy
            })
          ),

          catchError(error =>
            of(
              PolicyActions.policyFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  submitPolicy$ = createEffect(() =>
    this.actions$.pipe(

      ofType(PolicyActions.submitPolicy),

      switchMap(({ policyId }) =>
        this.policyApi.submitPolicy(policyId).pipe(

          map(policy =>
            PolicyActions.submitPolicySuccess({
              policy
            })
          ),

          catchError(error =>
            of(
              PolicyActions.policyFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  updatePolicyStatus$ = createEffect(() =>
    this.actions$.pipe(

      ofType(PolicyActions.updatePolicyStatus),

      switchMap(({ policyId, status }) =>
        this.policyApi.updatePolicyStatus(
          policyId,
          status
        ).pipe(

          map(policy =>
            PolicyActions.updatePolicyStatusSuccess({
              policy
            })
          ),

          catchError(error =>
            of(
              PolicyActions.policyFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  private getErrorMessage(error: any): string {

    return (
      error?.error?.message ||
      error?.message ||
      'Something went wrong while processing the policy.'
    );
  }
}

