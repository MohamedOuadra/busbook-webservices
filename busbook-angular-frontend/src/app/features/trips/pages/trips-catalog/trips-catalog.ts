import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { TripService } from '../../../../core/services/trip';
import { Trip } from '../../models/trip';
import { TripSearch as TripSearchCriteria } from '../../models/trip-search';
import { TripSearch } from '../../components/trip-search/trip-search';
import { TripCard } from '../../components/trip-card/trip-card';

@Component({
  selector: 'app-trips-catalog',
  imports: [CommonModule, TripSearch, TripCard],
  templateUrl: './trips-catalog.html',
  styleUrl: './trips-catalog.css',
})
export class TripsCatalogPage implements OnInit {
  private readonly _trips = signal<Trip[]>([]);
  readonly isLoading = signal(false);

  get trips(): Trip[] {
    return this._trips();
  }

  set trips(value: Trip[]) {
    this._trips.set(value);
  }

  constructor(
    private tripService: TripService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadTrips();
  }

  loadTrips(): void {
    this.isLoading.set(true);
    this.tripService.getTrips().subscribe({
      next: (trips) => {
        this.trips = trips;
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  onSearch(criteria: TripSearchCriteria): void {
    this.isLoading.set(true);
    this.tripService.searchTrips(criteria).subscribe({
      next: (results) => {
        this.trips = results;
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  onTripSelected(trip: Trip): void {
    this.router.navigate(['/reservation', trip.id]);
  }
}
