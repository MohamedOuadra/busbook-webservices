import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { BookingSummary } from '../booking-summary/booking-summary';
import { Trip } from '../../../trips/models/trip';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-booking-panel',
  standalone: true,
  imports: [BookingSummary, FormsModule, RouterLink],
  templateUrl: './booking-panel.html',
  styleUrl: './booking-panel.css',
})
export class BookingPanel implements OnInit {
  @Input({ required: true }) trip!: Trip;
  @Input() seatNumber: number | null = null;
  @Input() isSubmitting: boolean = false;

  @Output() startBooking = new EventEmitter<{
    fullName: string;
    phoneNumber: string;
    email: string;
  }>();

  // Informations du voyageur
  fullName = '';
  phoneNumber = '';
  email = '';

  private authService = inject(AuthService);
  private router = inject(Router);

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.fullName = user.fullName || '';
      this.email = user.email || '';
    }
  }

  onContinueBooking(): void {
    if (this.seatNumber === null) {
      return;
    }

    // Si non authentifié, rediriger vers login
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: `/reservation/${this.trip.id}` }
      });
      return;
    }

    if (!this.fullName || !this.email) {
      return;
    }

    this.startBooking.emit({
      fullName: this.fullName,
      phoneNumber: this.phoneNumber,
      email: this.email,
    });
  }
}
