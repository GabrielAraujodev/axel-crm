import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const role = auth.currentUser?.role;
  if (auth.isAuthenticated && (role === 'ADMIN' || role === 'SUPER_ADMIN')) {
    return true;
  }
  return router.createUrlTree(['/dashboard']);
};
