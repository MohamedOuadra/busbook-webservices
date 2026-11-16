
import { Component, OnInit } from '@angular/core';
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

  trips: Trip[] = [];

  constructor(
    private tripService: TripService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.tripService.getTrips().subscribe((trips) => {
      this.trips = trips;
      console.log('Trajets récupérés :', this.trips);
    });
  }

  onTripSearch(criteria: TripSearchCriteria): void {
    this.tripService.searchTrips(criteria).subscribe((trips) => {
      this.trips = trips;
      console.log('Résultats de recherche :', this.trips);
    });
  }

  onTripSelected(trip: Trip): void {
    this.router.navigate(['/reservation', trip.id]);
  }
}
