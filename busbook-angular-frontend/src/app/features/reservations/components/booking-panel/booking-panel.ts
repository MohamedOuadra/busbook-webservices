import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BookingSummary } from '../booking-summary/booking-summary';
import { Trip } from '../../../trips/models/trip';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-booking-panel',
  imports: [BookingSummary, FormsModule],
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

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.fullName = user.fullName || '';
      this.email = user.email || '';
    }
  }

  onContinueBooking(): void {
    if (this.seatNumber === null || !this.fullName || !this.email) {
      return;
    }

    this.startBooking.emit({
      fullName: this.fullName,
      phoneNumber: this.phoneNumber,
      email: this.email,
    });
  }
}
