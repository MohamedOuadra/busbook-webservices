import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { SettingsPage } from './settings';
import { TravelPreferencesService } from '../../../../core/services/travel-preferences.service';
import { AuthService } from '../../../../core/services/auth.service';

describe('SettingsPage', () => {
  let component: SettingsPage;
  let fixture: ComponentFixture<SettingsPage>;

  beforeEach(async () => {
    localStorage.clear();
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

  it('persists travel preferences and restores them after recreating the page', () => {
    component.defaultCity = 'Agadir';
    component.preferredSeat = 'aisle';
    component.smsNotifications = false;
    component.savePreferences();
    expect(component.saveSuccessMessage()).toContain('saved on this device');
    const restored = TestBed.createComponent(SettingsPage);
    restored.detectChanges();
    expect(restored.componentInstance.defaultCity).toBe('Agadir');
    expect(restored.componentInstance.preferredSeat).toBe('aisle');
    expect(restored.componentInstance.smsNotifications).toBe(false);
    expect(component.isSaving()).toBe(false);
  });

  it('reports storage failures without claiming success', () => {
    vi.spyOn(TestBed.inject(TravelPreferencesService), 'save').mockReturnValue(false);
    component.savePreferences();
    expect(component.saveSuccessMessage()).toBeNull();
    expect(component.saveErrorMessage()).toContain('Could not save');
    expect(component.isSaving()).toBe(false);
  });

  it('does not save old form choices into another account', () => {
    const save = vi.spyOn(TestBed.inject(TravelPreferencesService), 'save');
    TestBed.inject(AuthService).currentUser.set({ id: 2, token: '', type: '', email: 'other@example.com', fullName: 'Other', role: 'USER' });
    component.savePreferences();
    expect(save).not.toHaveBeenCalled();
    expect(component.saveErrorMessage()).toContain('account changed');
  });

  it('keeps account fields read-only and saves only preferences', () => {
    const save = vi.spyOn(TestBed.inject(TravelPreferencesService), 'save');
    component.savePreferences();
    expect(fixture.nativeElement.querySelector('#email').readOnly).toBe(true);
    expect(fixture.nativeElement.querySelector('#fullName').readOnly).toBe(true);
    expect(save.mock.calls[0][1]).not.toHaveProperty('email');
    expect(save.mock.calls[0][1]).not.toHaveProperty('password');
  });
});
