import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  
  // Récupère le token stocké (adaptez la clé si votre AuthService utilise un autre nom, ex: 'access_token')
  const token = localStorage.getItem('token');

  // Clone la requête en ajoutant le header Authorization si le token existe
  const authReq = token
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Si le token est invalide ou expiré (401), on nettoie et on redirige vers /login
      if (error.status === 401 && !req.url.includes('/auth/login')) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        router.navigate(['/login'], { queryParams: { sessionExpired: 'true' } });
      }
      return throwError(() => error);
    })
  );
};