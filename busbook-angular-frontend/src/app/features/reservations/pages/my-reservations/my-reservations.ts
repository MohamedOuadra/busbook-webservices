import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { ReservationApiService } from '../../../../core/services/reservation.service';
import { TicketApiService } from '../../../../core/services/ticket.service';
import { TripService } from '../../../../core/services/trip';
import { AuthService } from '../../../../core/services/auth.service';
import { ReservationResponse } from '../../../../core/models/reservation.model';
import { AuthResponse } from '../../../../core/models/auth.model';
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
  reservations: ReservationWithTrip[] = [];
  isLoading = false;
  errorMessage: string | null = null;
  currentUser: AuthResponse | null = null;

  constructor(
    private reservationApi: ReservationApiService,
    private ticketApi: TicketApiService,
    private tripService: TripService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser) {
      this.loadReservations();
    } else {
      this.isLoading = false;
      this.cdr.markForCheck();
    }
  }

  loadReservations(): void {
    this.currentUser = this.authService.getCurrentUser();
    if (!this.currentUser) {
      this.reservations = [];
      this.isLoading = false;
      this.cdr.markForCheck();
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.cdr.markForCheck();

    this.reservationApi.getReservationsByTravelerId(this.currentUser.id).subscribe({
      next: (list: ReservationResponse[]) => {
        const now = new Date();
        this.reservations = (list || []).map((res: ReservationResponse) => {
          const expires = res.expiresAt ? new Date(res.expiresAt) : null;
          const isExpired =
            res.status === 'EXPIREE' ||
            (res.status === 'EN_ATTENTE' && expires !== null && expires <= now);

          const item: ReservationWithTrip = {
            reservation: res,
            isExpired,
          };

          this.tripService.getTripById(res.tripId).subscribe((trip) => {
            item.trip = trip;
            this.cdr.markForCheck();
          });

          return item;
        });

        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Unable to load reservations for your account.';
        this.cdr.markForCheck();
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
