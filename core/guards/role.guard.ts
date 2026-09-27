import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserRole } from '../constants/roles';
import { TokenService } from '../services/token.service';

export const roleGuard = (
  allowed: UserRole[]
): CanActivateFn => {
  return () => {
    const token = inject(TokenService);
    const router = inject(Router);
    const role = token.payload()?.['role'] as UserRole;

    return allowed.includes(role)
      ? true
      : router.createUrlTree(['/dashboard']);
  };
};

