
import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';

import * as NotificationActions from './notification.actions';
import { NotificationApiService } from '../service/notification-api.service';

@Injectable()
export class NotificationEffects {

  private readonly actions$ = inject(Actions);
  private readonly api = inject(NotificationApiService);

  loadNotifications$ = createEffect(() =>
    this.actions$.pipe(
      ofType(NotificationActions.loadNotifications),

      switchMap(() =>
        this.api.getMine().pipe(

          map(notifications =>
            NotificationActions.loadNotificationsSuccess({
              notifications
            })
          ),

          catchError(error =>
            of(
              NotificationActions.loadNotificationsFailure({
                error:
                  error?.error?.message ??
                  'Unable to load notifications'
              })
            )
          )
        )
      )
    )
  );

  markAsRead$ = createEffect(() =>
    this.actions$.pipe(
      ofType(NotificationActions.markNotificationRead),

      switchMap(({ id }) =>
        this.api.markAsRead(id).pipe(

          map(notification =>
            NotificationActions.markNotificationReadSuccess({
              notification
            })
          ),

          catchError(error =>
            of(
              NotificationActions.markNotificationReadFailure({
                error:
                  error?.error?.message ??
                  'Unable to update notification'
              })
            )
          )
        )
      )
    )
  );
}

