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
}
