import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { TokenService } from './token.service';

export const authGuard: CanActivateFn = () => {
  const router = inject(Router);
  const tokenService = inject(TokenService);

  const hasToken = tokenService.hasToken();

  if (!hasToken) {
    router.navigateByUrl('/auth');
    return false;
  }
  return true;
};
