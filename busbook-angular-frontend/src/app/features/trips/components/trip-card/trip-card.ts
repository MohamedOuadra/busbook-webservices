import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DatePipe } from '@angular/common';

import { Trip } from '../../models/trip';

@Component({
  selector: 'app-trip-card',
  imports: [DatePipe],
  templateUrl: './trip-card.html',
  styleUrl: './trip-card.css',
})
export class TripCard {

  @Input({ required: true }) trip!: Trip;

  @Output() tripSelected = new EventEmitter<Trip>();

  selectTrip(): void {
    this.tripSelected.emit(this.trip);
  }

  get duration(): string {
    if (!this.trip?.dateDepart || !this.trip?.dateArrivee) return '6h 00m';
    const dep = new Date(this.trip.dateDepart).getTime();
    const arr = new Date(this.trip.dateArrivee).getTime();
    const diffMs = arr - dep;
    if (isNaN(diffMs) || diffMs <= 0) return '6h 00m';
    const totalMinutes = Math.floor(diffMs / (1000 * 60));
    const hours = Math.floor(totalMinutes / 60);
    const minutes = totalMinutes % 60;
    return `${hours}h ${minutes > 0 ? minutes + 'm' : '00m'}`;
  }
}
