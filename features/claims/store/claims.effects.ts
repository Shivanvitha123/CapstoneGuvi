import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, mergeMap, of } from 'rxjs';

import { ClaimsApiService } from '../services/claims-api.service';
import * as ClaimsActions from './claims.actions';

@Injectable()
export class ClaimsEffects {

  private readonly actions$ = inject(Actions);
  private readonly api = inject(ClaimsApiService);

  // ---------------- LOAD CLAIMS ----------------

  loadClaims$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.loadClaims),
      mergeMap(() =>
        this.api.getAllClaims().pipe(
          map(claims =>
            ClaimsActions.loadClaimsSuccess({ claims })
          ),
          catchError(error =>
            of(
              ClaimsActions.loadClaimsFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  loadOwnerClaims$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.loadOwnerClaims),
      mergeMap(() =>
        this.api.getOwnerClaims().pipe(
          map(claims =>
            ClaimsActions.loadClaimsSuccess({ claims })
          ),
          catchError(error =>
            of(
              ClaimsActions.loadClaimsFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  // ---------------- CREATE CLAIM ----------------

  createClaim$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.createClaim),
      mergeMap(({ request }) =>
        this.api.createClaim(request).pipe(
          map(claim =>
            ClaimsActions.createClaimSuccess({ claim })
          ),
          catchError(error =>
            of(
              ClaimsActions.createClaimFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  // ---------------- GET CLAIM ----------------

  getClaim$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.getClaim),
      mergeMap(({ claimId }) =>
        this.api.getClaim(claimId).pipe(
          map(claim =>
            ClaimsActions.getClaimSuccess({ claim })
          ),
          catchError(error =>
            of(
              ClaimsActions.getClaimFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  // ---------------- UPDATE CLAIM ----------------

  updateClaimStatus$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.updateClaimStatus),
      mergeMap(({ claimId, request }) =>
        this.api.updateClaimStatus(
          claimId,
          request
        ).pipe(
          map(claim =>
            ClaimsActions.updateClaimStatusSuccess({
              claim
            })
          ),
          catchError(error =>
            of(
              ClaimsActions.updateClaimStatusFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  // ---------------- CREATE RECOVERY ----------------

  createRecovery$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.createRecovery),
      mergeMap(({ claimId, request }) =>
        this.api.createRecovery(
          claimId,
          request
        ).pipe(
          map(recovery =>
            ClaimsActions.createRecoverySuccess({
              recovery
            })
          ),
          catchError(error =>
            of(
              ClaimsActions.createRecoveryFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  // ---------------- LOAD CLAIM RECOVERIES ----------------

  loadClaimRecoveries$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.loadClaimRecoveries),
      mergeMap(({ claimId }) =>
        this.api.getClaimRecoveries(
          claimId
        ).pipe(
          map(recoveries =>
            ClaimsActions.loadClaimRecoveriesSuccess({
              recoveries
            })
          ),
          catchError(error =>
            of(
              ClaimsActions.loadClaimRecoveriesFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  // ---------------- LOAD ALL RECOVERIES ----------------

  loadAllRecoveries$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.loadAllRecoveries),
      mergeMap(() =>
        this.api.getAllRecoveries().pipe(
          map(recoveries =>
            ClaimsActions.loadAllRecoveriesSuccess({
              recoveries
            })
          ),
          catchError(error =>
            of(
              ClaimsActions.loadAllRecoveriesFailure({
                error: this.getErrorMessage(error)
              })
            )
          )
        )
      )
    )
  );

  // ---------------- UPDATE RECOVERY ----------------

  updateRecoveryStatus$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClaimsActions.updateRecoveryStatus),
      mergeMap(({ recoveryId, request }) =>
        this.api.updateRecoveryStatus(
          recoveryId,
          request
        ).pipe(
          map(recovery =>
            ClaimsActions.updateRecoveryStatusSuccess({
              recovery
            })
          ),
          catchError(error =>
            of(
              ClaimsActions.updateRecoveryStatusFailure({
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
      error?.error?.error ||
      error?.message ||
      'Something went wrong. Please try again.'
    );
  }
}

