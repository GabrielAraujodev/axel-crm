import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Attaches X-Tenant-Id header to every outgoing API request
 * for multi-tenant organization isolation.
 */
export const tenantInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const tenantId = auth.tenantId;

  if (tenantId) {
    const cloned = req.clone({
      setHeaders: { 'X-Tenant-Id': tenantId }
    });
    return next(cloned);
  }

  return next(req);
};
