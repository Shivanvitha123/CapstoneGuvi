import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../services/token.service';

export const authGuard: CanActivateFn = () => {
  const token = inject(TokenService);
  const router = inject(Router);

  return token.hasToken()
    ? true
    : router.createUrlTree(['/login']);
};
