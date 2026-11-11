import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { ApiService } from '../services/api.service';

/** Solo deja pasar si hay un usuario con sesión iniciada. */
export const sesionGuard: CanActivateFn = () => {
  const api = inject(ApiService);
  const router = inject(Router);
  return api.usuario() ? true : router.createUrlTree(['/login']);
};
