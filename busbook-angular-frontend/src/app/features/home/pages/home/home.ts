
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
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
    FormsModule,
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {

  private readonly _trips = signal<Trip[]>([]);
  readonly isLoading = signal(false);
  readonly sortOrder = signal('default');
  readonly maxPrice = signal<number | null>(null);
  readonly busType = signal('');
  readonly busTypes = computed(() => [...new Set(this._trips().map(trip => trip.bus.type))].sort());
  readonly visibleTrips = computed(() => {
    const maximum = this.maxPrice();
    const trips = this._trips().filter(trip =>
      (maximum === null || !Number.isFinite(maximum) || maximum < 0 || trip.prix <= maximum) &&
      (!this.busType() || trip.bus.type === this.busType())
    );
    switch (this.sortOrder()) {
      case 'price-asc': return trips.sort((a, b) => a.prix - b.prix);
      case 'price-desc': return trips.sort((a, b) => b.prix - a.prix);
      case 'departure': return trips.sort((a, b) => Date.parse(a.dateDepart) - Date.parse(b.dateDepart));
      default: return trips;
    }
  });

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
    this.clearResultFilters();
    this.sortOrder.set('default');
    this.loadTrips();
  }

  clearResultFilters(): void {
    this.maxPrice.set(null);
    this.busType.set('');
  }
}
