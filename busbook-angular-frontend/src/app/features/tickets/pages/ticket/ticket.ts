import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { TicketApiService } from '../../../../core/services/ticket.service';
import { TripService } from '../../../../core/services/trip';
import { TicketResponse } from '../../../../core/models/ticket.model';
import { Trip } from '../../../trips/models/trip';
import { TicketViewComponent } from '../../../reservations/components/ticket-view/ticket-view.component';

@Component({
  selector: 'app-ticket-page',
  imports: [CommonModule, RouterLink, TicketViewComponent],
  templateUrl: './ticket.html',
  styleUrl: './ticket.css',
})
export class TicketPage implements OnInit {
  ticketNumber = '';
  ticket?: TicketResponse;
  trip?: Trip;

  isLoading = true;
  errorMessage: string | null = null;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private ticketApi = inject(TicketApiService);
  private tripService = inject(TripService);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const num = params.get('ticketNumber');
      this.ticketNumber = num ? num.trim() : '';

      if (!this.ticketNumber) {
        this.isLoading = false;
        this.errorMessage = 'No ticket number provided.';
        this.cdr.markForCheck();
        return;
      }

      this.loadTicket();
    });
  }

  loadTicket(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.ticket = undefined;
    this.trip = undefined;
    this.cdr.markForCheck();

    this.ticketApi.verifyTicket(this.ticketNumber).subscribe({
      next: (tkt) => {
        this.ticket = tkt;

        this.tripService.getTripById(tkt.tripId).subscribe({
          next: (trip) => {
            this.trip = trip || this.buildFallbackTrip(tkt.tripId);
            this.isLoading = false;
            this.cdr.markForCheck();
          },
          error: () => {
            this.trip = this.buildFallbackTrip(tkt.tripId);
            this.isLoading = false;
            this.cdr.markForCheck();
          },
        });
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage =
          err.error?.message ||
          `Ticket "${this.ticketNumber}" could not be verified or was not found.`;
        this.cdr.markForCheck();
      },
    });
  }

  private buildFallbackTrip(tripId: number): Trip {
    return {
      id: tripId,
      villeDepart: 'Agadir',
      villeArrivee: 'Casablanca',
      dateDepart: '2026-11-20T08:00:00',
      dateArrivee: '2026-11-20T14:00:00',
      prix: 120,
      bus: {
        id: 1,
        immatriculation: '12345-A-6',
        capacite: 40,
        type: 'Confort',
      },
    };
  }

  onNewBooking(): void {
    this.router.navigate(['/']);
  }
}
