import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest, UserProfile } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly baseUrl = `${environment.authApiUrl}/auth`;
  private readonly TOKEN_KEY = 'busbook_jwt_token';
  private readonly USER_KEY = 'busbook_user_data';

  // Signal pour l'état réactif de l'utilisateur connecté
  currentUser = signal<AuthResponse | null>(this.getStoredUser());

  constructor(private http: HttpClient) {
    if (this.isLoggedIn()) {
      this.refreshUser();
    }
  }

  refreshUser(): void {
    if (!this.getToken()) return;
    this.getProfile().subscribe({
      next: (profile) => {
        const current = this.currentUser();
        if (current) {
          const updated: AuthResponse = {
            ...current,
            role: profile.role,
            fullName: profile.fullName || current.fullName,
            email: profile.email || current.email
          };
          this.saveSession(updated);
        }
      },
      error: () => {
        // En cas d'erreur de token expiré par exemple
      }
    });
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/register`, request).pipe(
      tap(response => this.saveSession(response))
    );
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, request).pipe(
      tap(response => this.saveSession(response))
    );
  }

  getProfile(): Observable<UserProfile> {
    const token = this.getToken();
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.http.get<UserProfile>(`${this.baseUrl}/me`, { headers });
  }

  saveSession(auth: AuthResponse): void {
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.setItem(this.TOKEN_KEY, auth.token);
      localStorage.setItem(this.USER_KEY, JSON.stringify(auth));
    }
    this.currentUser.set(auth);
  }

  getToken(): string | null {
    if (typeof window !== 'undefined' && window.localStorage) {
      return localStorage.getItem(this.TOKEN_KEY);
    }
    return null;
  }

  getCurrentUser(): AuthResponse | null {
    return this.currentUser();
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.USER_KEY);
    }
    this.currentUser.set(null);
  }

  private getStoredUser(): AuthResponse | null {
    if (typeof window !== 'undefined' && window.localStorage) {
      const data = localStorage.getItem(this.USER_KEY);
      if (data) {
        try {
          return JSON.parse(data) as AuthResponse;
        } catch {
          return null;
        }
      }
    }
    return null;
  }
}
