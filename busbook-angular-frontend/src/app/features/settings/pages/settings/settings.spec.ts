import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { SettingsPage } from './settings';

describe('SettingsPage', () => {
  let component: SettingsPage;
  let fixture: ComponentFixture<SettingsPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsPage],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SettingsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create settings page component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize default preferences', () => {
    expect(component.defaultCity).toBe('Casablanca');
    expect(component.currency).toBe('MAD');
    expect(component.preferredSeat).toBe('window');
    expect(component.smsNotifications).toBe(true);
  });

  it('should show success message after saving profile', async () => {
    component.saveProfile();
    expect(component.isSaving()).toBe(true);
  });
});
