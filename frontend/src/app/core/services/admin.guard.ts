import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenService } from './token.service';

// we use this guard to prevent regular users to even access this route,
// even though the backend will also check for admin role if an endpoint is accessed
export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const tokenService = inject(TokenService);

  if (!tokenService.hasToken()) {
    router.navigateByUrl('/auth');
    return false;
  }

  if (!tokenService.isAdmin()) {
    router.navigateByUrl('/');
    return false;
  }

  return true;
};
