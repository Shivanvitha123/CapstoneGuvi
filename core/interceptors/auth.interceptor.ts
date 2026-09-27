import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { TokenService } from '../services/token.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Do not attach tokens to login or registration requests.
  const isPublicAuthEndpoint =
    req.url.includes('/api/auth/login') ||
    req.url.includes('/api/auth/register');

  if (isPublicAuthEndpoint) {
    return next(req);
  }

  const token = inject(TokenService).get();

  if (!token) {
    return next(req);
  }

  return next(
    req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    })
  );
};