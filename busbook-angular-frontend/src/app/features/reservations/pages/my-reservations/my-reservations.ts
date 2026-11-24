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
  normalizedStatus: 'CONFIRMED' | 'PENDING' | 'CANCELLED' | 'EXPIRED';
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
  readonly reservations = signal<ReservationWithTrip[]>([]);
  readonly isLoading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly activeFilter = signal<BookingFilter>('ALL');
  readonly toastMessage = signal<{ text: string; type: 'success' | 'error' } | null>(null);
  readonly cancellingId = signal<number | null>(null);

  readonly currentUser = computed(() => this.authService.currentUser());

  readonly filteredReservations = computed(() => {
    const list = this.reservations();
    const filter = this.activeFilter();

    switch (filter) {
      case 'CONFIRMED':
        return list.filter((item) => item.normalizedStatus === 'CONFIRMED');
      case 'PENDING':
        return list.filter((item) => item.normalizedStatus === 'PENDING' && !item.isExpired);
      case 'CANCELLED':
        return list.filter((item) => item.normalizedStatus === 'CANCELLED' || item.isExpired);
      default:
        return list;
    }
  });

  readonly confirmedCount = computed(
    () => this.reservations().filter((i) => i.normalizedStatus === 'CONFIRMED').length
  );
  readonly pendingCount = computed(
    () => this.reservations().filter((i) => i.normalizedStatus === 'PENDING' && !i.isExpired).length
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

  private normalizeStatus(status: string): 'CONFIRMED' | 'PENDING' | 'CANCELLED' | 'EXPIRED' {
    const upper = (status || '').toUpperCase();
    if (upper === 'CONFIRMEE' || upper === 'CONFIRMED') return 'CONFIRMED';
    if (upper === 'ANNULEE' || upper === 'CANCELLED') return 'CANCELLED';
    if (upper === 'EXPIREE' || upper === 'EXPIRED') return 'EXPIRED';
    return 'PENDING';
  }

  private showToast(text: string, type: 'success' | 'error' = 'success'): void {
    this.toastMessage.set({ text, type });
    setTimeout(() => this.toastMessage.set(null), 4000);
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
        const sorted = [...(list || [])].sort((a, b) => b.id - a.id);

        const items: ReservationWithTrip[] = sorted.map((res: ReservationResponse) => {
          const normalizedStatus = this.normalizeStatus(res.status);
          const expires = res.expiresAt ? new Date(res.expiresAt) : null;
          const isExpired =
            normalizedStatus === 'EXPIRED' ||
            (normalizedStatus === 'PENDING' && expires !== null && expires <= now);

          const item: ReservationWithTrip = { reservation: res, isExpired, normalizedStatus };

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
        this.showToast('Billet introuvable ou en cours de génération pour cette réservation.', 'error');
      },
    });
  }

  requestCancel(id: number): void {
    this.cancellingId.set(id);
  }

  abortCancel(): void {
    this.cancellingId.set(null);
  }

  confirmCancel(id: number): void {
    this.cancellingId.set(null);
    this.reservationApi.cancelReservation(id).subscribe({
      next: () => {
        this.showToast(`Réservation #${id} annulée avec succès.`, 'success');
        this.loadReservations();
      },
      error: (err) => {
        this.showToast(err.error?.message || "Impossible d'annuler cette réservation.", 'error');
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