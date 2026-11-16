import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ReservationApiService } from '../../../../core/services/reservation.service';
import { TicketApiService } from '../../../../core/services/ticket.service';
import { TripService } from '../../../../core/services/trip';
import { ReservationResponse } from '../../../../core/models/reservation.model';
import { Trip } from '../../../trips/models/trip';

interface ReservationWithTrip {
  reservation: ReservationResponse;
  trip?: Trip;
  isExpired: boolean;
}

@Component({
  selector: 'app-my-reservations',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './my-reservations.html',
  styleUrl: './my-reservations.css',
})
export class MyReservationsPage implements OnInit {
  travelerId = 1;
  reservations: ReservationWithTrip[] = [];
  isLoading = false;
  errorMessage: string | null = null;

  constructor(
    private reservationApi: ReservationApiService,
    private ticketApi: TicketApiService,
    private tripService: TripService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadReservations();
  }

  loadReservations(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.reservationApi.getReservationsByTravelerId(this.travelerId).subscribe({
      next: (list: ReservationResponse[]) => {
        const now = new Date();
        this.reservations = list.map((res: ReservationResponse) => {
          const expires = res.expiresAt ? new Date(res.expiresAt) : null;
          const isExpired = res.status === 'EXPIREE' || (res.status === 'EN_ATTENTE' && expires !== null && expires <= now);

          const item: ReservationWithTrip = {
            reservation: res,
            isExpired,
          };

          this.tripService.getTripById(res.tripId).subscribe((trip) => {
            item.trip = trip;
          });

          return item;
        });

        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Unable to load reservations for traveler #' + this.travelerId;
      }
    });
  }

  viewTicket(reservationId: number): void {
    this.ticketApi.getTicketByReservation(reservationId).subscribe({
      next: (ticket) => {
        this.router.navigate(['/ticket', ticket.ticketNumber]);
      },
      error: () => {
        alert('Ticket not yet generated for this reservation.');
      }
    });
  }

  cancelReservation(id: number): void {
    if (!confirm('Are you sure you want to cancel this reservation?')) {
      return;
    }

    this.reservationApi.cancelReservation(id).subscribe({
      next: () => {
        this.loadReservations();
      },
      error: (err) => {
        alert(err.error?.message || 'Error cancelling reservation.');
      }
    });
  }
}
