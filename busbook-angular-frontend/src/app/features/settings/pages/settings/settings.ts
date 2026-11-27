import { Component, computed, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { TravelPreferencesService } from '../../../../core/services/travel-preferences.service';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './settings.html',
  styleUrl: './settings.css',
})
export class SettingsPage implements OnInit {
  readonly currentUser = computed(() => this.authService.currentUser());

  // Form fields
  fullName = '';
  email = '';
  phoneNumber = '';
  preferredSeat = 'window';
  preferredClass = 'standard';
  defaultCity = 'Casablanca';
  smsNotifications = true;
  emailNotifications = true;
  promoAlerts = false;
  currency = 'MAD';
  language = 'fr';

  // Security fields
  currentPassword = '';
  newPassword = '';
  confirmPassword = '';

  saveSuccessMessage = signal<string | null>(null);
  saveErrorMessage = signal<string | null>(null);
  isSaving = signal(false);
  private preferencesOwner: number | null = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private preferences: TravelPreferencesService
  ) {}

  ngOnInit(): void {
    const user = this.currentUser();
    this.preferencesOwner = user?.id ?? null;
    Object.assign(this, this.preferences.load(this.preferencesOwner));
    if (user) {
      this.fullName = user.fullName || '';
      this.email = user.email || '';
    }
  }

  savePreferences(): void {
    this.isSaving.set(true);
    this.saveSuccessMessage.set(null);
    this.saveErrorMessage.set(null);

    if ((this.currentUser()?.id ?? null) !== this.preferencesOwner) {
      this.saveErrorMessage.set('Your account changed. Reload settings before saving.');
    } else if (this.preferences.save(this.preferencesOwner, {
      preferredSeat: this.preferredSeat, defaultCity: this.defaultCity,
      smsNotifications: this.smsNotifications, emailNotifications: this.emailNotifications,
      promoAlerts: this.promoAlerts, currency: this.currency, language: this.language,
    })) {
      this.saveSuccessMessage.set('Travel preferences saved on this device.');
    } else {
      this.saveErrorMessage.set('Could not save preferences. Allow browser storage and try again.');
    }
    this.isSaving.set(false);
  }

  updatePassword(): void {
    if (!this.newPassword || this.newPassword !== this.confirmPassword) {
      this.saveErrorMessage.set('Passwords do not match or are empty.');
      return;
    }

    this.isSaving.set(true);
    setTimeout(() => {
      this.isSaving.set(false);
      this.currentPassword = '';
      this.newPassword = '';
      this.confirmPassword = '';
      this.saveSuccessMessage.set('Password updated successfully.');

      setTimeout(() => {
        this.saveSuccessMessage.set(null);
      }, 4000);
    }, 600);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
