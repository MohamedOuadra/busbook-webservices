import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-booking-summary',
  imports: [],
  templateUrl: './booking-summary.html',
  styleUrl: './booking-summary.css',
})
export class BookingSummary {
  @Input() from = 'Dhaka';
  @Input() to = 'Rangpur';
  @Input() date = '12 June 2026';
  @Input() seatNumber: number | null = null;
  @Input() price = 850;

  // État de validation des informations du voyageur
  @Input() formValid: boolean | null = false;
}