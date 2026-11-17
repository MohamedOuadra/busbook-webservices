
import { Component, OnInit, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

import { TripSearch } from '../../../trips/components/trip-search/trip-search';
import { TripCard } from '../../../trips/components/trip-card/trip-card';

import { TripService } from '../../../../core/services/trip';
import { Trip } from '../../../trips/models/trip';
import { TripSearch as TripSearchCriteria } from '../../../trips/models/trip-search';

@Component({
  selector: 'app-home',
  imports: [
    TripSearch,
    TripCard,
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {

  private readonly _trips = signal<Trip[]>([]);
  readonly isLoading = signal(false);

  get trips(): Trip[] {
    return this._trips();
  }

  set trips(value: Trip[]) {
    this._trips.set(value);
  }

  private tripService = inject(TripService);
  private router = inject(Router);

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

  onTripSearch(criteria: TripSearchCriteria): void {
    this.isLoading.set(true);
    this.tripService.searchTrips(criteria).subscribe({
      next: (trips) => {
        this.trips = trips;
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

  resetFilters(): void {
    this.loadTrips();
  }
}
