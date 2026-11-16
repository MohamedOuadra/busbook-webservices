import { Component, OnInit, computed, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { ReservationApiService } from '../../../../core/services/reservation.service';
import { TicketApiService } from '../../../../core/services/ticket.service';
import { TripService } from '../../../../core/services/trip';
import { AuthService } from '../../../../core/services/auth.service';
import { ReservationResponse } from '../../../../core/models/reservation.model';
import { Trip } from '../../../trips/models/trip';

export interface ReservationWithTrip {
  reservation: ReservationResponse;
  trip?: Trip;
  isExpired: boolean;
}

@Component({
  selector: 'app-my-reservations',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './my-reservations.html',
  styleUrl: './my-reservations.css',
})
export class MyReservationsPage implements OnInit {
  // Signals for reactive state (zoneless compatible)
  readonly reservations = signal<ReservationWithTrip[]>([]);
  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  // Expose auth signal directly so the template reacts to login/logout
  readonly currentUser = computed(() => this.authService.currentUser());

  constructor(
    private reservationApi: ReservationApiService,
    private ticketApi: TicketApiService,
    private tripService: TripService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.currentUser()) {
      this.loadReservations();
    }
  }

  loadReservations(): void {
    const user = this.currentUser();
    if (!user) {
      this.reservations.set([]);
      this.isLoading.set(false);
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.reservationApi.getReservationsByTravelerId(user.id).subscribe({
      next: (list: ReservationResponse[]) => {
        const now = new Date();
        const items: ReservationWithTrip[] = (list || []).map((res: ReservationResponse) => {
          const expires = res.expiresAt ? new Date(res.expiresAt) : null;
          const isExpired =
            res.status === 'EXPIREE' ||
            (res.status === 'EN_ATTENTE' && expires !== null && expires <= now);

          const item: ReservationWithTrip = { reservation: res, isExpired };

          this.tripService.getTripById(res.tripId).subscribe((trip) => {
            item.trip = trip;
            // Trigger signal update so the template re-renders with trip data
            this.reservations.update(r => [...r]);
          });

          return item;
        });

        this.reservations.set(items);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        this.errorMessage.set('Unable to load reservations for your account.');
      },
    });
  }

  viewTicket(reservationId: number): void {
    this.ticketApi.getTicketByReservation(reservationId).subscribe({
      next: (ticket) => {
        this.router.navigate(['/ticket', ticket.ticketNumber]);
      },
      error: () => {
        alert('Ticket not yet generated for this reservation.');
      },
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
      },
    });
  }
}
