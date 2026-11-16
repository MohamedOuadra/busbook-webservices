import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { App } from './app';
import { routes } from './app.routes';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter(routes),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render router-outlet', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('router-outlet')).toBeTruthy();
  });

  it('should navigate to root / and activate MainLayout with Home', async () => {
    const router = TestBed.inject(Router);
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    await router.navigate(['/']);
    await fixture.whenStable();

    expect(router.url).toBe('/');
  });

  it('should redirect /trips to /', async () => {
    const router = TestBed.inject(Router);
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    await router.navigate(['/trips']);
    await fixture.whenStable();

    expect(router.url).toBe('/');
  });

  it('should navigate to /login and activate AuthLayout', async () => {
    const router = TestBed.inject(Router);
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    await router.navigate(['/login']);
    await fixture.whenStable();

    expect(router.url).toBe('/login');
  });
});
