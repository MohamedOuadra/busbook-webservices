import { Component, computed, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';

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
  phoneNumber = '+212 600-000000';
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

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.currentUser();
    if (user) {
      this.fullName = user.fullName || '';
      this.email = user.email || '';
    }
  }

  saveProfile(): void {
    this.isSaving.set(true);
    this.saveSuccessMessage.set(null);
    this.saveErrorMessage.set(null);

    setTimeout(() => {
      this.isSaving.set(false);
      this.saveSuccessMessage.set('Your preferences and profile details have been saved successfully.');

      setTimeout(() => {
        this.saveSuccessMessage.set(null);
      }, 4000);
    }, 600);
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
