import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { TripSearch as TripSearchCriteria } from '../../models/trip-search';

@Component({
  selector: 'app-trip-search',
  imports: [FormsModule],
  templateUrl: './trip-search.html',
  styleUrl: './trip-search.css',
})
export class TripSearch {

  @Output() search = new EventEmitter<TripSearchCriteria>();

  departure = '';
  destination = '';
  date = '';

  onSearch(): void {
    const criteria: TripSearchCriteria = {
      departure: this.departure.trim(),
      destination: this.destination.trim(),
      date: this.date,
    };

    this.search.emit(criteria);
  }

  onSwap(): void {
    const temp = this.departure;
    this.departure = this.destination;
    this.destination = temp;
    if (this.departure && this.destination && this.date) {
      this.onSearch();
    }
  }

  selectRoute(dep: string, dest: string): void {
    this.departure = dep;
    this.destination = dest;
    if (!this.date) {
      this.date = '2026-11-20';
    }
    this.onSearch();
  }
}