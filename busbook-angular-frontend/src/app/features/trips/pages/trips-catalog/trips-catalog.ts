import { Component, OnInit } from '@angular/core';
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
  trips: Trip[] = [];

  constructor(
    private tripService: TripService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.tripService.getTrips().subscribe((trips) => {
      this.trips = trips;
    });
  }

  onSearch(criteria: TripSearchCriteria): void {
    this.tripService.searchTrips(criteria).subscribe((results) => {
      this.trips = results;
    });
  }

  onTripSelected(trip: Trip): void {
    this.router.navigate(['/reservation', trip.id]);
  }
}
