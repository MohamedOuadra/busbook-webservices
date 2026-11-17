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

export type BookingFilter = 'ALL' | 'CONFIRMED' | 'PENDING' | 'CANCELLED';

@Component({
  selector: 'app-my-reservations',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './my-reservations.html',
  styleUrl: './my-reservations.css',
})
export class MyReservationsPage implements OnInit {
  // Reactive state signals
  readonly reservations = signal<ReservationWithTrip[]>([]);
  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly activeFilter = signal<BookingFilter>('ALL');

  // Expose current auth user
  readonly currentUser = computed(() => this.authService.currentUser());

  // Filtered reservations computed
  readonly filteredReservations = computed(() => {
    const list = this.reservations();
    const filter = this.activeFilter();

    switch (filter) {
      case 'CONFIRMED':
        return list.filter((item) => item.reservation.status === 'CONFIRMEE');
      case 'PENDING':
        return list.filter(
          (item) => item.reservation.status === 'EN_ATTENTE' && !item.isExpired
        );
      case 'CANCELLED':
        return list.filter(
          (item) => item.reservation.status === 'ANNULEE' || item.isExpired
        );
      default:
        return list;
    }
  });

  // Summary counts
  readonly confirmedCount = computed(
    () => this.reservations().filter((i) => i.reservation.status === 'CONFIRMEE').length
  );
  readonly pendingCount = computed(
    () => this.reservations().filter((i) => i.reservation.status === 'EN_ATTENTE' && !i.isExpired).length
  );

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

  setFilter(filter: BookingFilter): void {
    this.activeFilter.set(filter);
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
            this.reservations.update((r) => [...r]);
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

  formatDate(dateStr?: string): string {
    if (!dateStr) return '—';
    try {
      const d = new Date(dateStr);
      return d.toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
      });
    } catch {
      return dateStr;
    }
  }

  formatTime(dateStr?: string): string {
    if (!dateStr) return '—';
    try {
      const d = new Date(dateStr);
      return d.toLocaleTimeString('fr-FR', {
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return dateStr;
    }
  }
}
