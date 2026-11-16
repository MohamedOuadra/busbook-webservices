
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Trip } from '../../../trips/models/trip';
import { TripService } from '../../../../core/services/trip';

import { BusIllustration } from '../../components/bus-illustration/bus-illustration';
import { SeatMap } from '../../components/seat-map/seat-map';
import { BookingPanel } from '../../components/booking-panel/booking-panel';

@Component({
  selector: 'app-reservation',
  imports: [BusIllustration, SeatMap, BookingPanel],
  templateUrl: './reservation.html',
  styleUrl: './reservation.css',
})
export class Reservation implements OnInit {

  selectedSeat: number | null = null;
  trip: Trip | undefined;

  constructor(
    private route: ActivatedRoute,
    private tripService: TripService
  ) {}

  ngOnInit(): void {
    const tripId = Number(this.route.snapshot.paramMap.get('tripId'));

    this.tripService.getTripById(tripId).subscribe((trip) => {
      this.trip = trip;

      console.log('Trajet sélectionné :', this.trip);
    });
  }

  onSeatSelected(seatNumber: number): void {
  this.selectedSeat = seatNumber;

  console.log('Siège sélectionné :', this.selectedSeat);
}
}
