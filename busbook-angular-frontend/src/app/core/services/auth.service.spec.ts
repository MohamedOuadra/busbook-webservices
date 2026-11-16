import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';
import { AuthResponse } from '../models/auth.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpTesting: HttpTestingController;

  const mockResponse: AuthResponse = {
    token: 'jwt-test-token',
    type: 'Bearer',
    id: 1,
    email: 'user@busbook.ma',
    fullName: 'Test User',
    role: 'USER'
  };

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        AuthService
      ]
    });

    service = TestBed.inject(AuthService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should register and store session', () => {
    service.register({
      fullName: 'Test User',
      email: 'user@busbook.ma',
      password: 'password123'
    }).subscribe(res => {
      expect(res.token).toBe('jwt-test-token');
      expect(service.isLoggedIn()).toBe(true);
      expect(service.getToken()).toBe('jwt-test-token');
      expect(service.getCurrentUser()?.email).toBe('user@busbook.ma');
    });

    const req = httpTesting.expectOne(`${environment.authApiUrl}/auth/register`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should login and store session', () => {
    service.login({
      email: 'user@busbook.ma',
      password: 'password123'
    }).subscribe(res => {
      expect(res.token).toBe('jwt-test-token');
      expect(service.isLoggedIn()).toBe(true);
    });

    const req = httpTesting.expectOne(`${environment.authApiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should logout and clear session', () => {
    service.saveSession(mockResponse);
    expect(service.isLoggedIn()).toBe(true);

    service.logout();
    expect(service.isLoggedIn()).toBe(false);
    expect(service.getToken()).toBeNull();
    expect(service.getCurrentUser()).toBeNull();
  });
});
