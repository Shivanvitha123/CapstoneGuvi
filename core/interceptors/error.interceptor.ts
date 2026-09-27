import {
  HttpErrorResponse,
  HttpInterceptorFn
} from '@angular/common/http';

import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notification = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        notification.error('Your session has expired.');
      } else if (error.status === 403) {
        notification.error('You do not have permission for this action.');
      } else if (error.status >= 500) {
        notification.error('Something went wrong on the server.');
      } else {
        notification.error(
          error.error?.message ?? 'Request failed.'
        );
      }

      return throwError(() => error);
    })
  );
};
