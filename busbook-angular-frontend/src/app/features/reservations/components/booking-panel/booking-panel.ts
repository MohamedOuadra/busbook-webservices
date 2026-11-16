
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { BookingSummary } from '../booking-summary/booking-summary';
import { Trip } from '../../../trips/models/trip';

@Component({
  selector: 'app-booking-panel',
  imports: [BookingSummary, FormsModule],
  templateUrl: './booking-panel.html',
  styleUrl: './booking-panel.css',
})
export class BookingPanel {

  @Input({ required: true }) trip!: Trip;
  @Input() seatNumber: number | null = null;

  // Informations du voyageur
  fullName = '';
  phoneNumber = '';
  email = '';

  onContinueBooking(): void {
    if (this.seatNumber === null) {
      return;
    }

    console.log('Selected trip:', this.trip);
    console.log('Selected seat:', this.seatNumber);
  }
}
