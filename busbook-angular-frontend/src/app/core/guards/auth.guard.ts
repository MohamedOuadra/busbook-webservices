import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = localStorage.getItem('token');

  if (token) {
    return true;
  }

  // Redirige vers login en mémorisant la page demandée pour y revenir après connexion
  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }
  });
};