import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterPage } from './register';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

describe('RegisterPage', () => {
  let component: RegisterPage;
  let fixture: ComponentFixture<RegisterPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterPage],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate matching passwords', () => {
    component.form.fullName = 'Test User';
    component.form.email = 'test@example.com';
    component.form.password = '123456';
    component.confirmPassword = 'different';

    component.onSubmit();
    expect(component.errorMessage).toBe('Passwords do not match.');
  });
});
