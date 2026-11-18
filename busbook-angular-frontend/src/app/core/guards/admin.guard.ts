import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guard qui protège les routes admin.
 * Vérifie que l'utilisateur est connecté ET possède le rôle ADMIN.
 * Redirige vers /login si non connecté, vers / si connecté mais pas admin.
 */
export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const user = authService.currentUser();

  if (!user) {
    router.navigate(['/login'], { queryParams: { returnUrl: '/admin' } });
    return false;
  }

  const role = user.role?.toString().toUpperCase();
  if (role !== 'ADMIN' && role !== 'ROLE_ADMIN') {
    router.navigate(['/']);
    return false;
  }

  return true;
};
